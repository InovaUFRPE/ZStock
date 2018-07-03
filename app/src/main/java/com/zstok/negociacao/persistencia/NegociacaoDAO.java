package com.zstok.negociacao.persistencia;

import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.negociacao.dominio.Negociacao;

public class NegociacaoDAO {

    public static void inserirNegociacao(Negociacao negociacao){
        negociacao.setIdNegociacao(FirebaseController.getFirebase().push().getKey());
        FirebaseController.getFirebase().child("negociacao").child(negociacao.getIdNegociacao()).setValue(negociacao);
    }
}
