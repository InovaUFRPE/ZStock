package com.zstok.produto.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zstok.R;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.MoneyTextWatcher;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.produto.dominio.Produto;
import com.zstok.produto.negocio.ProdutoServices;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarProdutoActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALERY_REQUEST_CODE = 71;

    private String idProduto;

    private CircleImageView cvImagemProduto;
    private EditText edtNomeProduto;
    private EditText edtPrecoProduto;
    private EditText edtQuantidadeEstoqueProduto;
    private EditText edtDescricaoProduto;

    private VerificaConexao verificaConexao;

    private Uri uriFoto;

    private ProgressDialog progressDialog;

    private StorageReference referenciaStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_produto);

        //Resgatando id do produto enviado pela intent
        idProduto = getIntent().getStringExtra("idProduto");

        //Instanciando progress dialog
        progressDialog = new ProgressDialog(this);

        //Instancinado storage do firebase
        referenciaStorage = FirebaseStorage.getInstance().getReference();

        cvImagemProduto = findViewById(R.id.cvEditarProduto);
        edtNomeProduto = findViewById(R.id.edtNomeProduto);
        edtPrecoProduto = findViewById(R.id.edtPrecoProduto);
        edtQuantidadeEstoqueProduto = findViewById(R.id.edtQuantidadeEstoqueProduto);
        edtDescricaoProduto = findViewById(R.id.edtDescricaoProduto);

        verificaConexao = new VerificaConexao(this);

        //Mascara Monetária
        Locale mLocale = new Locale("pt", "BR");
        edtPrecoProduto.addTextChangedListener(new MoneyTextWatcher(edtPrecoProduto,mLocale));

        FirebaseController.getFirebase().child("produto").child(idProduto)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Produto produto = dataSnapshot.getValue(Produto.class);
                if (produto != null) {
                    if (produto.getUrlImagem() != null) {
                        recuperarFoto(produto);
                    }
                    edtNomeProduto.setText(produto.getNome());
                    edtPrecoProduto.setText(NumberFormat.getCurrencyInstance().format(produto.getPrecoSugerido()));
                    edtQuantidadeEstoqueProduto.setText(String.valueOf(produto.getQuantidadeEstoque()));
                    edtDescricaoProduto.setText(produto.getDescricao());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Button btnAlterarProduto = findViewById(R.id.btnAlterarProduto);

        btnAlterarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()){
                    if (verificaConexao.isConected()){
                        alterarProduto(criarProduto());
                    }
                }
            }
        });

        FloatingActionButton fabAbrirGaleriaEditarProduto = findViewById(R.id.fabAbrirGaleriaEditarProduto);
        fabAbrirGaleriaEditarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherFoto();
            }
        });

        FloatingActionButton fabAbrirCameraEditarProduto =  findViewById(R.id.fabAbrirCameraEditarProduto);
        fabAbrirCameraEditarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tirarFoto();
            }
        });
    }
    //Resgatando foto do Storage
    private void recuperarFoto(Produto produto){
        iniciarProgressDialog();
        Glide.with(EditarProdutoActivity.this).load(Uri.parse(produto.getUrlImagem())).into(cvImagemProduto);
        progressDialog.dismiss();
    }
    //Método que inicia o progress dialog
    private void iniciarProgressDialog() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.zs_titulo_progress_dialog_perfil));
        progressDialog.show();
    }
    //Validar campos
    private boolean validarCampos(){
        boolean verificador = true;

        if (edtNomeProduto.getText().toString().isEmpty() || edtNomeProduto.getText().toString().trim().length() == 0){
            edtNomeProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtPrecoProduto.getText().toString().isEmpty() || edtPrecoProduto.getText().toString().trim().length() == 0){
            edtPrecoProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtQuantidadeEstoqueProduto.getText().toString().isEmpty() || edtQuantidadeEstoqueProduto.getText().toString().trim().length() == 0){
            edtQuantidadeEstoqueProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtQuantidadeEstoqueProduto.getText().toString().isEmpty() || edtQuantidadeEstoqueProduto.getText().toString().trim().length() == 0){
            edtQuantidadeEstoqueProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtDescricaoProduto.getText().toString().isEmpty() || edtDescricaoProduto.getText().toString().trim().length() == 0){
            edtDescricaoProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        return verificador;
    }
    //Método que preenche objeto produto
    private Produto criarProduto(){
        Produto produto = new Produto();

        produto.setNome(edtNomeProduto.getText().toString());
        produto.setNomePesquisa(Helper.removerAcentos(edtNomeProduto.getText().toString().toLowerCase()));
        produto.setPrecoSugerido(MoneyTextWatcher.convertToBigDecimal(edtPrecoProduto.getText().toString()).doubleValue());
        produto.setQuantidadeEstoque(Integer.valueOf(edtQuantidadeEstoqueProduto.getText().toString()));
        produto.setDescricao(edtDescricaoProduto.getText().toString());
        produto.setIdProduto(idProduto);
        if (uriFoto != null) {
            produto.setUrlImagem(uriFoto.toString());
        }
        return produto;
    }
    //Inserindo imagem no banco
    private void alterarProduto(Produto produto){
        if (ProdutoServices.alterarProduto(produto)){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_produto_alterado_sucesso));
            abrirTelaMeusProdutosActivity();
        } else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Método que abre a galeria
    private void escolherFoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Selecione uma imagem"), GALERY_REQUEST_CODE);
    }
    //Método que abre a câmera
    private void tirarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALERY_REQUEST_CODE:
                Bitmap bitmapCadstrarProduto;
            {
                if (requestCode == GALERY_REQUEST_CODE && resultCode == RESULT_OK) {
                    uriFoto = data.getData();
                    Glide.with(EditarProdutoActivity.this).load(uriFoto).into(cvImagemProduto);
                }
            }
            case CAMERA_REQUEST_CODE: {
                if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            bitmapCadstrarProduto = (Bitmap) extras.get("data");
                            uriFoto = Helper.getImageUri(getApplicationContext(), bitmapCadstrarProduto);
                            cvImagemProduto.setImageBitmap(bitmapCadstrarProduto);
                        }
                    }
                }
            }
        }
    }
    //Intent para abrir meus produtos
    private void abrirTelaMeusProdutosActivity(){
        Intent intent = new Intent(getApplicationContext(), MeusProdutosActivity.class);
        startActivity(intent);
        finish();
    }
}