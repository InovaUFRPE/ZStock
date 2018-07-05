package com.zstok.negociacao.negocio;

import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.negociacao.persistencia.NegociacaoDAO;
import com.zstok.produto.dominio.Produto;

public class NegociacaoServices {

    public static void inserirNegociacao(Negociacao negociacao){
        NegociacaoDAO.inserirNegociacao(negociacao);
    }

    public static boolean finalizarNegociacao(Negociacao negociacao){
        return NegociacaoDAO.finalizarNegociacao(negociacao);
    }

    public static void diminuirQuantidade(Produto produto, ItemCompra itemCompra){
        NegociacaoDAO.diminuirQuantidade(produto, itemCompra);
    }
}
