package com.zstok.itemcompra.dominio;

public class ItemCompra {

    private String idItemCompra;
    private String nome;
    private Double valor;
    private int quantidade;
    private Double desconto;
    private Double total = (valor*quantidade)-((valor*quantidade)*(desconto/100));


    public String getIdItemCompra() {
        return idItemCompra;
    }

    public void setIdItemCompra(String idItemCompra) {
        this.idItemCompra = idItemCompra;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
