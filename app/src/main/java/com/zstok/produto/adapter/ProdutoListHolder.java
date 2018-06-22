package com.zstok.produto.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zstok.R;


public class ProdutoListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //public int currentItem;
    public ImageView imgCardViewProduto;
    public TextView tvCardViewNomeProduto;
    public TextView tvCardViewPrecoProduto;
    public TextView tvCardViewQuantidadeEstoque;
    public TextView tvCardViewNomeEmpresa;
    public View mainLayout;
    public View linearLayout;
    private ProdutoListHolder.ClickListener itemClickListener;


    public ProdutoListHolder(final View itemView) {
        super(itemView);

        imgCardViewProduto = itemView.findViewById(R.id.imgCardViewProduto);
        tvCardViewNomeProduto = itemView.findViewById(R.id.nomeCardViewProduto);
        tvCardViewPrecoProduto = itemView.findViewById(R.id.precoCardViewProduto);
        tvCardViewQuantidadeEstoque = itemView.findViewById(R.id.quantidadeCardViewProduto);
        tvCardViewNomeEmpresa = itemView.findViewById(R.id.nomeEmpresaCardViewProduto);
        mainLayout = itemView.findViewById(R.id.cardViewProduto);
        linearLayout = itemView.findViewById(R.id.produtoCard);

        itemView.setOnClickListener(this);

    }

    public void setVisibility(boolean isVisible){
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
        if (isVisible){
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        }else{
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null){
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(ProdutoListHolder.ClickListener clickListener){
        itemClickListener = clickListener;
    }
}