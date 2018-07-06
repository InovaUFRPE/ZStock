package com.zstok.historico.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.historico.adapter.HistoricoListHolder;
import com.zstok.historico.dominio.Historico;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.negociacao.gui.MainNegociacaoActivity;
import com.zstok.perfil.gui.PerfilPessoaFisicaActivity;
import com.zstok.pessoaFisica.gui.MainPessoaFisicaActivity;

import java.text.NumberFormat;

public class MainHistoricoPessoaFisicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AlertDialog alertaSair;

    private FirebaseRecyclerAdapter adapterHistorico;
    private RecyclerView recyclerViewHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_historico_pessoa_fisica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Instanciando recyler view
        recyclerViewHistorico = findViewById(R.id.recyclerHistoricoPessoaFisica);
        recyclerViewHistorico.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainHistoricoPessoaFisicaActivity.this);
        recyclerViewHistorico.setLayoutManager(layoutManager);

        //Criando adapter
        criarAdapterHistorico();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_meu_perfil_pessoa_fisica:
                        abrirTelaMeuPerfilPessoaFisicaActivity();
                        return true;
                    case R.id.nav_negociacao_pessoa_fisica:
                        //Intent para tela de negocicao
                        abrirTelaMainNegociacaoActivity();
                        return true;
                    case R.id.nav_produtos_pessoa_fisica:
                        abrirTelaMainPessoaFisicaActivity();
                        return true;
                    case R.id.nav_meu_historico_pessoa_fisica:
                        drawer.closeDrawers();
                        return true;
                    case  R.id.nav_meu_historico_negociacao_pessoa_juridica:
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
    //Método que cria o adapter de histórico
    private void criarAdapterHistorico(){
        DatabaseReference referenciaHistorico = FirebaseController.getFirebase().child("historico");
        Query queryHistoricoCompra = referenciaHistorico.orderByChild("idPessoaFisica").equalTo(FirebaseController.getUidUser());

        if (queryHistoricoCompra != null) {
            adapterHistorico = new FirebaseRecyclerAdapter<Historico, HistoricoListHolder>(
                    Historico.class,
                    R.layout.card_historico,
                    HistoricoListHolder.class,
                    queryHistoricoCompra) {

                @Override
                protected void populateViewHolder(final HistoricoListHolder viewHolder, final Historico model, int position) {
                    getItemCount();
                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                    viewHolder.linearLayout.setVisibility(View.VISIBLE);
                    viewHolder.tvCardViewTotalCompra.setText(NumberFormat.getCurrencyInstance().format(model.getTotal()));
                    viewHolder.tvCardViewDataCompra.setText(String.valueOf(model.getDataInicio()));

                    FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nomeEmpresa = dataSnapshot.child("pessoa").child(model.getIdPessoaJuridica()).child("nome").getValue(String.class);
                            viewHolder.tvCardViewNome.setText(nomeEmpresa);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @NonNull
                @Override
                public HistoricoListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                    final HistoricoListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                    viewHolder.setOnItemClickListener(new HistoricoListHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Historico historico =  (Historico) adapterHistorico.getItem(position);
                            abrirTelaVisualizarHistoricoActivity(historico);
                        }
                    });
                    return viewHolder;
                }
            };
            recyclerViewHistorico.setAdapter(adapterHistorico);
        }
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
    //Intent para tela main
    private void abrirTelaMainPessoaFisicaActivity() {
        Intent intent = new Intent(getApplicationContext(), MainPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de perfil pessoa física
    private void abrirTelaMeuPerfilPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela visualizar histórico
    private void abrirTelaVisualizarHistoricoActivity(Historico historico){
        Intent intent = new Intent(getApplicationContext(), VisualizarHistoricoActivity.class);
        intent.putExtra("idHistorico", historico.getIdHistorico());
        intent.putExtra("idEmpresa", historico.getIdPessoaJuridica());
        intent.putExtra("idPessoaFisica", historico.getIdPessoaFisica());
        startActivity(intent);
    }
    //Intent para a tela de negociação
    private void abrirTelaMainNegociacaoActivity(){
        Intent intent = new Intent(getApplicationContext(), MainNegociacaoActivity.class);
        startActivity(intent);
    }
    //Intent para a tela com o histórico de negociações
    private void abrirTelaMainHistoricoNegociacaoPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainHistoricoNegociacaoPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de login
    private void abrirTelaLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}