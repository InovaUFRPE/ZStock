package com.zstok.historico.gui;

import android.app.ProgressDialog;
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
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.negociacao.adapter.NegociacaoListHolder;
import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.perfil.gui.PerfilPessoaJuridicaActivity;
import com.zstok.pessoaJuridica.gui.MainPessoaJuridicaActivity;
import com.zstok.produto.gui.MeusProdutosActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainHistoricoNegociacaoPessoaJuridicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AlertDialog alertaSair;

    private TextView tvNomeUsuarioNavHeader;
    private TextView tvEmailUsuarioNavHeader;
    private CircleImageView cvNavHeaderPessoa;

    private RecyclerView recyclerViewHistoricoNegociacao;
    private FirebaseRecyclerAdapter adapterHistoricoNegociacao;

    private NavigationView navigationView;

    private ProgressDialog progressDialog;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_historico_negociacao_pessoa_juridica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Resgatando usuario atual
        user = FirebaseController.getFirebaseAuthentication().getCurrentUser();

        //Instanciando progress dialog
        progressDialog = new ProgressDialog(this);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Instanciando recyler view
        recyclerViewHistoricoNegociacao = findViewById(R.id.recyclerHistoricoNegociacaoPessoaJuridica);
        recyclerViewHistoricoNegociacao.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainHistoricoNegociacaoPessoaJuridicaActivity.this);
        recyclerViewHistoricoNegociacao.setLayoutManager(layoutManager);

        //Criando adapter
        criarAdapterHistoricoNegociacao();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Instanciando views do menu lateral
        instanciandoViews();

        //Carregar dados do menu lateral
        setDadosMenuLateral();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_meu_perfil_pessoa_juridica:
                        //Abrir a tela de perfil
                        abrirTelaPerfilPessoaJuridicaActivity();
                        return true;
                    case R.id.nav_negociacao_pessoa_juridica:
                        //Fechando menu lateral
                        abrirTelaMainPessoaJuridicaActivity();
                        return true;
                    case R.id.nav_produtos_pessoa_juridica:
                        //Abrir a tela main
                        abritTelaMeusProdutosActivity();
                        return true;
                    case R.id.nav_meu_historico_vendas_pessoa_juridica:
                        //Abrir a tela de histórico vendas
                        abrirTelaMainHistoricoVendaPessoaJuridicaActivity();
                        return true;
                    case R.id.nav_meu_historico_negociacao_pessoa_juridica:
                        //Fechar menu lateral
                        drawer.closeDrawers();
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
    //Método que instancia as views
    private void instanciandoViews(){
        View headerView = navigationView.getHeaderView(0);
        tvNomeUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderNome);
        tvEmailUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderEmail);
        cvNavHeaderPessoa = headerView.findViewById(R.id.cvNavHeaderPessoa);
    }
    //Método que carrega nome e email do usuário e seta nas views do menu lateral
    private void setDadosMenuLateral(){
        if (user.getPhotoUrl() != null){
            Glide.with(MainHistoricoNegociacaoPessoaJuridicaActivity.this).load(user.getPhotoUrl()).into(cvNavHeaderPessoa);
        }else {
            cvNavHeaderPessoa.setImageResource(R.drawable.ic_sem_foto);
        }
        tvNomeUsuarioNavHeader.setText(user.getDisplayName());
        tvEmailUsuarioNavHeader.setText(user.getEmail());
    }
    //Método que inicia o progress dialog
    private void iniciarProgressDialog() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.zs_titulo_progress_dialog_carregar_historico_negociacao));
        progressDialog.show();
    }
    //Método que cria o adapter de histórico
    private void criarAdapterHistoricoNegociacao(){
        iniciarProgressDialog();
        DatabaseReference referenciaHistorico = FirebaseController.getFirebase().child("historicoNegociacao");
        Query queryHistoricoCompra = referenciaHistorico.orderByChild("idPessoaJuridica").equalTo(FirebaseController.getUidUser());

        if (queryHistoricoCompra != null) {
            adapterHistoricoNegociacao = new FirebaseRecyclerAdapter<Negociacao, NegociacaoListHolder>(
                    Negociacao.class,
                    R.layout.card_negociacao,
                    NegociacaoListHolder.class,
                    queryHistoricoCompra) {

                @Override
                protected void populateViewHolder(final NegociacaoListHolder viewHolder, final Negociacao model, int position) {
                    getItemCount();
                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                    viewHolder.linearLayout.setVisibility(View.VISIBLE);
                    viewHolder.tvCardViewDataInicio.setText(model.getDataInicio());
                    viewHolder.tvCardViewDataFim.setText(model.getDataFim());

                    //Método que resgata cnpj da pesso jurídica envolvida na negociação
                    resgatarCnpjPessoaJuridica(viewHolder, model);
                }
                //Criar onClickListenner para visualizar negociação
            };
            recyclerViewHistoricoNegociacao.setAdapter(adapterHistoricoNegociacao);
        }
    }
    //Resgatando cpf pessoa física
    private void resgatarCnpjPessoaJuridica(final NegociacaoListHolder viewHolder, final Negociacao negociacao) {
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cnpj = dataSnapshot.child("pessoaJuridica").child(negociacao.getIdPessoaJuridica()).child("cnpj").getValue(String.class);
                viewHolder.tvCardViewNomeCpfEmpresa.setText(cnpj);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Método que exibe a caixa de diálogo para o usuário confirmar ou não a sua saída do sistema
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
        } else {
            super.onBackPressed();
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
    //Intent para a tela de perfil
    private void abrirTelaPerfilPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de negociação
    private void abrirTelaMainPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de historico vendas
    private void abrirTelaMainHistoricoVendaPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainHistoricoVendaPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de produtos
    private void abritTelaMeusProdutosActivity(){
        Intent intent = new Intent(getApplicationContext(), MeusProdutosActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de login
    private void abrirTelaLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
