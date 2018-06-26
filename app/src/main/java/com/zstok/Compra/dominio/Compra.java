package com.zstok.Compra.dominio;

import com.zstok.itemcompra.dominio.ItemCompra;

import java.util.ArrayList;
import java.util.List;

public class Compra {
    private String idCompra;
    private Double totalCompra;
    private List<String> testeLista = new ArrayList<>();
    private List<Token> testeLista2 = new ArrayList<>();

    public Compra() {
        this.testeLista.add("1");
        this.testeLista.add("2");
        Token token = new Token();
        this.testeLista2.add(token);
    }


}
