package com.zstok.negociacao.persistencia;

import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.produto.dominio.Produto;

public class NegociacaoDAO {

    public static void inserirNegociacao(Negociacao negociacao){
        negociacao.setIdNegociacao(FirebaseController.getFirebase().push().getKey());
        FirebaseController.getFirebase().child("negociacao").child(negociacao.getIdNegociacao()).setValue(negociacao);
    }

    public static boolean finalizarNegociacao(Negociacao negociacao){
        boolean verificador;

        try{
            FirebaseController.getFirebase().child("negociacao").child(negociacao.getIdNegociacao()).setValue(negociacao);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }

    public static void diminuirQuantidade(Produto produto, ItemCompra itemCompra){
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("quantidadeEstoque").setValue(produto.getQuantidadeEstoque() - itemCompra.getQuantidade());
    }
}
