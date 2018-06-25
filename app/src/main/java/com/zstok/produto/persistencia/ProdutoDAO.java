package com.zstok.produto.persistencia;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.produto.dominio.Produto;

public class ProdutoDAO {

    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    //Inserindo foto do produto no banco de dados
    private static void insereFotoProduto(final Produto produto){
        StorageReference reference = storageReference.child("images/produtos/" + FirebaseController.getUidUser() + "/" + produto.getIdProduto() + ".bmp");
        reference.putFile(Uri.parse(produto.getUrlImagem())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                produto.setUrlImagem(taskSnapshot.getDownloadUrl().toString());
            }
        });
    }
    //Inserindo produto no banco de dados
    public static boolean insereProduto(Produto produto){
        boolean verificador;

        try {
            //Setando o idProduto
            produto.setIdProduto(FirebaseController.getFirebase().child("produto").push().getKey());
            if (produto.getUrlImagem() != null) {
                insereFotoProduto(produto);
            }
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
            insereFotoProduto(produto);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Alterando produto da árvore de visão do cliente
    private static void alterarProduto(Produto produto) {
        if (produto.getUrlImagem() != null){
            FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("urlImagem").setValue(produto.getUrlImagem());
        }
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("nome").setValue(produto.getNome());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("nomePesquisa").setValue(produto.getNomePesquisa());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("precoSugerido").setValue(produto.getPrecoSugerido());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("quantidadeEstoque").setValue(produto.getQuantidadeEstoque());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("descricao").setValue(produto.getDescricao());
    }
    //Método provisório
    public static boolean comprarProduto(String idProduto, int novaQuantidade){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("produto").child(idProduto).child("quantidadeEstoque").setValue(novaQuantidade);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}