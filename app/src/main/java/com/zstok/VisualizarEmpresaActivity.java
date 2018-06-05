package com.zstok;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarEmpresaActivity extends AppCompatActivity {

    private String idEmpresa;

    private TextView tvNomeFantasiaEmpresa;
    private TextView tvRazaoSocialEmpresa;
    private TextView tvCnpjEmpresa;
    private TextView tvTelefoneEmpresa;
    private TextView tvEnderecoEmpresa;

    private StorageReference referenciaStorage;

    private CircleImageView cvImagemPerfilEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_empresa);

        //Resgatando id da empresa enviado pela intent
        idEmpresa = getIntent().getStringExtra("idEmpresa");

        //Instanciando views
        tvNomeFantasiaEmpresa = findViewById(R.id.tvNomeFantasiaEmpresa);
        tvRazaoSocialEmpresa = findViewById(R.id.tvRazaoSocialEmpresa);
        tvCnpjEmpresa = findViewById(R.id.tvCnpjEmpresa);
        tvTelefoneEmpresa = findViewById(R.id.tvTelefoneEmpresa);
        tvEnderecoEmpresa = findViewById(R.id.tvEnderecoEmpresa);
        cvImagemPerfilEmpresa = findViewById(R.id.cvEmpresa);

        //Instanciando referência do storage
        referenciaStorage = FirebaseStorage.getInstance().getReference();

        FloatingActionButton fabAvaliarEmpresa = findViewById(R.id.fabAvaliarEmpresa);
        fabAvaliarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.criarToast(getApplicationContext(), "Em construção...");
            }
        });

        //Resgatando foto do perfil da empresa
        downloadFoto();

        //Aplicando máscaras aos campos de cnpj e telefone
        aplicarMascaras();

        //Recuperando dados do firebase
        recuperarDados();
    }
    //Resgatando foto do Storage
    private void downloadFoto(){
        StorageReference ref = referenciaStorage.child("images/perfil/" + idEmpresa + ".bmp");

        try {
            final File localFile = File.createTempFile("images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap minhaFoto = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    cvImagemPerfilEmpresa.setImageBitmap(minhaFoto);
                }
            });
        } catch (IOException e) {
            Log.d("IOException downlaod", e.getMessage());
        }
    }
    private void recuperarDados(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PessoaJuridica pessoaJuridica = dataSnapshot.child("pessoaJuridica").child(idEmpresa).getValue(PessoaJuridica.class);
                Pessoa pessoa = dataSnapshot.child("pessoa").child(idEmpresa).getValue(Pessoa.class);
                if (pessoaJuridica != null && pessoa != null) {
                    setarCampos(pessoaJuridica, pessoa);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void aplicarMascaras(){
        Helper.mascaraCnpj(tvCnpjEmpresa);
        Helper.mascaraTelefone(tvTelefoneEmpresa);
    }
    //Setando campos da activity
    private void setarCampos(PessoaJuridica pessoaJuridica, Pessoa pessoa) {
        tvNomeFantasiaEmpresa.setText(pessoa.getNome());
        tvTelefoneEmpresa.setText(pessoa.getTelefone());
        tvEnderecoEmpresa.setText(pessoa.getEndereco());
        tvRazaoSocialEmpresa.setText(pessoaJuridica.getRazaoSocial());
        tvCnpjEmpresa.setText(pessoaJuridica.getCnpj());
    }
}