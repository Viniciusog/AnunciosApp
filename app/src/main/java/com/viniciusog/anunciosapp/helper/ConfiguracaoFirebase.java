package com.viniciusog.anunciosapp.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.Reference;

public class ConfiguracaoFirebase {

    private static DatabaseReference referenciaFirebase;
    private static StorageReference referenciaStorage;
    private static FirebaseAuth referenciaAutenticacao;

    public static String getIdUsuario(){
        FirebaseAuth autenticacao = getFirebaseAuth();
        return autenticacao.getCurrentUser().getUid();
    }

    public static DatabaseReference getFirebaseDatabase() {
        if ( referenciaFirebase == null) {
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    public static StorageReference getFirebaseStorage() {
        if ( referenciaStorage == null) {
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if ( referenciaAutenticacao == null) {
            referenciaAutenticacao = FirebaseAuth.getInstance();
        }
        return referenciaAutenticacao;
    }
}