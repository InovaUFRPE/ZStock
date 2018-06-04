package com.zstok.negociacao.dominio;

import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.produto.dominio.Produto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Negociacao {

    private String idNegociacao;

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

    public List<ItemCompra> getListaProduto() {
        return listaProduto;
    }

    public void setListaProduto(List<ItemCompra> listaProduto) {
        this.listaProduto = listaProduto;
    }

    private String idPessoaFisica;
    private String idPessoaJuridica;
    private Date dataInicio;
    private Date dataFim;
    private List<ItemCompra> listaProduto = new ArrayList<>();


}
