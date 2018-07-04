package com.zstok.carrinhoCompra.negocio;

import com.google.firebase.database.DataSnapshot;
import com.zstok.carrinhoCompra.dominio.CarrinhoCompra;
import com.zstok.carrinhoCompra.persistencia.CarrinhoCompraDAO;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.produto.dominio.Produto;

public class CarrinhoCompraServices {

    public static void reduzirQuantidade(Produto produto){
        CarrinhoCompraDAO.reduzirQuantidade(produto);
    }

    public static boolean alterarValorItemCompra(ItemCompra itemCompra, Produto produto){
        return CarrinhoCompraDAO.alterarValorItemCompra(itemCompra, produto);
    }

    public static void inserirTotal(double total, Produto produto, ItemCompra itemCompra){
        CarrinhoCompraDAO.inserirToral(total, produto, itemCompra);
    }

    public static void limparCarrinho(){
        CarrinhoCompraDAO.limparCarrinho();
    }

    public static void removerItemCarrinho(String idItemCompra){
        CarrinhoCompraDAO.removerItemCarrinho(idItemCompra);
    }

}
