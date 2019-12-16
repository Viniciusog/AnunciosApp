package com.viniciusog.anunciosapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.viniciusog.anunciosapp.R;

import java.util.Locale;

public class CadastrarAnuncioActivity extends AppCompatActivity {

    private EditText editTitulo, editDescricao;
    private CurrencyEditText editValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Inicializar componentes
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        editValor = findViewById(R.id.editValor);

        //Condigura localidade para pt BR
        editValor.setLocale(new Locale("pt", "BR"));
    }
}
