package com.zstok.negociacao.negocio;

import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.negociacao.persistencia.NegociacaoDAO;

public class NegociacaoServices {

    public static void inserirNegociacao(Negociacao negociacao){
        NegociacaoDAO.inserirNegociacao(negociacao);
    }
}
