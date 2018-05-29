package com.zstok.produto.persistencia;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.produto.dominio.Produto;

public class ProdutoDAO {
    //Inserindo produto no banco de dados
    public static boolean insereProduto(Produto produto){
        boolean verificador;

        try {
            produto.setIdProduto(FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).push().getKey());
            FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).setValue(produto);
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
            FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).setValue(null);
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
            if (produto.getBitmapImagemProduto() != null){
                FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).child("bitmapImagemProduto").setValue(produto.getBitmapImagemProduto());
            }
            FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).child("nomeProduto").setValue(produto.getNomeProduto());
            FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).child("preco").setValue(produto.getPreco());
            FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).child("quantidadeEstoque").setValue(produto.getQuantidadeEstoque());
            FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).child(produto.getIdProduto()).child("descricao").setValue(produto.getDescricao());
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}
