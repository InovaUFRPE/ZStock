package com.zstok.carrinhoCompra.persistencia;

import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.produto.dominio.Produto;

public class CarrinhoCompraDAO {

    public static void reduzirQuantidade(Produto produto){
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("quantidadeEstoque").setValue(produto.getQuantidadeEstoque());
    }

    public static boolean alterarValorItemCompra(ItemCompra itemCompra, Produto produto){
        boolean verificador;

        try{
            FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").child(itemCompra.getIdItemCompra()).child("valor").setValue(produto.getPrecoSugerido());
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }

    public static void inserirToral(double novoTotal){
        FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").setValue(novoTotal);
    }
}
