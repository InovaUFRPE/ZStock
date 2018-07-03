package com.zstok.negociacao.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.mensagem.dominio.Mensagem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatNegociacaoActivity extends AppCompatActivity {

    private TextView tvNegociacaoNomeEmpresa;
    private EditText edtNegociacaoBarraMensagem;
    private ListView lvMensagens;
    private String idNegociacao;

    private VerificaConexao verificaConexao;
    private List<HashMap<String, String>> listaMensagem = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_negocicao);

        //Recuperando Views
        Button btnNegociacaoCarrinho = findViewById(R.id.btnNegociacaoCarrinho);
        Button btnNegociacaoOferta = findViewById(R.id.btnNegociacaoOferta);
        Button btnNegociacaoEnviarMensagem = findViewById(R.id.btnNegociacaoEnviarMensagem);
        tvNegociacaoNomeEmpresa = findViewById(R.id.tvNegociacaoNomeEmpresa);
        edtNegociacaoBarraMensagem = findViewById(R.id.edtNegociacaoBarraMensagem);
        lvMensagens = findViewById(R.id.lvMensagens);

        //Recuperando ID da negociação
        idNegociacao = getIntent().getStringExtra("idNegociacao");

        btnNegociacaoCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnNegociacaoOferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FirebaseController.getFirebase().child("chat").child(idNegociacao).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                carregarMensagens();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                carregarMensagens();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        
    }

}
