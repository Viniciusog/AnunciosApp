package com.viniciusog.anunciosapp.model;

import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseReference;
import com.viniciusog.anunciosapp.helper.ConfiguracaoFirebase;

import java.util.List;

public class Anuncio {

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
                .child(ConfiguracaoFirebase.getIdUsuario())
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
