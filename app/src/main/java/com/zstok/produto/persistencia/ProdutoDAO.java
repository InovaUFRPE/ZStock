package com.zstok.produto.persistencia;

import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.produto.dominio.Produto;

public class ProdutoDAO {
    //Inserindo produto no banco de dados
    public static boolean insereProduto(Produto produto){
        boolean verificador;

        try {
            produto.setIdProduto(FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).push().getKey());
            FirebaseController.getFirebase().child("produtoFornecedor").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).setValue(produto);
            FirebaseController.getFirebase().child("produtoCliente").child(produto.getIdProduto()).setValue(produto);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Excluindo produto do banco de dados
    public static boolean excluirProduto(Produto produto){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("produtoFornecedor").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).setValue(null);
            FirebaseController.getFirebase().child("produtoCliente").child(produto.getIdProduto()).setValue(null);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Alterando produto do banco de dados
    public static boolean alterarProduto(Produto produto){
        boolean verificador;

        try {
            alterarProdutoFornecedor(produto);
            alterarProdutoCliente(produto);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Alterando produto da árvore de visão do cliente
    private static void alterarProdutoCliente(Produto produto){
        if (produto.getBitmapImagemProduto() != null){
            FirebaseController.getFirebase().child("produtoCliente").child(produto.getIdProduto()).child("bitmapImagemProduto").setValue(produto.getBitmapImagemProduto());
        }
        FirebaseController.getFirebase().child("produtoCliente").child(produto.getIdProduto()).child("nomeProduto").setValue(produto.getNomeProduto());
        FirebaseController.getFirebase().child("produtoCliente").child(produto.getIdProduto()).child("preco").setValue(produto.getPrecoSugerido());
        FirebaseController.getFirebase().child("produtoCliente").child(produto.getIdProduto()).child("quantidadeEstoque").setValue(produto.getQuantidadeEstoque());
        FirebaseController.getFirebase().child("produtoCliente").child(produto.getIdProduto()).child("descricao").setValue(produto.getDescricao());
    }
    //Alterando produto da árvore de visão do fornecedor
    private static void alterarProdutoFornecedor(Produto produto) {
        if (produto.getBitmapImagemProduto() != null){
            FirebaseController.getFirebase().child("produtoFornecedor").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).child("bitmapImagemProduto").setValue(produto.getBitmapImagemProduto());
        }
        FirebaseController.getFirebase().child("produtoFornecedor").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).child("nomeProduto").setValue(produto.getNomeProduto());
        FirebaseController.getFirebase().child("produtoFornecedor").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).child("preco").setValue(produto.getPrecoSugerido());
        FirebaseController.getFirebase().child("produtoFornecedor").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).child("quantidadeEstoque").setValue(produto.getQuantidadeEstoque());
        FirebaseController.getFirebase().child("produtoFornecedor").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).child("descricao").setValue(produto.getDescricao());
    }
}
