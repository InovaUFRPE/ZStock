package com.zstok.carrinhoCompra.gui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.carrinhoCompra.adapter.ItemCompraListHolder;
import com.zstok.carrinhoCompra.negocio.CarrinhoCompraServices;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.produto.dominio.Produto;

import java.text.NumberFormat;

public class CarrinhoCompraActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerViewItens;
    private TextView tvTotalCardViewItemCompra;

    private VerificaConexao verificaConexao;

    private double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho_compra);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Instanciando view
        tvTotalCardViewItemCompra = findViewById(R.id.tvTotalCardViewItemCompra);
        Button btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Instanciando recyler view
        recyclerViewItens = findViewById(R.id.recyclerItensCarrinho);
        recyclerViewItens.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CarrinhoCompraActivity.this);
        recyclerViewItens.setLayoutManager(layoutManager);

        //Inicializando o objeto da classe VerificaConexao
        verificaConexao = new VerificaConexao(this);

        //Preenchendo recyclerView
        criarAdapter();

        FirebaseController.getFirebase().child("produto").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                atualizarCarrinhoCompra(dataSnapshot.getKey());
                //Preenchendo recyclerView
                criarAdapter();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Evento finalizar compra
        btnFinalizarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    //1 - Verificar se produto esta disponível 2- Diminuir quantidade firebase
                    FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            verificaQuantidade(dataSnapshot);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
    //Verificando quantidade da compra
    private void verificaQuantidade(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> produtosCarrinho = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
        //Validando quantidade
        for(DataSnapshot itemSnapshot: produtosCarrinho){
            ItemCompra itemCompra = itemSnapshot.getValue(ItemCompra.class);
            Produto produtoCompra = dataSnapshot.child("produto").child(itemCompra.getIdProduto()).getValue(Produto.class);
            if (itemCompra.getQuantidade() > produtoCompra.getQuantidadeEstoque() ) {
                Helper.criarToast(getApplicationContext(), "Quantidade de " + produtoCompra.getNome() + " indisponível!");
                break;
            }else {
                reduzirQuantidade(produtoCompra, itemCompra);
            }
        }
    }
    private void reduzirQuantidade(Produto produto, ItemCompra itemCompra){
        int quantidadeNova = produto.getQuantidadeEstoque() - itemCompra.getQuantidade();
        produto.setQuantidadeEstoque(quantidadeNova);
        //Chamando camada de negócio para inserir nova quantidade
        CarrinhoCompraServices.reduzirQuantidade(produto);
    }
    //Método que atualiza carrinho compra
    private void atualizarCarrinhoCompra(final String idAlterado){
        FirebaseController.getFirebase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).exists())) {
                    Produto produto = dataSnapshot.child("produto").child(idAlterado).getValue(Produto.class);
                    Double total = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").getValue(Double.class);
                    if (produto != null) {
                        Iterable<DataSnapshot> itensCompra = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
                        for (DataSnapshot dataSnapshotChild: itensCompra){
                            ItemCompra itemCompra = dataSnapshotChild.getValue(ItemCompra.class);
                            String idProduto = dataSnapshotChild.child("idProduto").getValue(String.class);
                            if ((idProduto.equals(idAlterado))) {
                                if (alterarValorItemCompra(itemCompra, produto)){
                                    inserirTotal(produto, total, itemCompra);
                                }else {
                                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
                                }
                            }else {
                                Helper.criarToast(getApplicationContext(), idAlterado);
                            }
                        }
                    }
                }else{
                    Helper.criarToast(getApplicationContext(), "Carrinho vazio");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inserirTotal(Produto produto, Double total, ItemCompra itemCompra) {
        itemCompra.setValor(produto.getPrecoSugerido());
        double difPrecoTotal = Math.abs((itemCompra.getValor() * itemCompra.getQuantidade()) - total);
        double novoTotal = Math.abs(total - difPrecoTotal);
        
        CarrinhoCompraServices.inserirTotal(novoTotal);
    }

    //Método que calcula o novo total
    private boolean alterarValorItemCompra(ItemCompra itemCompra, Produto produto) {
        return CarrinhoCompraServices.alterarValorItemCompra(itemCompra, produto);
    }
    //Montando adapter e jogando no list holder
    private void criarAdapter() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra");

        if (databaseReference != null) {

            adapter = new FirebaseRecyclerAdapter<ItemCompra, ItemCompraListHolder>(
                    ItemCompra.class,
                    R.layout.card_item_compra,
                    ItemCompraListHolder.class,
                    databaseReference) {

                @Override
                protected void populateViewHolder(final ItemCompraListHolder viewHolder, final ItemCompra model, int position) {
                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                    viewHolder.linearLayout.setVisibility(View.VISIBLE);

                    FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Produto produto = dataSnapshot.child("produto").child(model.getIdProduto()).getValue(Produto.class);
                            if (produto != null) {
                                Pessoa pessoa = dataSnapshot.child("pessoa").child(produto.getIdEmpresa()).getValue(Pessoa.class);
                                if (pessoa != null){
                                    viewHolder.tvCardViewNomeItemCompra.setText(produto.getNome());
                                    viewHolder.tvCardViewPrecoItemCompra.setText(NumberFormat.getCurrencyInstance().format(produto.getPrecoSugerido()));
                                    viewHolder.tvCardViewQuantidadeItemCompra.setText(String.valueOf(model.getQuantidade()));
                                    viewHolder.tvCardViewNomeEmpresa.setText(pessoa.getNome());
                                    if (produto.getUrlImagem() != null) {
                                        Glide.with(getApplicationContext()).load(produto.getUrlImagem()).into(viewHolder.imgCardViewItemCompra);
                                    }
                                    if (getItemCount() == 0){
                                        calcularTotal(produto.getPrecoSugerido(), model.getQuantidade());
                                    }else {
                                        tvTotalCardViewItemCompra.setText(NumberFormat.getCurrencyInstance().format(dataSnapshot.child("carrinhoCompra")
                                                .child(FirebaseController.getUidUser()).child("total").getValue(Double.class)));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            };
            recyclerViewItens.setAdapter(adapter);
        }
    }
    //Calculando total
    private void calcularTotal(double preco, int quantidade) {
        total+=(preco*quantidade);
        tvTotalCardViewItemCompra.setText(NumberFormat.getCurrencyInstance().format(total));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.carrinho_compra, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}