package com.zstok.historico.dominio;

import com.zstok.itemcompra.dominio.ItemCompra;

import java.util.ArrayList;

public class Historico {

    private String idHistorico;
    private String idPessoaJuridica;
    private String idPessoaFisica;
    private String dataCompra;
    private double total;
    private ArrayList<ItemCompra> carrinho;

    public String getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(String idHistorico) {
        this.idHistorico = idHistorico;
    }

    public String getIdPessoaJuridica() {
        return idPessoaJuridica;
    }

    public void setIdPessoaJuridica(String cnpj) {
        this.idPessoaJuridica = cnpj;
    }

    public String getIdPessoaFisica() {
        return idPessoaFisica;
    }

    public void setIdPessoaFisica(String cpf) {
        this.idPessoaFisica = cpf;
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}