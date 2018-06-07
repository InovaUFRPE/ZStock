package com.zstok.produto.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.VisualizarEmpresaActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.MoneyTextWatcher;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.produto.dominio.Produto;

import java.math.BigDecimal;

public class VisualizarProdutoActivity extends AppCompatActivity {

    private String idEmpresa;
    private String idProduto;
    private TextView tvNomeProduto;
    private TextView tvQuantidadeEstoqueProduto;
    private TextView tvPrecoProduto;
    private TextView tvDescricaoProduto;
    private TextView tvEmpresaProduto;
    private ImageView imgProduto;

    //Views da caixa de diálogo
    private EditText edtQuantidadeDialogoCompra;
    private TextView tvNomeProdutoDialogoCompra;
    private TextView tvTotalDialogoCompra;
    private Button btnComprarDialogoCompra;
    private Button btnVoltarDialogoCompra;

    private AlertDialog alertaCompra;

    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);

        //Resgatando elementos passados pela intent
        idEmpresa = getIntent().getStringExtra("idEmpresa");
        idProduto = getIntent().getStringExtra("idProduto");

        //Instanciando views
        tvNomeProduto = findViewById(R.id.tvNomeProduto);
        tvQuantidadeEstoqueProduto = findViewById(R.id.tvQuantidadeDisponivelProduto);
        tvPrecoProduto = findViewById(R.id.tvPrecoProduto);
        tvDescricaoProduto = findViewById(R.id.tvDescricaoProduto);
        tvEmpresaProduto = findViewById(R.id.tvEmpresaProduto);
        imgProduto = findViewById(R.id.imgProduto);
        Button btnComprar = findViewById(R.id.btnComprarProduto);
        Button btnNegociar = findViewById(R.id.btnNegociarProduto);

        //Instanciando objeto para verificar conexão
        verificaConexao = new VerificaConexao(this);

        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarCompra();
            }
        });

        //Recuperando dados do firebase e setando campos da activity
        recuperarDados();

        tvEmpresaProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaVisualizarEmpresaActivity();

            }
        });
    }
    //Recuperando dados do firebase
    private void recuperarDados(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Produto produto = dataSnapshot.child("produto").child(idProduto).getValue(Produto.class);
                Pessoa pessoa = dataSnapshot.child("pessoa").child(idEmpresa).getValue(Pessoa.class);

                if (produto != null && pessoa != null) {
                    setarCampos(pessoa, produto);
                    setarFoto(produto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //Setando campo foto do produto
    private void setarFoto(Produto produto){
        Bitmap bitmap = Helper.stringToBitMap(produto.getBitmapImagemProduto());
        Glide.with(getApplicationContext()).load(bitmap).into(imgProduto);
    }
    //Setando campos da activity
    private void setarCampos(Pessoa pessoa, Produto produto) {
        tvNomeProduto.setText(produto.getNomeProduto());
        tvQuantidadeEstoqueProduto.setText(String.valueOf(produto.getQuantidadeEstoque()));
        tvPrecoProduto.setText(MoneyTextWatcher.convertStringToMoney(String.valueOf(produto.getPrecoSugerido())));
        tvDescricaoProduto.setText(produto.getDescricao());
        tvEmpresaProduto.setText(pessoa.getNome());
    }
    //Intent para a tela de visualização da empresa
    private void abrirTelaVisualizarEmpresaActivity(){
        Intent intent = new Intent(getApplicationContext(), VisualizarEmpresaActivity.class);
        intent.putExtra("idEmpresa", idEmpresa);
        startActivity(intent);
    }
    //Método que abre a caixa de diálogo
    private void iniciarCompra () {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(VisualizarProdutoActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.modelo_caixa_dialogo_compra, null);

        //Instanciando views
        instanciandoViews(mView);

        //Setando informações das views
        setarInformacoesViews();

        builder.setView(mView);
        alertaCompra = builder.create();
        alertaCompra.show();

        //Chamando método para tratar o clique no botão voltar
        clickVoltar();

        //Chamando método para tratar o clique no botão comprar
        clickComprar();
    }
    //Validando edit text da quantidade digitavda pelo usuário na caixa de diálogo
    private boolean validarCampos(){
        boolean verificador = true;

        if (edtQuantidadeDialogoCompra.getText().toString().isEmpty() || edtQuantidadeDialogoCompra.getText().toString().trim().length() == 0){
            edtQuantidadeDialogoCompra.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        return verificador;
    }
    //Método que instancia as views da caixa de diálogo
    private void instanciandoViews(View mView){
        edtQuantidadeDialogoCompra = mView.findViewById(R.id.edtQuantidadeDesejadaDialogoCompra);
        tvNomeProdutoDialogoCompra = mView.findViewById(R.id.tvNomeProdutoDialogoCompra);
        tvTotalDialogoCompra = mView.findViewById(R.id.tvTotalDialogoCompra);
        btnVoltarDialogoCompra = mView.findViewById(R.id.btnVoltarDialogoCompra);
        btnComprarDialogoCompra = mView.findViewById(R.id.btnComprarDialogoCompra);
    }
    //Método que seta as informações para as views da caixa de diálogo
    private void setarInformacoesViews(){
        tvNomeProdutoDialogoCompra.setText(tvNomeProduto.getText());
        edtQuantidadeDialogoCompra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtQuantidadeDialogoCompra.getText().toString().isEmpty() ||
                        !(edtQuantidadeDialogoCompra.getText().toString().trim().length() == 0)){
                    int quantidadeItens = Integer.valueOf(edtQuantidadeDialogoCompra.getText().toString());
                    BigDecimal precoItem = MoneyTextWatcher.convertToBigDecimal(tvPrecoProduto.getText().toString());
                    tvTotalDialogoCompra.setText(MoneyTextWatcher.convertStringToMoney(String.valueOf(precoItem.multiply(new BigDecimal(quantidadeItens)))));
                }
                BigDecimal bigDecimal = MoneyTextWatcher.convertToBigDecimal(tvTotalDialogoCompra.getText().toString());
                BigDecimal bigDecimal1 = new BigDecimal(50000);
                if ((bigDecimal.compareTo(bigDecimal1)) == -1){
                    edtQuantidadeDialogoCompra.setError("Valor de compra excedido!Tente comprar uma quantidade menor.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!edtQuantidadeDialogoCompra.getText().toString().isEmpty() ||
                        !(edtQuantidadeDialogoCompra.getText().toString().trim().length() == 0)){
                    int quantidadeItens = Integer.valueOf(edtQuantidadeDialogoCompra.getText().toString());
                    BigDecimal precoItem = MoneyTextWatcher.convertToBigDecimal(tvPrecoProduto.getText().toString());
                    tvTotalDialogoCompra.setText(MoneyTextWatcher.convertStringToMoney(String.valueOf(precoItem.multiply(new BigDecimal(quantidadeItens)))));
                }
                BigDecimal bigDecimal = MoneyTextWatcher.convertToBigDecimal(tvTotalDialogoCompra.getText().toString());
                BigDecimal bigDecimal1 = new BigDecimal(50000);
                if ((bigDecimal.compareTo(bigDecimal1)) == -1){
                    edtQuantidadeDialogoCompra.setError("Valor de compra excedido!Tente comprar uma quantidade menor.");
                }
            }
        });
    }
    //Método que implementa o evento de click do botão voltar
    private void clickVoltar(){
        btnVoltarDialogoCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaCompra.dismiss();
            }
        });
    }
    //Método que implementa o evento de click do botão comprar
    private void clickComprar(){
        btnComprarDialogoCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    if (validarCampos()){
                        Helper.criarToast(getApplicationContext(), "Em construção...");
                    }
                }
            }
        });
    }
}
