package com.viniciusog.anunciosapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.viniciusog.anunciosapp.R;
import com.viniciusog.anunciosapp.helper.ConfiguracaoFirebase;

public class LoginActivity extends AppCompatActivity {

    private EditText editLoginEmail, editLoginSenha;
    private TextView textCadastrar;
    private Button buttonLogar;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        //Inicializar componentes
        inicializarComponentes();

        buttonLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editLoginEmail.getText().toString();
                String senha = editLoginSenha.getText().toString();

                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {
                        autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,
                                            "Sucesso ao realizar login!",
                                            Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(LoginActivity.this, AnunciosActivity.class));
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Erro ao realizar login: " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Insira sua senha!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Insira seu email!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(i);
            }
        });
    }


    private void inicializarComponentes() {
        editLoginEmail = findViewById(R.id.editLoginEmail);
        editLoginSenha = findViewById(R.id.editLoginSenha);
        textCadastrar = findViewById(R.id.textCadastrar);
        buttonLogar = findViewById(R.id.buttonLogar);
    }
}