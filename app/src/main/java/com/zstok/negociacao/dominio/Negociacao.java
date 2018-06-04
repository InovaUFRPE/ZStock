package com.zstok.negociacao.dominio;

import com.zstok.produto.dominio.Produto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Negociacao {
    
    private String idNegociacao;
    private String idPessoaFisica;
    private String idPessoaJuridica;
    private Date dataInicio;
    private Date dataFim;
    private List<Produto> listaProduto = new ArrayList<>();

    public String getIdNegociacao() {
        return idNegociacao;
    }

    public void setIdNegociacao(String idNegociacao) {
        this.idNegociacao = idNegociacao;
    }

    public String getIdPessoaFisica() {
        return idPessoaFisica;
    }

    public void setIdPessoaFisica(String idPessoaFisica) {
        this.idPessoaFisica = idPessoaFisica;
    }

    public String getIdPessoaJuridica() {
        return idPessoaJuridica;
    }

    public void setIdPessoaJuridica(String idPessoaJuridica) {
        this.idPessoaJuridica = idPessoaJuridica;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public List<Produto> getListaProduto() {
        return listaProduto;
    }

    public void setListaProduto(List<Produto> listaProduto) {
        this.listaProduto = listaProduto;
    }

}
