package com.zstok.compra.dominio;

import com.zstok.itemcompra.dominio.ItemCompra;

import java.util.ArrayList;
import java.util.List;

public class Compra {
    private String idCompra;
    private Double totalCompra;
    private List<ItemCompra> listaItemCompra = new ArrayList<>();

    public String getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(String idCompra) {
        this.idCompra = idCompra;
    }

    public Double getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(Double totalCompra) {
        this.totalCompra = totalCompra;
    }

    public List<ItemCompra> getListaItemCompra() {
        return listaItemCompra;
    }

    public void setListaItemCompra(List<ItemCompra> listaItemCompra) {
        this.listaItemCompra = listaItemCompra;
    }

}
