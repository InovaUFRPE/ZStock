package com.zstok.historico.gui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.historico.adapter.HistoricoListHolder;
import com.zstok.historico.dominio.Historico;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.negociacao.adapter.NegociacaoListHolder;
import com.zstok.negociacao.dominio.Negociacao;

import java.text.NumberFormat;

public class MainHistoricoNegociacaoPessoaFisicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerViewHistoricoNegociacao;
    private FirebaseRecyclerAdapter adapterHistoricoNegociacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_historico_negociacao_pessoa_fisica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Instanciando recyler view
        recyclerViewHistoricoNegociacao = findViewById(R.id.recyclerHistoricoNegociacaoPessoaFisica);
        recyclerViewHistoricoNegociacao.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainHistoricoNegociacaoPessoaFisicaActivity.this);
        recyclerViewHistoricoNegociacao.setLayoutManager(layoutManager);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        criarAdapterHistorico();
    }
    //Método que cria o adapter de histórico
    private void criarAdapterHistorico(){
        DatabaseReference referenciaHistorico = FirebaseController.getFirebase().child("historicoNegociacao");
        Query queryHistoricoCompra = referenciaHistorico.orderByChild("idEmpresa").equalTo(FirebaseController.getUidUser());

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
                    resgatarCpfPessoaFisica(viewHolder, model);
                }
            };
            recyclerViewHistoricoNegociacao.setAdapter(adapterHistoricoNegociacao);
        }
    }
    //Resgatando cpf pessoa física
    private void resgatarCpfPessoaFisica(final NegociacaoListHolder viewHolder, final Negociacao negociacao) {
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cpf = dataSnapshot.child("pessoaFisica").child(negociacao.getIdPessoaFisica()).child("cpf").getValue(String.class);
                viewHolder.tvCardViewNomeCpfEmpresa.setText(cpf);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}