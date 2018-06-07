package com.zstok.produto.negocio;

import android.net.Uri;

import com.zstok.produto.dominio.Produto;
import com.zstok.produto.persistencia.ProdutoDAO;

public class ProdutoServices {
    public static boolean insereProduto(Produto produto){
        return ProdutoDAO.insereProduto(produto);
    }
    public static boolean excluirProduto(Produto produto){
        return ProdutoDAO.excluirProduto(produto);
    }
    public static boolean alterarProduto(Produto produto){
        return ProdutoDAO.alterarProdutoVerificador(produto);
    }
    //Método provisório
    public static boolean comprarProduto(String idProduto, int novaQuantidade){
        return ProdutoDAO.comprarProduto(idProduto, novaQuantidade);
    }
}