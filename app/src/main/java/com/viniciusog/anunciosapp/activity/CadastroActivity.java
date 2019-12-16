package com.viniciusog.anunciosapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.viniciusog.anunciosapp.R;
import com.viniciusog.anunciosapp.helper.ConfiguracaoFirebase;

public class CadastroActivity extends AppCompatActivity {

    private EditText editCadastroEmail, editCadastroSenha;
    private Button buttonCadastrar;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        //Inicializar componentes
        inicializarComponentes();

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editCadastroEmail.getText().toString();
                String senha = editCadastroSenha.getText().toString();

                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {
                        autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CadastroActivity.this,
                                            "Sucesso ao realizar cadastro!",
                                            Toast.LENGTH_SHORT).show();

                                    //Direcionar para a tela principal do app
                                    Intent i = new Intent(CadastroActivity.this, AnunciosActivity.class);
                                    startActivity(i);
                                } else {
                                    String excecao = "";
                                    try {
                                        throw task.getException();

                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        excecao = "Digite uma senha mais forte!";
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        excecao = "Insira um e-mail válido!";
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        excecao = "Esta conta já foi cadastrada!";
                                    } catch (Exception e) {
                                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(CadastroActivity.this, "Insira sua senha!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this, "Insira seu email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void inicializarComponentes() {
        editCadastroEmail = findViewById(R.id.editCadastroEmail);
        editCadastroSenha = findViewById(R.id.editCadastroSenha);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
    }
}
