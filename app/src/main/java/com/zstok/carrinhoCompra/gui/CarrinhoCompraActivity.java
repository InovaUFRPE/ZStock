package com.zstok.carrinhoCompra.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.carrinhoCompra.dominio.CarrinhoCompra;
import com.zstok.carrinhoCompra.negocio.CarrinhoCompraServices;
import com.zstok.historico.dominio.Historico;
import com.zstok.historico.gui.MainHistoricoPessoaFisicaActivity;
import com.zstok.historico.negocio.HistoricoServices;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.negociacao.gui.MainNegociacaoActivity;
import com.zstok.negociacao.negocio.NegociacaoServices;
import com.zstok.perfil.gui.PerfilPessoaFisicaActivity;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaFisica.gui.MainPessoaFisicaActivity;
import com.zstok.produto.adapter.ProdutoListHolder;
import com.zstok.produto.dominio.Produto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class CarrinhoCompraActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerViewItens;

    private TextView tvTotalCardViewItemCompra;
    private TextView tvNomeUsuarioNavHeader;
    private TextView tvEmailUsuarioNavHeader;
    private CircleImageView cvNavHeaderPessoa;
    private NavigationView navigationView;

    private AlertDialog alertaSair;

    private FirebaseUser user;

    private VerificaConexao verificaConexao;

    private double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho_compra);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Resgatando usuário atual do usuário atual
        user = FirebaseController.getFirebaseAuthentication().getCurrentUser();

        //Instanciando view
        tvTotalCardViewItemCompra = findViewById(R.id.tvTotalCardViewItemCompra);
        Button btnFinalizarCompra = findViewById(R.id.btnComprar);
        Button btnNegociarCompra = findViewById(R.id.btnNegociarCompra);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Instanciando recyler view
        recyclerViewItens = findViewById(R.id.recyclerItensCarrinho);
        recyclerViewItens.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CarrinhoCompraActivity.this);
        recyclerViewItens.setLayoutManager(layoutManager);

        //Instanciando views do navigation header (menu lateral)
        instanciandoView();

        //Resgatando informações do menu lateral
        setDadosMenuLateral();

        //Resgatando total do carrinho
        resgatarTotal();

        //Inicializando o objeto da classe VerificaConexao
        verificaConexao = new VerificaConexao(this);

        //Preenchendo recyclerView
        criarAdapter();

        //Verifica itens excluidos enquanto usuário estava off
        verificarItensRemovidos();
        atualizarTotalCarrinho();


        FirebaseController.getFirebase().child("produto").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                atualizarCarrinhoCompra(dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                verificarItensRemovidos();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Evento iniciar negociação
        btnNegociarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificaConexao.isConected()){
                    if (adapter.getItemCount() > 0) {
                        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                geraNegociacao(dataSnapshot);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_compra_vazia));
                    }
                }else {
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_conexao_falha));
                }
            }
        });
        //Evento finalizar compra
        btnFinalizarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()) {
                    if (adapter.getItemCount() > 0) {
                        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                verificaCompra(dataSnapshot);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else {
                        Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_compra_vazia));
                    }
                }else {
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_conexao_falha));
                }
            }
        });
        //Evento de click para interação com o menu lateral
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_meu_perfil_pessoa_fisica:
                        abrirTelaPerfilPessoaFisicaActivity();
                        return true;
                    case R.id.nav_produtos_pessoa_fisica:
                        abrirTelaMainPessoaFisicaActivity();
                        return true;
                    case R.id.nav_negociacao_pessoa_fisica:
                        Helper.criarToast(getApplicationContext(), "Em construção...");
                        return true;
                    case R.id.nav_meu_historico_pessoa_fisica:
                        abrirTelaMainHistoricoPessoaFisicaActivity();
                        return true;
                    case R.id.nav_sair:
                        sair();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
    //Método que carrega nome e email do usuário e seta nas views do menu lateral
    private void setDadosMenuLateral(){
        if (user.getPhotoUrl() != null){
            Glide.with(this).load(user.getPhotoUrl()).into(cvNavHeaderPessoa);
        }else {
            cvNavHeaderPessoa.setImageResource(R.drawable.ic_sem_foto);
        }
        tvNomeUsuarioNavHeader.setText(user.getDisplayName());
        tvEmailUsuarioNavHeader.setText(user.getEmail());
    }
    //Método que instancia as views
    private void instanciandoView(){
        View headerView = navigationView.getHeaderView(0);
        tvNomeUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderNome);
        tvEmailUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderEmail);
        cvNavHeaderPessoa = headerView.findViewById(R.id.cvNavHeaderPessoa);
    }
    //Verificando quantidade da compra
    private void verificaCompra(DataSnapshot dataSnapshot){
       if (verificaQuantidade(dataSnapshot)){
           reduzirQuantidade(dataSnapshot);
           //HistoricoServices.adicionarHistorico(dataSnapshot);
           geraHistorico(separadorCarrinhoCompra(dataSnapshot));
           Helper.criarToast(getApplicationContext(),getString(R.string.zs_compra_realizada_sucesso));
           tvTotalCardViewItemCompra.setText("");
           CarrinhoCompraServices.limparCarrinho();
       }
    }
    //Verificando se a quantidade solicitada pelo usuário está disponível em estoque
    private boolean verificaQuantidade(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> produtosCarrinho = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
        //Validando quantidade
        for(DataSnapshot itemSnapshot: produtosCarrinho){
            ItemCompra itemCompra = itemSnapshot.getValue(ItemCompra.class);
            Produto produtoCompra = dataSnapshot.child("produto").child(itemCompra.getIdProduto()).getValue(Produto.class);
            if (itemCompra.getQuantidade() > produtoCompra.getQuantidadeEstoque() ) {
                Helper.criarToast(getApplicationContext(), "Quantidade de " + produtoCompra.getNome() + " indisponível!");
                return false;
            }
        }
        return true;
    }
    //Método que seleciona os itens do carrinho e decresce do estoque das respectivas empresas responsáveis pelos itens
    private void reduzirQuantidade(DataSnapshot dataSnapshot){
        Iterable<DataSnapshot> produtosCarrinho = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
        for(DataSnapshot itensCompraCarrinhoUser: produtosCarrinho){
            ItemCompra itemCompra = itensCompraCarrinhoUser.getValue(ItemCompra.class);
            Produto produto = dataSnapshot.child("produto").child(itemCompra.getIdProduto()).getValue(Produto.class);
            int quantidadeNova = produto.getQuantidadeEstoque() - itemCompra.getQuantidade();
            produto.setQuantidadeEstoque(quantidadeNova);
            CarrinhoCompraServices.reduzirQuantidade(produto);
        }
    }
