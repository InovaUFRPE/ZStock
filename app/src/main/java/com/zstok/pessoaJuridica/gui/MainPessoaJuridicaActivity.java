package com.zstok.pessoaJuridica.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
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
import com.zstok.R;
import com.zstok.historico.gui.MainHistoricoPessoaJuridicaActivity;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.negociacao.adapter.NegociacaoListHolder;
import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.negociacao.gui.ChatNegociacaoActivity;
import com.zstok.perfil.gui.PerfilPessoaJuridicaActivity;
import com.zstok.produto.gui.MeusProdutosActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPessoaJuridicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private AlertDialog alertaSair;

    private FirebaseUser user;

    private TextView tvNomeUsuarioNavHeader;
    private TextView tvEmailUsuarioNavHeader;
    private CircleImageView cvNavHeaderPessoa;

    private RecyclerView recylerViewNegocicao;
    private FirebaseRecyclerAdapter adapterNegociacao;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pessoa_juridica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Resgatando usuário atual
        user = FirebaseController.getFirebaseAuthentication().getCurrentUser();

        //Instanciando recyler view
        recylerViewNegocicao = findViewById(R.id.recyclerNegociacaoPessoaJuridica);
        recylerViewNegocicao.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(MainPessoaJuridicaActivity.this);
        recylerViewNegocicao.setLayoutManager(layoutManager);

        //Criando adapter negociacao
        criarAdapterNegociacao();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Instanciando views do menu lateral
        instanciandoView();

        //Carregando informações do menu lateral
        setDadosMenuLateral();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_meu_perfil_pessoa_juridica:
                        abrirTelaPerfilPessoaJuridicaActivity();
                        return true;
                    case R.id.nav_negociacao_pessoa_juridica:
                        //Função abrir tela negociacao
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_produtos_pessoa_juridica:
                        //Função abrir tela produtos
                        abrirTelaMeusProdutosActivity();
                        return true;
                    case R.id.nav_meu_historico_pessoa_juridica:
                        //Função abrir tela histórico de vendas
                        abrirTelaHistoricoPessoaJuridicaActivity();
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
    private void criarAdapterNegociacao() {
        final DatabaseReference databaseReference = FirebaseController.getFirebase().child("negociacao");
        Query queryAdapter = databaseReference.orderByChild("idPessoaJuridica").equalTo(FirebaseController.getUidUser());
        if (queryAdapter != null) {
            adapterNegociacao = new FirebaseRecyclerAdapter<Negociacao, NegociacaoListHolder>(
                    Negociacao.class,
                    R.layout.card_negociacao,
                    NegociacaoListHolder.class,
                    queryAdapter) {

                @Override
                protected void populateViewHolder(final NegociacaoListHolder viewHolder, final Negociacao model, int position) {
                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                    viewHolder.linearLayout.setVisibility(View.VISIBLE);

                    viewHolder.tvCardViewDataInicio.setText(model.getDataInicio());
                    if (model.getDataFim() == null){
                        viewHolder.tvCardViewDataFim.setText("Indefinida");
                    }else {
                        viewHolder.tvCardViewDataFim.setText(model.getDataFim());
                    }
                    resgatarCpfPessoaFisica(viewHolder, model);
                }

                @NonNull
                @Override
                public NegociacaoListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                    final NegociacaoListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                    viewHolder.setOnItemClickListener(new NegociacaoListHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Negociacao negociacao = (Negociacao) adapterNegociacao.getItem(position);
                            abrirTelaChatNegociacaoActivity(negociacao.getIdNegociacao());
                        }
                    });
                    return viewHolder;
                }
            };
            recylerViewNegocicao.setAdapter(adapterNegociacao);
        }
    }

    private void resgatarCpfPessoaFisica(final NegociacaoListHolder viewHolder, final Negociacao model) {
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cpf = dataSnapshot.child("pessoaFisica").child(model.getIdPessoaFisica()).child("cpf").getValue(String.class);
                viewHolder.tvCardViewNomeCpfEmpresa.setText(cpf);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Instanciando views do navigation header
    private void instanciandoView(){
        View headerView = navigationView.getHeaderView(0);
        tvNomeUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderNome);
        tvEmailUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderEmail);
        cvNavHeaderPessoa = headerView.findViewById(R.id.cvNavHeaderPessoa);
    }
    //Método que carrega nome e email do usuário e seta nas views do menu lateral
    private void setDadosMenuLateral(){
        if (user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).into(cvNavHeaderPessoa);
        }else {
            cvNavHeaderPessoa.setImageResource(R.drawable.ic_sem_foto);
        }
        tvNomeUsuarioNavHeader.setText(user.getDisplayName());
        tvEmailUsuarioNavHeader.setText(user.getEmail());
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
    //Intent para a tela dos produtos cadastrados
    private void abrirTelaMeusProdutosActivity(){
        Intent intent = new Intent(getApplicationContext(), MeusProdutosActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de perfil da pessoa jurídica
    private void abrirTelaPerfilPessoaJuridicaActivity() {
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de histórico de vendas
    private void abrirTelaHistoricoPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainHistoricoPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de chat
    private void abrirTelaChatNegociacaoActivity(String idNegociacao){
        Intent intent = new Intent(getApplicationContext(), ChatNegociacaoActivity.class);
        intent.putExtra("idNegociacao", idNegociacao);
        startActivity(intent);
    }
    //Intent para a tela de login
    private void abrirTelaLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
