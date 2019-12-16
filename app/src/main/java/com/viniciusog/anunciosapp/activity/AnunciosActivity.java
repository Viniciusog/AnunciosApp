package com.viniciusog.anunciosapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.viniciusog.anunciosapp.R;
import com.viniciusog.anunciosapp.helper.ConfiguracaoFirebase;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        //autenticacao.signOut();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //É chamado antes dos items serem carregados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Usuario deslogado
        if (autenticacao.getCurrentUser() == null) {
            menu.setGroupVisible(R.id.group_deslogado, true);
        } else { //Usuario logado
            menu.setGroupVisible(R.id.group_logado, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cadastrar: {
                startActivity(new Intent(AnunciosActivity.this, CadastroActivity.class));
                break;
            }
            case R.id.menu_logar: {
                startActivity(new Intent(AnunciosActivity.this, LoginActivity.class));
                break;
            }
            case R.id.menu_sair: {
                autenticacao.signOut();
                invalidateOptionsMenu();  //Irá invaidar o menu e chamar o método onPrepareOptionsMenu novamente (Regarrega
                //os menus novamente
                break;
            }
            case R.id.menu_anuncios: {
                startActivity(new Intent(AnunciosActivity.this, MeusAnunciosActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