/*    private void retirarItemInativo(){
                FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()) != null) {
                            Iterable<DataSnapshot> itensCarrinho = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
                            for (DataSnapshot dataSnapshotChild : itensCarrinho) {
                                String idProduto = dataSnapshotChild.child("idProduto").getValue(String.class);
                                if (dataSnapshot.child("produtoExcluido").child(idProduto).exists()){
                                    FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").child(dataSnapshotChild.getKey()).setValue(null);
                                    break;
                                }
                            }
                        }
                    }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/
/*    //Removendo produto carrinho compra
    private void removerProdutoCarrinho(final String idProdutoRemovido){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).exists()) {
                    Double total = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").getValue(Double.class);
                    Produto produto = dataSnapshot.child("produtoExcluido").child(idProdutoRemovido).getValue(Produto.class);
                    excluirItemCarrinho(dataSnapshot, produto, total, idProdutoRemovido);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Método que exclui os itens do carrinho
    private void excluirItemCarrinho(DataSnapshot dataSnapshot, Produto produto, double total, String idProdutoRemovido) {
        Iterable<DataSnapshot> itensCarrinho = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
        for (DataSnapshot dataSnapshotChild : itensCarrinho) {
            ItemCompra itemCompra = dataSnapshotChild.getValue(ItemCompra.class);
            if (produto.getIdProduto().equals(idProdutoRemovido)) {
                FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").child(dataSnapshotChild.getKey()).setValue(null);
                inserirTotal(produto, itemCompra, total);
                resgatarTotal();
                criarAdapter();
                break;
            }
        }
    }*/
    //Método que atualiza carrinho compra
    private void atualizarCarrinhoCompra(final String idProdutoAlterado){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).exists())) {
                    Produto produto = dataSnapshot.child("produto").child(idProdutoAlterado).getValue(Produto.class);
                    Double total = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").getValue(Double.class);
                    if (produto != null) {
                        resgatarItensComprasCarrinho(dataSnapshot, produto, total, idProdutoAlterado);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void verificarItensRemovidos(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).exists()){
                    Iterable<DataSnapshot> itensCompra = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
                    for (DataSnapshot itensCompraCarrinhoUser: itensCompra){
                        ItemCompra itemCompra = itensCompraCarrinhoUser.getValue(ItemCompra.class);
                        if (dataSnapshot.child("produtoExcluido").child(itemCompra.getIdProduto()).exists()){
                            CarrinhoCompraServices.removerItemCarrinho(itemCompra.getIdItemCompra());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void atualizarTotalCarrinho(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).exists()){
                    Iterable<DataSnapshot> itensCompra = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
                    for (DataSnapshot itensCompraCarrinhoUser: itensCompra){
                        ItemCompra itemCompra = itensCompraCarrinhoUser.getValue(ItemCompra.class);
                        Produto produto = dataSnapshot.child("produto").child(itemCompra.getIdProduto()).getValue(Produto.class);
                        Double total = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").getValue(Double.class);
                        inserirTotal(produto,itemCompra,total);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Método que resgata todos os itens do carrinho de compra
    private void resgatarItensComprasCarrinho(DataSnapshot dataSnapshot, Produto produto, Double total, String idProdutoAlterado) {
        Iterable<DataSnapshot> itensCompra = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
        for (DataSnapshot itensCompraCarrinhoUser: itensCompra){
            ItemCompra itemCompra = itensCompraCarrinhoUser.getValue(ItemCompra.class);
            if ((itemCompra.getIdProduto().equals(idProdutoAlterado))) {
                if (alterarValorItemCompra(itemCompra, produto)){
                    inserirTotal(produto, itemCompra, total);
                    resgatarTotal();
                    criarAdapter();
                    break;
                }else {
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
                }
            }
        }
    }
    //Gerando negociação
    private void geraNegociacao(DataSnapshot dataSnapshot){
        HashMap<String, ArrayList<ItemCompra>> dicionarioEmpresas = separadorCarrinhoCompra(dataSnapshot);
        Set<String> empresas = dicionarioEmpresas.keySet();
        for (String empresa: empresas) {
            Negociacao negociacao = criarNegociacao(dataSnapshot, dicionarioEmpresas, empresa);
            NegociacaoServices.inserirNegociacao(negociacao);
        }
        limparCarrinho();
    }
    //Gera Historico - Cada empresa do produto tem seu próprio histórico - Tem como entrada o dicionario gerador pelo separador carrinho compra
    private void geraHistorico(HashMap<String, ArrayList<ItemCompra>> dic){
        double total = 0.0;
        Set<String> empresas = dic.keySet();
        for (String empresa : empresas) {
            for (ItemCompra itemCompra: dic.get(empresa)){
                total+=(itemCompra.getValor()*itemCompra.getQuantidade());
            }
            Historico historico = criarHistorico(dic, total, empresa);
            HistoricoServices.adicionarHistorico(historico);
        }
    }
    //Separa carrinho de compra em um dicionário contendo Dic<Empresa, produtos comprados da empresa>
    private HashMap separadorCarrinhoCompra(DataSnapshot dataSnapshot){
        //Percorrendo carrinho para separar produtos por empresa
        Iterable<DataSnapshot> produtosCarrinho = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
        HashMap<String,ArrayList<ItemCompra>> dicionarioHistorico = new HashMap<>();
        //Separando produtos
        for(DataSnapshot itensCompraCarrinhoUser : produtosCarrinho){
            ItemCompra itemCompra = itensCompraCarrinhoUser.getValue(ItemCompra.class);
            Produto produto = dataSnapshot.child("produto").child(itemCompra.getIdProduto()).getValue(Produto.class);
            //Caso já esteja no dicionário, get na string idEmpresa do dicionario e adicionamos o produto da empresa
            if (dicionarioHistorico.containsKey(produto.getIdEmpresa())){
                dicionarioHistorico.get(produto.getIdEmpresa()).add(itemCompra);
            }
            //Caso contrário - adicionamos a String id empresa e a array com o itemcompra.
            else {
                ArrayList<ItemCompra> addDicionario = new ArrayList<>();
                addDicionario.add(itemCompra);
                dicionarioHistorico.put(produto.getIdEmpresa(), addDicionario);
            }
        }
        return dicionarioHistorico;
    }
    //Inserindo novo total no banco para servir de referência ao método "criarAdapter()"
    private void inserirTotal(Produto produto, ItemCompra itemCompra, Double total) {
        if (itemCompra.getValor() != produto.getPrecoSugerido()) {
            double novoTotal = total - (itemCompra.getValor()*itemCompra.getQuantidade()) + (produto.getPrecoSugerido()*itemCompra.getQuantidade());
            CarrinhoCompraServices.inserirTotal(novoTotal, produto,itemCompra);
        }
    }
    //Método que calcula o novo total
    private boolean alterarValorItemCompra(ItemCompra itemCompra, Produto produto) {
        return CarrinhoCompraServices.alterarValorItemCompra(itemCompra, produto);
    }
    //Criando objeto negociacao
    @NonNull
    private Negociacao criarNegociacao(DataSnapshot dataSnapshot, HashMap<String, ArrayList<ItemCompra>> dicionarioEmpresas, String empresa) {
        Negociacao negociacao = new Negociacao();
        negociacao.setIdPessoaFisica(FirebaseController.getUidUser());
        negociacao.setIdPessoaJuridica(dataSnapshot.child("produto").child(dicionarioEmpresas.get(empresa).get(0).getIdProduto()).child("idEmpresa").getValue(String.class));
        negociacao.setDataInicio(Helper.getData());
        negociacao.setCarrinhoAtual(dicionarioEmpresas.get(empresa));
        return negociacao;
    }
    //Criando objeto histórico
    @NonNull
    private Historico criarHistorico(HashMap<String, ArrayList<ItemCompra>> dic, double total, String empresa) {
        Historico historico = new Historico();
        historico.setIdPessoaJuridica(empresa);
        historico.setIdPessoaFisica(FirebaseController.getUidUser());
        historico.setCarrinho(dic.get(empresa));
        historico.setDataCompra(Helper.getData());
        historico.setTotal(total);
        return historico;
    }
    //Chamando camada de negócio para limpar carrinho
    private void limparCarrinho() {
        CarrinhoCompraServices.limparCarrinho();
        abrirTelaMainNegocicaoActivity();
    }
    //Montando adapter e jogando no list holder
    private void criarAdapter() {
        final DatabaseReference databaseReference = FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra");

        if (databaseReference != null) {

            adapter = new FirebaseRecyclerAdapter<ItemCompra, ProdutoListHolder>(
                    ItemCompra.class,
                    R.layout.card_produto,
                    ProdutoListHolder.class,
                    databaseReference) {

                @Override
                protected void populateViewHolder(final ProdutoListHolder viewHolder, final ItemCompra model, int position) {
                    FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Produto produto = dataSnapshot.child("produto").child(model.getIdProduto()).getValue(Produto.class);
                            if (produto != null) {
                                Pessoa pessoa = dataSnapshot.child("pessoa").child(produto.getIdEmpresa()).getValue(Pessoa.class);
                                if (pessoa != null) {
                                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                                    viewHolder.linearLayout.setVisibility(View.VISIBLE);
                                    viewHolder.tvCardViewNomeProduto.setText(produto.getNome());
                                    viewHolder.tvCardViewPrecoProduto.setText(NumberFormat.getCurrencyInstance().format(produto.getPrecoSugerido()));
                                    viewHolder.tvCardViewQuantidadeEstoque.setText(String.valueOf(model.getQuantidade()));
                                    viewHolder.tvCardViewNomeEmpresa.setText(pessoa.getNome());
                                    if (produto.getUrlImagem() != null) {
                                        Glide.with(getApplicationContext()).load(produto.getUrlImagem()).into(viewHolder.imgCardViewProduto);
                                    }else {
                                        viewHolder.imgCardViewProduto.setImageResource(R.drawable.ic_produtos);
                                    }
                                    if (getItemCount() == 0) {
                                        calcularTotal(produto.getPrecoSugerido(), model.getQuantidade());
                                    } else {
                                        tvTotalCardViewItemCompra.setText(NumberFormat.getCurrencyInstance().format(dataSnapshot.child("carrinhoCompra")
                                                .child(FirebaseController.getUidUser()).child("total").getValue(Double.class)));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            };
            recyclerViewItens.setAdapter(adapter);
        }
    }
    //Resgatando total do carrinho
    private void resgatarTotal(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double total = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").getValue(Double.class);
                if (total != null) {
                    tvTotalCardViewItemCompra.setText(NumberFormat.getCurrencyInstance().format(total));
                }else{
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_carrinho_vazio));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Método que exibe a caixa de diálogo para o aluno confirmar ou não a sua saída da turma
    private void sair () {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle(getString(R.string.zs_dialogo_titulo));
        //define a mensagem
        builder.setMessage(getString(R.string.zs_dialogo_mensagem_sair_conta));
        //define um botão como positivo
        builder.setPositiveButton(getString(R.string.zs_dialogo_sim), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                FirebaseAuth.getInstance().signOut();
                abrirTelaLoginActivity();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton(getString(R.string.zs_dialogo_nao), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                alertaSair.dismiss();
            }
        });
        //cria o AlertDialog
        alertaSair = builder.create();
        //Exibe
        alertaSair.show();
    }
    //Calculando total
    private void calcularTotal(double preco, int quantidade) {
        total+=(preco*quantidade);
        tvTotalCardViewItemCompra.setText(NumberFormat.getCurrencyInstance().format(total));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Intent para a tela de perfil
    private void abrirTelaPerfilPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela main pessoa física, onde estão os produtos
    private void abrirTelaMainPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de histórico pessoa física, onde estão os produtos
    private void abrirTelaMainHistoricoPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainHistoricoPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intetn para a tela de negociacao
    private void abrirTelaMainNegocicaoActivity(){
        Intent intent = new Intent(getApplicationContext(), MainNegociacaoActivity.class);
        startActivity(intent);
        finish();
    }
    //Intent para a tela de login
    private void abrirTelaLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}