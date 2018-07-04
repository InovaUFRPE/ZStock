package com.zstok.negociacao.gui;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.historico.adapter.ItemCompraListHolder;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.MoneyTextWatcher;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.produto.dominio.Produto;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class CarrinhoNegociacaoActivity extends AppCompatActivity {

    private TextView tvTotalCarrinhoNegocicao;

    //Views da caixa de diálogo
    private EditText edtDescontoCaixaDialogo;
    private TextView tvTotalCaixaDialogo;
    private TextView tvTotalDescontoCaixaDialogo;
    private Button btnGerarDescontoCaixaDialogo;

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerViewItens;

    private AlertDialog alerta;

    private double total;
    private String idNegociacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho_negociacao);

        idNegociacao = getIntent().getStringExtra("idNegociacao");

        tvTotalCarrinhoNegocicao = findViewById(R.id.tvTotalCardViewItemCompra);

        //Instanciando recyler view
        recyclerViewItens = findViewById(R.id.recyclerItensCarrinhoNecogiacao);
        recyclerViewItens.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CarrinhoNegociacaoActivity.this);
        recyclerViewItens.setLayoutManager(layoutManager);
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
                            dialogoItemCompra(itemCompra);
                        }
                    });
                    return viewHolder;
                }
            };
            recyclerViewItens.setAdapter(adapter);
        }
    }
    //Calculando total
    private void calcularTotal(double preco, int quantidade) {
        total+=(preco*quantidade);
        tvTotalCarrinhoNegocicao.setText(NumberFormat.getCurrencyInstance().format(total));
    }
    //Método que exibe a caixa de diálogo para o aluno confirmar ou não a sua saída da turma
    private void dialogoItemCompra(final ItemCompra itemCompra) {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.modelo_caixa_dialogo_desconto, null);

        //Instanciando views
        instanciandoViews(mView);

        //Setando views da caixa de diálogo
        setarInformacoesViews(itemCompra);

        //Cria o AlertDialog
        builder.setView(mView);
        alerta = builder.create();
        alerta.show();

        clickGerarDesconto();
    }
    //Método que gera desconto
    private void clickGerarDesconto() {
        btnGerarDescontoCaixaDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fazer método que aplica desconto ao produto selecionado
            }
        });
    }
    //Método que seta as informações para as views da caixa de diálogo
    private void setarInformacoesViews(ItemCompra itemCompra){
        tvTotalCaixaDialogo.setText(String.valueOf(itemCompra.getQuantidade() * itemCompra.getValor()));
        edtDescontoCaixaDialogo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtDescontoCaixaDialogo.getText().toString().isEmpty() ||
                        !(edtDescontoCaixaDialogo.getText().toString().trim().length() == 0)){
                    //Fazer método
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!edtDescontoCaixaDialogo.getText().toString().isEmpty() ||
                        !(edtDescontoCaixaDialogo.getText().toString().trim().length() == 0)){
                    //Fazer método
                }else {
                    //Fazer método
                }
            }
        });
    }
    //Método que instancia as views da caixa de diálogo
    private void instanciandoViews(View mView){
        edtDescontoCaixaDialogo = mView.findViewById(R.id.edtDescontoCaixaDialogo);
        tvTotalCaixaDialogo = mView.findViewById(R.id.tvTotalCaixaDialogo);
        tvTotalDescontoCaixaDialogo = mView.findViewById(R.id.tvTotalDescontoCaixaDialogo);
        btnGerarDescontoCaixaDialogo = mView.findViewById(R.id.btnGerarDescontoCaixaDialogo);
    }
}
