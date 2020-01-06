package com.viniciusog.anunciosapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.viniciusog.anunciosapp.helper.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Anuncio implements Serializable {

    private String idAnuncio;
    private String estado;
    private String categoria;
    private String titulo;
    private String valor;
    private String telefone;
    private String descricao;
    private List<String> fotos;

    //Já configura o id do anuncio assim que o objeto anuncio é instanciado
    public Anuncio() {
        DatabaseReference anunciosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("meus_anuncios");
        setIdAnuncio(anunciosRef.push().getKey());
    }

    public void salvar() {
        //Salva nó meus_anuncios
        DatabaseReference anunciosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("meus_anuncios");

        anunciosRef.child(ConfiguracaoFirebase.getIdUsuario())
                .child(getIdAnuncio())
                .setValue(this);

        //Salva anuncios publicos
        salvarAnuncioPublico();
    }

    public void salvarAnuncioPublico() {
        DatabaseReference anunciosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios")
                .child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio());

        anunciosRef.setValue(this);
    }

    public void atualizar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference anuncioRef = firebaseRef.child("meus_anuncios").child(ConfiguracaoFirebase.getIdUsuario())
                .child(getIdAnuncio());

        //Apesar de no Firebase conter o nó idAnuncio dentro do anúncio, não é necessário,
        // pois o mesmo já está dentro do nó que contém o id DoAnuncio
        HashMap<String, Object> objeto = new HashMap();
        objeto.put("categoria", getCategoria());
        objeto.put("descricao", getDescricao());
        objeto.put("estado", getEstado());
        objeto.put("fotos", getFotos());
        objeto.put("telefone", getTelefone());
        objeto.put("titulo", getTitulo());
        objeto.put("valor", getValor());

        //atualizarAnuncioPublico();
        atualizarImagensStorage();

        //firebaseRef.updateChildren(objeto);
        anuncioRef.updateChildren(objeto);
    }

    private void atualizarAnuncioPublico() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference anuncioPublicoRef = firebaseRef.child("anuncios")
                .child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio());

        HashMap<String, Object> objeto = new HashMap();
        objeto.put("categoria", getCategoria());
        objeto.put("descricao", getDescricao());
        objeto.put("estado", getEstado());
        objeto.put("fotos", getFotos());
        objeto.put("telefone", getTelefone());
        objeto.put("titulo", getTitulo());
        objeto.put("valor", getValor());
        objeto.put("idAnuncio", getIdAnuncio());

        anuncioPublicoRef.updateChildren(objeto);
    }

    private void atualizarImagensStorage() {

        StorageReference imagemsAnuncio = ConfiguracaoFirebase.getFirebaseStorage().child("imagens")
                .child("anuncios")
                .child(getIdAnuncio());

        for ( String urlImagem : getFotos()) {
            int posicao = getFotos().indexOf(urlImagem);
            StorageReference imagem = imagemsAnuncio.child("imagem" + posicao);
            imagem.putFile(Uri.parse(urlImagem));
        }
    }

    public void remover() {
        DatabaseReference anunciosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("meus_anuncios")
                .child(ConfiguracaoFirebase.getIdUsuario())
                .child(getIdAnuncio());

        anunciosRef.removeValue();
        removerAnuncioPublico();
    }

    public void removerAnuncioPublico() {
        DatabaseReference anunciosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios")
                .child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio());

        anunciosRef.removeValue();
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
