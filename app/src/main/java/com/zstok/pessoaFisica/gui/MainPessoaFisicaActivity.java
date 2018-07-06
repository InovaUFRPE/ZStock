package com.zstok.pessoaFisica.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zstok.carrinhoCompra.gui.CarrinhoCompraActivity;
import com.zstok.R;
import com.zstok.historico.gui.MainHistoricoNegociacaoPessoaJuridicaActivity;
import com.zstok.historico.gui.MainHistoricoPessoaFisicaActivity;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.negociacao.gui.MainNegociacaoActivity;
import com.zstok.perfil.gui.PerfilPessoaFisicaActivity;
import com.zstok.produto.adapter.ProdutoListHolder;
import com.zstok.produto.dominio.Produto;
import com.zstok.produto.gui.VisualizarProdutoActivity;

import java.text.NumberFormat;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainPessoaFisicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvNomeUsuarioNavHeader;
    private TextView tvEmailUsuarioNavHeader;
    private CircleImageView cvNavHeaderPessoa;

    private NavigationView navigationView;
    private AlertDialog alertaSair;

    private EditText edtPesquisaProdutoPessoaFisica;

    private RecyclerView recylerViewMeusprodutos;
    private FirebaseRecyclerAdapter adapter;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pessoa_fisica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Resgantado usuário atual
        user = FirebaseController.getFirebaseAuthentication().getCurrentUser();

        //Instanciando views
        edtPesquisaProdutoPessoaFisica = findViewById(R.id.edtPesquisaProdutoPessoaFisica);
        Button btnPesquisaProdutoPessoaJuridica = findViewById(R.id.btnPesquisaProdutoPessoaFisica);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCarrinhoCompra);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaCarrinhoCompraActivity();
            }
        });

        btnPesquisaProdutoPessoaJuridica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criandoAdapterPesquisa(Helper.removerAcentos(edtPesquisaProdutoPessoaFisica.getText().toString().toLowerCase()));
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Instanciando recyler view
        recylerViewMeusprodutos = findViewById(R.id.recyclerProdutosPessoaFisica);
        recylerViewMeusprodutos.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recylerViewMeusprodutos.setLayoutManager(layoutManager);

        //Criando o adapter
        criandoAdapter();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Instanciando views do menu lateral
        instanciandoViews();

        //Carregando informações do menu lateral
        setDadosMenuLateral();

        //Evento de pesquisa
        edtPesquisaProdutoPessoaFisica.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                criandoAdapterPesquisa(Helper.removerAcentos(edtPesquisaProdutoPessoaFisica.getText().toString().toLowerCase()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPesquisaProdutoPessoaFisica.getText().toString().isEmpty() ||
                        edtPesquisaProdutoPessoaFisica.getText().toString().trim().length() == 0){
                    criandoAdapter();
                }else {
                    criandoAdapterPesquisa(Helper.removerAcentos(edtPesquisaProdutoPessoaFisica.getText().toString().toLowerCase()));
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_meu_perfil_pessoa_fisica:
                        abrirTelaPerfilPessoaFisicaActivity();
                        return true;
                    case R.id.nav_negociacao_pessoa_fisica:
                        //Activity de negociação
                        abrirTelaMainNegociacaoActivity();
                        return true;
                    case R.id.nav_produtos_pessoa_fisica:
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_meu_historico_pessoa_fisica:
                        abrirTelaMainHistoricoPessoaFisicaActivity();
                        return true;
                    case  R.id.nav_meu_historico_negociacao_pessoa_fisica:
                        abrirTelaMainHistoricoNegociacaoPessoaJuridicaActivity();
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
    //Montando adapter e jogando no list holder
    private void criandoAdapterPesquisa(String pesquisa) {
        final DatabaseReference databaseReference = FirebaseController.getFirebase().child("produto");
        Query query = databaseReference.orderByChild("nomePesquisa").startAt(pesquisa).endAt(pesquisa+"\uf8ff");

        if (query != null) {
            FirebaseRecyclerAdapter adapter1 = new FirebaseRecyclerAdapter<Produto, ProdutoListHolder>(
                    Produto.class,
                    R.layout.card_produto,
                    ProdutoListHolder.class,
                    query) {

                @Override
                protected void populateViewHolder(final ProdutoListHolder viewHolder, final Produto model, int position) {
                    getItemCount();
                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                    viewHolder.linearLayout.setVisibility(View.VISIBLE);
                    viewHolder.tvCardViewNomeProduto.setText(model.getNome());
                    viewHolder.tvCardViewPrecoProduto.setText(NumberFormat.getCurrencyInstance().format(model.getPrecoSugerido()));
                    viewHolder.tvCardViewQuantidadeEstoque.setText(String.valueOf(model.getQuantidadeEstoque()));
                    if (model.getUrlImagem() != null) {
                        Glide.with(getApplicationContext()).load(Uri.parse(model.getUrlImagem())).into(viewHolder.imgCardViewProduto);
                    }
                    FirebaseController.getFirebase().child("pessoa").child(model.getIdEmpresa()).child("nome").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            viewHolder.tvCardViewNomeEmpresa.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @NonNull
                @Override
                public ProdutoListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                    final ProdutoListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                    viewHolder.setOnItemClickListener(new ProdutoListHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Produto produto = (Produto) adapter.getItem(position);
                            abrirTelaProdutoActivity(produto.getIdEmpresa(), produto.getIdProduto());
                        }
                    });
                    return viewHolder;
                }
            };
            recylerViewMeusprodutos.setAdapter(adapter1);
        }
    }
    //Montando adapter e jogando no list holder
    private void criandoAdapter() {
        final DatabaseReference databaseReference = FirebaseController.getFirebase().child("produto");

        if (databaseReference != null) {
            adapter = new FirebaseRecyclerAdapter<Produto, ProdutoListHolder>(
                    Produto.class,
                    R.layout.card_produto,
                    ProdutoListHolder.class,
                    databaseReference) {

                @Override
                protected void populateViewHolder(final ProdutoListHolder viewHolder, final Produto model, int position) {
                    getItemCount();
                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                    viewHolder.linearLayout.setVisibility(View.VISIBLE);
                    viewHolder.tvCardViewNomeProduto.setText(model.getNome());
                    viewHolder.tvCardViewPrecoProduto.setText(NumberFormat.getCurrencyInstance().format(model.getPrecoSugerido()));
                    if (model.getQuantidadeEstoque() != 0) {
                        viewHolder.tvCardViewQuantidadeEstoque.setText(String.valueOf(model.getQuantidadeEstoque()));
                    }else {
                        viewHolder.tvCardViewQuantidadeEstoque.setText("Produto esgotado!");
                    }
                    resgatarNomeEmpresa(viewHolder, model);
                    if (model.getUrlImagem() != null) {
                        Glide.with(MainPessoaFisicaActivity.this).load(Uri.parse(model.getUrlImagem())).into(viewHolder.imgCardViewProduto);
                    }else {
                        viewHolder.imgCardViewProduto.setImageResource(R.drawable.ic_produtos);
                    }
                }

                @NonNull
                @Override
                public ProdutoListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                    final ProdutoListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                    viewHolder.setOnItemClickListener(new ProdutoListHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Produto produto = (Produto) adapter.getItem(position);
                            abrirTelaProdutoActivity(produto.getIdEmpresa(), produto.getIdProduto());
                        }
                    });
                    return viewHolder;
                }
            };
            recylerViewMeusprodutos.setAdapter(adapter);
        }
    }
    //Método que resgata nome da empresa do banco
    private void resgatarNomeEmpresa(final ProdutoListHolder viewHolder, Produto model) {
        FirebaseController.getFirebase().child("pessoa").child(model.getIdEmpresa()).child("nome").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewHolder.tvCardViewNomeEmpresa.setText(dataSnapshot.getValue(String.class));
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
    //Método que instancia as views
    private void instanciandoViews(){
        View headerView = navigationView.getHeaderView(0);
        tvNomeUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderNome);
        tvEmailUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderEmail);
        cvNavHeaderPessoa = headerView.findViewById(R.id.cvNavHeaderPessoa);
    }
    //Método que carrega as informações do menu lateral
    private void setDadosMenuLateral(){
        if (user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).into(cvNavHeaderPessoa);
        }else {
            cvNavHeaderPessoa.setImageResource(R.drawable.ic_sem_foto);
        }
        tvNomeUsuarioNavHeader.setText(user.getDisplayName());
        tvEmailUsuarioNavHeader.setText(user.getEmail());
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Intent para a tela de perfil pessoa física
    private void abrirTelaPerfilPessoaFisicaActivity() {
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de login
    private void abrirTelaLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
    //Intent para a tela de login
    private void abrirTelaProdutoActivity(String idEmpresa, String idProduto) {
        Intent intent = new Intent(getApplicationContext(), VisualizarProdutoActivity.class);
        intent.putExtra("idEmpresa", idEmpresa);
        intent.putExtra("idProduto", idProduto);
        startActivity(intent);
    }
    //Intent para a tela de carrinho de compra
    private void abrirTelaCarrinhoCompraActivity() {
        Intent intent = new Intent(getApplicationContext(), CarrinhoCompraActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de histórico pessoa física
    private void abrirTelaMainHistoricoPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainHistoricoPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela com o histórico de negociações
    private void abrirTelaMainHistoricoNegociacaoPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainHistoricoNegociacaoPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de negociação
    private void abrirTelaMainNegociacaoActivity(){
        Intent intent = new Intent(getApplicationContext(), MainNegociacaoActivity.class);
        startActivity(intent);
    }
}