package com.viniciusog.anunciosapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.viniciusog.anunciosapp.R;
import com.viniciusog.anunciosapp.model.Anuncio;

public class DetalhesProdutosActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView textTitulo;
    private TextView textPreco;
    private TextView textEstado;
    private TextView textDescricao;
    private FloatingActionButton floatingButtonVerTelefone;

    private Anuncio anuncioSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produtos);

        //Configura toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalhe anúncio");

        //Inicializar componentes
        inicializarComponentes();

        //Recupera anuncio para exibição
        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");

        if ( anuncioSelecionado != null) {
            textTitulo.setText(anuncioSelecionado.getTitulo());
            textPreco.setText(anuncioSelecionado.getValor());
            textEstado.setText(anuncioSelecionado.getEstado());
            textDescricao.setText(anuncioSelecionado.getDescricao());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = anuncioSelecionado.getFotos().get(position);
                    //Carrega a imagem no imageView do carrouselView
                    Picasso.get().load(urlString).into(imageView);
                }
            };

            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener( imageListener );
        }

        //Adiciona evento de clique no floating action button
        floatingButtonVerTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visualizarTelefone();
            }
        });
    }

    private void visualizarTelefone() {
        //Inicia uma chamada com o telefone do anuncio
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null));
        startActivity(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void inicializarComponentes() {
        carouselView = findViewById(R.id.carouselView);
        textTitulo = findViewById(R.id.textTituloDetalhe);
        textPreco = findViewById(R.id.textPrecoDetalhe);
        textEstado = findViewById(R.id.textEstadoDetalhe);
        textDescricao = findViewById(R.id.textDescricaoDetalhe);
        floatingButtonVerTelefone = findViewById(R.id.floatingButtonVerTelefone);
    }
}