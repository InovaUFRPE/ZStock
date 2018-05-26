package com.zstok.produto.adapter;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zstok.R;

public class ProdutoListHolder extends RecyclerView.ViewHolder {

    //public int currentItem;
    public ImageView imgCardViewProduto;
    public TextView tvCardViewNomeProduto;
    public TextView tvCardViewPrecoProduto;
    public TextView tvCardViewQuantidadeEstoque;
    public TextView tvCardViewNomeEmpresa;
    public View mainLayout;
    public View linearLayout;


    public ProdutoListHolder (final View itemView){
        super(itemView);

        imgCardViewProduto = itemView.findViewById(R.id.imgCardViewProduto);
        tvCardViewNomeProduto = itemView.findViewById(R.id.nomeCardViewProduto);
        tvCardViewPrecoProduto = itemView.findViewById(R.id.precoCardViewProduto);
        tvCardViewQuantidadeEstoque = itemView.findViewById(R.id.quantidadeCardViewProduto);
        tvCardViewNomeEmpresa = itemView.findViewById(R.id.nomeEmpresaCardViewProduto);
        mainLayout = itemView.findViewById(R.id.cardViewProduto);
        linearLayout = itemView.findViewById(R.id.produtoCard);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();

                Snackbar.make(v, "Click detected on item " + position, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }
}