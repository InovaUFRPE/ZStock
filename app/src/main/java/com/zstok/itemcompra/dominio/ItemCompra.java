package com.zstok.itemcompra.dominio;

public class ItemCompra {

    private String idItemCompra;
    private String idProduto;
    private Double valor;
    private int quantidade;

    public String getIdItemCompra() {
        return idItemCompra;
    }
    public void setIdItemCompra(String idItemCompra) {
        this.idItemCompra = idItemCompra;
    }
    public Double getValor() {
        return valor;
    }
    public void setValor(Double valor) {
        this.valor = valor;
    }
    public Integer getQuantidade() {
        return quantidade;
    }
    public String getIdProduto() {
        return idProduto;
    }
    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
