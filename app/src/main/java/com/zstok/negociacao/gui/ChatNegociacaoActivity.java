package com.zstok.negociacao.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zstok.R;
import com.zstok.infraestrutura.utils.VerificaConexao;

public class ChatNegociacaoActivity extends AppCompatActivity {

    private TextView tvNegociacaoNomeEmpresa;
    private EditText edtNegociacaoBarraMensagem;
    private ListView lvMensagens;

    private VerificaConexao verificaConexao;


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

    }
}
