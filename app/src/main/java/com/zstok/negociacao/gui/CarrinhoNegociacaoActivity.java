package com.zstok.negociacao.gui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.carrinhoCompra.negocio.CarrinhoCompraServices;
import com.zstok.historico.adapter.ItemCompraListHolder;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.negociacao.negocio.NegociacaoServices;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.produto.dominio.Produto;

import java.text.NumberFormat;

public class CarrinhoNegociacaoActivity extends AppCompatActivity {

    private TextView tvTotalCarrinhoNegociacao;
    private Button btnComprarCarrinhoNegociacao;

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerViewItens;

    private VerificaConexao verificaConexao;

    private String idNegociacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho_negociacao);

        //Resgatando ID passada pela intent
        idNegociacao = getIntent().getStringExtra("idNegociacao");

        //Criando instância da class "VerificarConexao"
        verificaConexao = new VerificaConexao(this);

        //Instanciando views
        tvTotalCarrinhoNegociacao = findViewById(R.id.tvTotalCarrinhoNegociacao);
        btnComprarCarrinhoNegociacao = findViewById(R.id.btnComprarCarrinhoNegociacao);

        //Se for pessoa jurídica ocultamos o botão comprar
        verificarNegociacao();

        btnComprarCarrinhoNegociacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Comprar carrinho
                if (verificaConexao.isConected()) {
                    if (adapter.getItemCount() != 0) {
                        //Falta atualizar preco do produto caso tenha alguma atualização
                        fecharNegociacao();
                    }else {
                        Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_carrinho_vazio));
                    }
                }else {
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
                }
            }
        });

        //Instanciando recyler view
        recyclerViewItens = findViewById(R.id.recyclerItensCarrinhoNecogiacao);
        recyclerViewItens.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CarrinhoNegociacaoActivity.this);
        recyclerViewItens.setLayoutManager(layoutManager);

        criarAdapter();
    }
    //Fechando negociação
    private void fecharNegociacao(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Negociacao negociacao = dataSnapshot.child("negociacao").child(idNegociacao).getValue(Negociacao.class);
                if (negociacao != null) {
                    negociacao.setDataFim(Helper.getData());
                    if (verificaQuantidade(dataSnapshot, negociacao)) {
                        finalizarNegociacao(negociacao);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Verificando se a quantidade solicitada pelo usuário está disponível em estoque
    private boolean verificaQuantidade(DataSnapshot dataSnapshot, Negociacao negociacao) {
        //Validando quantidade
        for(ItemCompra itemCompra: negociacao.getCarrinhoAtual()){
            Produto produtoCompra = dataSnapshot.child("produto").child(itemCompra.getIdProduto()).getValue(Produto.class);
            if (itemCompra.getQuantidade() > produtoCompra.getQuantidadeEstoque() ) {
                Helper.criarToast(getApplicationContext(), "Quantidade de " + produtoCompra.getNome() + " indisponível!");
                return false;
            }else {
                //Diminuindo quando caso esteja disponível
                diminuirQuantidade(produtoCompra, itemCompra);
            }
        }
        return true;
    }
    //Diminuindo quantidade produto
    private void diminuirQuantidade(Produto produto, ItemCompra itemCompra){
        NegociacaoServices.diminuirQuantidade(produto, itemCompra);
    }
    //Chamando camada de negócio
    private void finalizarNegociacao(Negociacao negociacao){
        if (NegociacaoServices.finalizarNegociacao(negociacao)){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_negociacao_finalizada_sucesso));
            abrirTelaMainNegociacaoActivity();
        }else{
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Verificando tipo da conta
    private void verificarNegociacao() {
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("pessoaJuridica").child(FirebaseController.getUidUser()).exists() || dataSnapshot.child("negociacao").child(idNegociacao).child("dataFim").exists()){
                    btnComprarCarrinhoNegociacao.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Montando adapter e jogando no list holder
    private void criarAdapter() {
        final DatabaseReference databaseReference = FirebaseController.getFirebase().child("negociacao").child(idNegociacao).child("carrinhoAtual");

        if (databaseReference != null) {

            adapter = new FirebaseRecyclerAdapter<ItemCompra, ItemCompraListHolder>(
                    ItemCompra.class,
                    R.layout.card_item_compra,
                    ItemCompraListHolder.class,
                    databaseReference) {

                @Override
                protected void populateViewHolder(final ItemCompraListHolder viewHolder, final ItemCompra model, int position) {
                    FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Produto produto = dataSnapshot.child("produto").child(model.getIdProduto()).getValue(Produto.class);
                            if (produto != null) {
                                Pessoa pessoa = dataSnapshot.child("pessoa").child(produto.getIdEmpresa()).getValue(Pessoa.class);
                                if (pessoa != null) {
                                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                                    viewHolder.linearLayout.setVisibility(View.VISIBLE);
                                    viewHolder.tvCardViewNomeItemCompra.setText(produto.getNome());
                                    viewHolder.tvCardViewPrecoItemCompra.setText(NumberFormat.getCurrencyInstance().format(produto.getPrecoSugerido()));
                                    viewHolder.tvCardViewQuantidadeItemCompra.setText(String.valueOf(model.getQuantidade()));
                                    viewHolder.tvCardViewNomeEmpresaItemCompra.setText(pessoa.getNome());
                                    if (produto.getUrlImagem() != null) {
                                        Glide.with(getApplicationContext()).load(produto.getUrlImagem()).into(viewHolder.imgCardViewItemCompra);
                                    }else {
                                        viewHolder.imgCardViewItemCompra.setImageResource(R.drawable.ic_produtos);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                @NonNull
                @Override
                public ItemCompraListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                    final ItemCompraListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                    viewHolder.setOnItemClickListener(new ItemCompraListHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            ItemCompra itemCompra = (ItemCompra) adapter.getItem(position);
                        }
                    });
                    return viewHolder;
                }
            };
            recyclerViewItens.setAdapter(adapter);
        }
    }
    //Intent para a tela main negociacao da pessoa física
    private void abrirTelaMainNegociacaoActivity(){
        Intent intent = new Intent(getApplicationContext(), MainNegociacaoActivity.class);
        startActivity(intent);
        finish();
    }
}