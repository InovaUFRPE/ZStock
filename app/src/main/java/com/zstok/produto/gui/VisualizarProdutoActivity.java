package com.zstok.produto.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.produto.dominio.Produto;

public class VisualizarProdutoActivity extends AppCompatActivity {

    private String idEmpresa;
    private String idProduto;
    private TextView tvNomeProduto;
    private TextView tvQuantidadeEstoqueProduto;
    private TextView tvPrecoProduto;
    private TextView tvDescricaoProduto;
    private TextView tvEmpresaProduto;
    private ImageView imgProduto;

    //Caixa Dialógo
    private EditText edtQuantidadeCaixaDialogoCompra;


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
    //Setando campos da activity
    private void setarCampos(Pessoa pessoa, Produto produto) {
        tvNomeProduto.setText(produto.getNomeProduto());
        tvQuantidadeEstoqueProduto.setText(String.valueOf(produto.getQuantidadeEstoque()));
        tvPrecoProduto.setText(MoneyTextWatcher.convertStringToMoney(String.valueOf(produto.getPrecoSugerido())));
        tvDescricaoProduto.setText(produto.getDescricao());
        tvEmpresaProduto.setText(pessoa.getNome());
    }

    private void setarFoto(Produto produto){
        Bitmap bitmap = Helper.stringToBitMap(produto.getBitmapImagemProduto());
        Glide.with(getApplicationContext()).load(bitmap).into(imgProduto);

    }

    //Intent para a tela de visualização da empresa
    private void abrirTelaVisualizarEmpresaActivity(){
        Intent intent = new Intent(getApplicationContext(), VisualizarEmpresaActivity.class);
        intent.putExtra("idEmpresa", idEmpresa);
        startActivity(intent);
    }
    private void iniciarCompra () {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        View mview = getLayoutInflater().inflate(R.layout.modelo_caixa_dialogo_compra, null);
        //Instanciando views

    }
}
