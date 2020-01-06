package com.viniciusog.anunciosapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.viniciusog.anunciosapp.R;
import com.viniciusog.anunciosapp.model.Anuncio;

import java.util.ArrayList;
import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Anuncio> anuncios = new ArrayList<>();
    private Context context;

    public AdapterAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (anuncios.size() > 0) {
            Anuncio anuncio = anuncios.get(position);
            holder.titulo.setText(anuncio.getTitulo());
            holder.valor.setText(anuncio.getValor());

            //Pega a primeira imagem da lista - obs está errado pois coloquei a posição 2 e pga a primeira
            List<String> urlFotos = anuncio.getFotos();
            if ( urlFotos.size() > 0 ) {
                String urlCapa = urlFotos.get(0);
                Picasso.get().load(urlCapa).into(holder.foto);
            } else {
                Log.i("urlFotosSize", String.valueOf(urlFotos.size()));
            }

        } else {
            Log.i("Lista", "Lista Vazia, Anuncios Adapter 40");
        }
    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo;
        TextView valor;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTituloDetalhe);
            valor = itemView.findViewById(R.id.textPreco);
            foto = itemView.findViewById(R.id.imageAnuncio);
        }
    }
}
