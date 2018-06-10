package com.zstok.produto.dominio;

public class Produto {
    private String idProduto;
    private String idEmpresa;
    private int quantidadeEstoque;
    private String nomeProduto;
    private String descricao;
    private String bitmapImagemProduto;
    private double precoSugerido;

    public String getNomeProduto() {
        return nomeProduto;
    }
    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public double getPrecoSugerido() {
        return precoSugerido;
    }
    public void setPrecoSugerido(double precoSugerido) {
        this.precoSugerido = precoSugerido;
    }
    public String getIdProduto(){
        return idProduto;
    }
    public void setIdProduto(String idProduto){
        this.idProduto = idProduto;
    }
    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }
    public String getBitmapImagemProduto() {
        return bitmapImagemProduto;
    }
    public void setBitmapImagemProduto(String bitmapImagemProduto) {
        this.bitmapImagemProduto = bitmapImagemProduto;
    }
    public String getIdEmpresa() {
        return idEmpresa;
    }
    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
}