package com.zstok.compra.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zstok.R;

public class ItemCompraListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //public int currentItem;
    public ImageView imgCardViewItemCompra;
    public TextView tvCardViewNomeItemCompra;
    public TextView tvCardViewPrecoItemCompra;
    public TextView tvCardViewQuantidadeItemCompra;
    public TextView tvCardViewNomeEmpresa;
    public View mainLayout;
    public View linearLayout;
    private ItemCompraListHolder.ClickListener itemClickListener;


    public ItemCompraListHolder(final View itemView) {
        super(itemView);

        imgCardViewItemCompra = itemView.findViewById(R.id.imgCardViewItemCompra);
        tvCardViewNomeItemCompra = itemView.findViewById(R.id.nomeCardViewItemCompra);
        tvCardViewPrecoItemCompra = itemView.findViewById(R.id.precoCardViewItemCompra);
        tvCardViewQuantidadeItemCompra = itemView.findViewById(R.id.quantidadeCardViewItemCompra);
        tvCardViewNomeEmpresa = itemView.findViewById(R.id.nomeEmpresaCardViewItemCompra);
        mainLayout = itemView.findViewById(R.id.cardViewItemCompra);
        linearLayout = itemView.findViewById(R.id.itemCompraCard);

        itemView.setOnClickListener(this);

    }

    public void setVisibility(boolean isVisible) {
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        if (isVisible) {
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        } else {
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(ItemCompraListHolder.ClickListener clickListener) {
        itemClickListener = clickListener;
    }
}
