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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.carrinhoCompra.adapter.ItemCompraListHolder;
import com.zstok.carrinhoCompra.negocio.CarrinhoCompraServices;
import com.zstok.historico.dominio.Historico;
import com.zstok.historico.negocio.HistoricoServices;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.perfil.gui.PerfilPessoaFisicaActivity;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaFisica.gui.MainPessoaFisicaActivity;
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
        Button btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra);

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

        FirebaseController.getFirebase().child("produto").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                atualizarCarrinhoCompra(dataSnapshot.getKey());
                //Preenchendo recyclerView
                criarAdapter();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                    case R.id.nav_meu_perfil_fisico:
                        abrirTelaPerfilPessoaFisicaActivity();
                        return true;
                    case R.id.nav_produtos_fisico:
                        abrirTelaMainPessoaFisicaActivity();
                        return true;
                    case R.id.nav_negociacao_fisico:
                        Helper.criarToast(getApplicationContext(), "Em construção...");
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
           geraHistorico(separadorCarrinhoCompra(dataSnapshot), dataSnapshot);
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
    //Método que atualiza carrinho compra
    private void atualizarCarrinhoCompra(final String idProdutoAlterado){
        FirebaseController.getFirebase().addValueEventListener(new ValueEventListener() {
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
    //Gera Historico - Cada empresa do produto tem seu próprio histórico - Tem como entrada o dicionario gerador pelo separador carrinho compra
    private void geraHistorico(HashMap<String, ArrayList<ItemCompra>> dic, DataSnapshot dataSnapshot){
        Set<String> empresas = dic.keySet();
        for (String empresa : empresas) {
            Historico historico = new Historico();
            historico.setCnpj(dataSnapshot.child("pessoaJuridica").child(empresa).child("cnpj").getValue(String.class));
            historico.setCpf(dataSnapshot.child("pessoaFisica").child(FirebaseController.getUidUser()).child("cpf").getValue(String.class));
            historico.setCarrinho(dic.get(empresa));
            historico.setDataCompra(Helper.getData());
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
    //Método que resgata todos os itens do carrinho de compra
    private void resgatarItensComprasCarrinho(DataSnapshot dataSnapshot, Produto produto, Double total, String idProdutoAlterado) {
        Iterable<DataSnapshot> itensCompra = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
        for (DataSnapshot itensCompraCarrinhoUser: itensCompra){
            ItemCompra itemCompra = itensCompraCarrinhoUser.getValue(ItemCompra.class);
            if ((itemCompra.getIdProduto().equals(idProdutoAlterado))) {
                if (alterarValorItemCompra(itemCompra, produto)){
                    inserirTotal(produto, itemCompra, total);
                    resgatarTotal();
                    break;
                }else {
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
                }
            }
        }
    }
    //Inserindo novo total no banco para servir de referência ao método "criarAdapter()"
    private void inserirTotal(Produto produto, ItemCompra itemCompra, Double total) {
        if (itemCompra.getValor() != produto.getPrecoSugerido()) {
            double novoTotal = total - (itemCompra.getValor()*itemCompra.getQuantidade()) + (produto.getPrecoSugerido()*itemCompra.getQuantidade());
            CarrinhoCompraServices.inserirTotal(novoTotal);
        }
    }
    //Método que calcula o novo total
    private boolean alterarValorItemCompra(ItemCompra itemCompra, Produto produto) {
        return CarrinhoCompraServices.alterarValorItemCompra(itemCompra, produto);
    }
    //Montando adapter e jogando no list holder
    private void criarAdapter() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra");

        if (databaseReference != null) {

            adapter = new FirebaseRecyclerAdapter<ItemCompra, ItemCompraListHolder>(
                    ItemCompra.class,
                    R.layout.card_item_compra,
                    ItemCompraListHolder.class,
                    databaseReference) {

                @Override
                protected void populateViewHolder(final ItemCompraListHolder viewHolder, final ItemCompra model, int position) {
                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                    viewHolder.linearLayout.setVisibility(View.VISIBLE);

                    FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Produto produto = dataSnapshot.child("produto").child(model.getIdProduto()).getValue(Produto.class);
                            if (produto != null) {
                                Pessoa pessoa = dataSnapshot.child("pessoa").child(produto.getIdEmpresa()).getValue(Pessoa.class);
                                if (pessoa != null) {
                                    viewHolder.tvCardViewNomeItemCompra.setText(produto.getNome());
                                    viewHolder.tvCardViewPrecoItemCompra.setText(NumberFormat.getCurrencyInstance().format(produto.getPrecoSugerido()));
                                    viewHolder.tvCardViewQuantidadeItemCompra.setText(String.valueOf(model.getQuantidade()));
                                    viewHolder.tvCardViewNomeEmpresa.setText(pessoa.getNome());
                                    if (produto.getUrlImagem() != null) {
                                        Glide.with(getApplicationContext()).load(produto.getUrlImagem()).into(viewHolder.imgCardViewItemCompra);
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
    //Intent para a tela de login
    private void abrirTelaLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}