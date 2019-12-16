package com.viniciusog.anunciosapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.santalu.maskedittext.MaskEditText;
import com.viniciusog.anunciosapp.R;
import com.viniciusog.anunciosapp.helper.Permissoes;

import java.util.Locale;

public class CadastrarAnuncioActivity extends AppCompatActivity {

    private EditText editTitulo, editDescricao;
    private CurrencyEditText editValor;
    private MaskEditText editTelefone;

    private String[] permissoes= new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        //Inicializar componentes
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        editValor = findViewById(R.id.editValor);
        editTelefone = findViewById(R.id.editTelefone);

        //Condigura localidade para pt BR
        editValor.setLocale(new Locale("pt", "BR"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for ( int permissaoResultado : grantResults) {
            if ( permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utlizar o app é necessário aceitar as permissões.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
