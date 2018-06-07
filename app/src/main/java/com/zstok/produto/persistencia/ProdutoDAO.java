package com.zstok.produto.persistencia;

import android.net.Uri;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.produto.dominio.Produto;

public class ProdutoDAO {
    //Inserindo produto no banco de dados
    public static boolean insereProduto(Produto produto){
        boolean verificador;

        try {
            produto.setIdProduto(FirebaseController.getFirebase().child("produto").push().getKey());
            FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).setValue(produto);
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
            FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).setValue(null);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Retorno alterar produto para GUI
    public static boolean alterarProdutoVerificador(Produto produto){
        boolean verificador;

        try {
            alterarProduto(produto);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Alterando produto da árvore de visão do cliente
    private static void alterarProduto(Produto produto) {
        if (produto.getBitmapImagemProduto() != null){
            FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("bitmapImagemProduto").setValue(produto.getBitmapImagemProduto());
        }
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("nomeProduto").setValue(produto.getNomeProduto());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("precoSugerido").setValue(produto.getPrecoSugerido());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("quantidadeEstoque").setValue(produto.getQuantidadeEstoque());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("descricao").setValue(produto.getDescricao());
    }
    //Método provisório
    public static boolean comprarProduto(String idProduto, int novaQuantidade){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("produto").child(idProduto).child("quantidadeEstoque").setValue(String.valueOf(novaQuantidade));
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}
