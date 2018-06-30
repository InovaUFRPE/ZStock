package com.zstok.historico.dominio;

import com.zstok.itemcompra.dominio.ItemCompra;

import java.util.ArrayList;

public class Historico {

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(String dataCompra) {
        this.dataCompra = dataCompra;
    }

    public ArrayList<ItemCompra> getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(ArrayList<ItemCompra> carrinho) {
        this.carrinho = carrinho;
    }

    private String cnpj;
    private String cpf;
    private String dataCompra;
    private ArrayList<ItemCompra> carrinho;



}
