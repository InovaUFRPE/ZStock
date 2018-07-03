package com.zstok.historico.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.historico.adapter.ItemCompraListHolder;
import com.zstok.historico.dominio.Historico;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.produto.dominio.Produto;

import java.text.NumberFormat;

public class VisualizarHistoricoActivity extends AppCompatActivity {

    private TextView tvNomeEmpresaVizualizarHistorico;
    private TextView tvDataCompraVizualizarHistorico;
    private TextView tvCpfVisualizarHistorico;
    private TextView tvCnpjVisualizarHistorico;
    private TextView tvTotalVisualizarHistorico;

    private RecyclerView recyclerViewItens;

    private String idHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_historico);

        //Resgatando id da intent
        idHistorico = getIntent().getStringExtra("idHistorico");

        //Instanciando views
        tvNomeEmpresaVizualizarHistorico = findViewById(R.id.tvNomeEmpresaVizualizarHistorico);
        tvDataCompraVizualizarHistorico = findViewById(R.id.tvDataCompraVizualizarHistorico);
        tvCpfVisualizarHistorico =  findViewById(R.id.tvCpfVisualizarHistorico);
        tvCnpjVisualizarHistorico = findViewById(R.id.tvCnpjVisualizarHistorico);
        tvTotalVisualizarHistorico = findViewById(R.id.tvTotalVisualizarHistorico);

        //Aplicar máscara
        Helper.mascaraCnpj(tvCnpjVisualizarHistorico);
        Helper.mascaraCpf(tvCpfVisualizarHistorico);

        //Instanciando recyler view
        recyclerViewItens = findViewById(R.id.recyclerItensCompraHistorico);
        recyclerViewItens.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VisualizarHistoricoActivity.this);
        recyclerViewItens.setLayoutManager(layoutManager);

        //Setando informações do histórico
        setarViews();

        //Método que cria o adapter de itens compra
        criarAdapterItensCompra();
    }
    //Montando adapter e jogando no list holder
    private void criarAdapterItensCompra() {
        final DatabaseReference databaseReference = FirebaseController.getFirebase().child("historico").child(idHistorico).child("carrinho");

        if (databaseReference != null) {

            FirebaseRecyclerAdapter adapterItensCompra = new FirebaseRecyclerAdapter<ItemCompra, ItemCompraListHolder>(
                    ItemCompra.class,
                    R.layout.card_itens_compra,
                    ItemCompraListHolder.class,
                    databaseReference) {

                @Override
                protected void populateViewHolder(final ItemCompraListHolder viewHolder, final ItemCompra model, int position) {
                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                    viewHolder.linearLayout.setVisibility(View.VISIBLE);

                    viewHolder.tvCardViewPrecoItemCompra.setText(NumberFormat.getCurrencyInstance().format(model.getValor()));
                    viewHolder.tvCardViewQuantidadeItemCompra.setText(String.valueOf(model.getQuantidade()));

                    resgatarInformacoes(viewHolder, model);

                }
            };
            recyclerViewItens.setAdapter(adapterItensCompra);
        }
    }
    //Método que resgata as informações do banco (informações do produto e nome da empresa responsável por este)
    private void resgatarInformacoes(final ItemCompraListHolder viewHolder, final ItemCompra model) {
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Produto produto = dataSnapshot.child("produto").child(model.getIdProduto()).getValue(Produto.class);
                if (produto != null) {
                    viewHolder.tvCardViewNomeItemCompra.setText(produto.getNome());
                    if (produto.getUrlImagem() != null) {
                        Glide.with(getApplicationContext()).load(produto.getUrlImagem()).into(viewHolder.imgCardViewItemCompra);
                    } else {
                        viewHolder.imgCardViewItemCompra.setImageResource(R.drawable.ic_produtos);
                    }
                    Pessoa pessoa = dataSnapshot.child("pessoa").child(produto.getIdEmpresa()).getValue(Pessoa.class);
                    if (pessoa != null) {
                        viewHolder.tvCardViewNomeEmpresaItemCompra.setText(pessoa.getNome());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Criando objeto histórico
    private Historico criarHistorico(){
        Historico historico = new Historico();

        historico.setIdPessoaJuridica(getIntent().getStringExtra("idEmpresa"));
        historico.setIdPessoaFisica(getIntent().getStringExtra("idPessoaFisica"));

        return historico;
    }
    private void setarViews(){
        final Historico historico = criarHistorico();

        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNomeEmpresaVizualizarHistorico.setText(dataSnapshot.child("pessoa").child(historico.getIdPessoaJuridica()).child("nome").getValue(String.class));
                tvCpfVisualizarHistorico.setText(dataSnapshot.child("pessoaFisica").child(historico.getIdPessoaFisica()).child("cpf").getValue(String.class));
                tvCnpjVisualizarHistorico.setText(dataSnapshot.child("pessoaJuridica").child(historico.getIdPessoaJuridica()).child("cnpj").getValue(String.class));
                tvTotalVisualizarHistorico.setText(NumberFormat.getCurrencyInstance().format(dataSnapshot.child("historico").child(idHistorico).child("total").getValue(Double.class)));
                tvDataCompraVizualizarHistorico.setText(dataSnapshot.child("historico").child(idHistorico).child("dataCompra").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
