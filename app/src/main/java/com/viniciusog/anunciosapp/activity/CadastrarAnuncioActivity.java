package com.viniciusog.anunciosapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskedittext.MaskEditText;
import com.viniciusog.anunciosapp.R;
import com.viniciusog.anunciosapp.helper.ConfiguracaoFirebase;
import com.viniciusog.anunciosapp.helper.Permissoes;
import com.viniciusog.anunciosapp.model.Anuncio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTitulo, editDescricao;
    private CurrencyEditText editValor;
    private MaskEditText editTelefone;
    private Spinner spinnerEstado, spinnerCategoria;

    private ImageView imagem1, imagem2, imagem3;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>(Arrays.asList("", "", "")); //Caminho das fotos no dispositivo
    private List<String> listaFotosOkRecuperadas = new ArrayList<>(); //Caminho das fotos no dispositivo
    private List<String> listaUrlFotos = new ArrayList<>(); //Caminho das fotos no firebase
    private Anuncio anuncio;
    private StorageReference storage;
    private android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Inicializar componentes
        inicializarComponentes();
        carregarSpinner();
        storage = ConfiguracaoFirebase.getFirebaseStorage();
    }

    private void carregarSpinner() {
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);

        //Configura spinner de categoria
        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );

        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategorias);
    }

    public void salvarAnuncio() {
        //Salvar imagem no storage
        for (String urlImagem : listaFotosOkRecuperadas) {
            int tamanhoLista = listaFotosOkRecuperadas.size();
            //Primeira foto (index 0), segunda foto ( index1 ), terceira foto ( index2 )
            int indexFotoASerSalva = listaFotosOkRecuperadas.indexOf(urlImagem);
            salvarFotoStorage(urlImagem, tamanhoLista, indexFotoASerSalva);
        }
    }

    //ESTÁ COM UM ERRO QUE NÃO COMPROMETE O SISTEMA, POSIÇÃO DAS IMAGENS A SEREM EXIBIDAS
    private void salvarFotoStorage(String urlImagem, final int tamanhoLista, int indexFotoASerSalva) {

        //Criar nó no storage
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + indexFotoASerSalva);

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlImagem));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                String urlConvertida = firebaseUrl.toString();
                listaUrlFotos.add(urlConvertida);

                if (tamanhoLista == listaUrlFotos.size()) {
                    anuncio.setFotos(listaUrlFotos); //ERRO É AQUI
                    anuncio.salvar();

                    //Fecha a DIALOG pois o carregamento terminou
                    dialog.dismiss();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                exibirMensagemErro("Erro ao fazer upload de imagem.");
            }
        });
    }

    public void validarDadosAnuncio(View view) {
        //A dialog é fechada no método salvarFotoStorage
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anúncio")
                .setCancelable(false)
                .build();
        dialog.show();

        try {
            // Testar aqui tudo que pode dar errado!
            verificaDadosAnuncios();
            // Fazer aqui o que você realmente quer fazer!
            salvarAnuncio();

        } catch (Exception e) {
            exibirMensagemErro(e.getMessage());
        }
    }

    private void verificaDadosAnuncios() throws Exception {
        String fone = "";
        String valor = String.valueOf(editValor.getRawValue());
        anuncio = configurarAnuncio();
        if (editTelefone.getRawText() != null) {
            fone = editTelefone.getRawText().toString();
        }
        if (listaFotosOkRecuperadas.size() < 2)
            throw new Exception("Escolha no mínimo 2 fotos para seu anúncio.");
        if (anuncio.getEstado().isEmpty())
            throw new Exception("Selecione um estado.");
        if (anuncio.getCategoria().isEmpty())
            throw new Exception("Selecione uma categoria.");
        if (anuncio.getTitulo().isEmpty())
            throw new Exception("Informe o título.");
        if (valor.equals("0") || valor.isEmpty())
            throw new Exception("Preencha o campo valor.");
        if (anuncio.getTelefone().isEmpty() || fone.length() != 11)
            throw new Exception("Selecione o campo telefone. Digite 11 números.");
        if (anuncio.getDescricao().isEmpty())
            throw new Exception("Preencha o campo descrição.");
    }

    private Anuncio configurarAnuncio() {
        Anuncio anuncio = new Anuncio();

        String estado = spinnerEstado.getSelectedItem().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String titulo = editTitulo.getText().toString();
        String valor = editValor.getText().toString();
        String telefone = editTelefone.getRawText().toString();
        String descricao = editDescricao.getText().toString();

        anuncio.setCategoria(categoria);
        anuncio.setDescricao(descricao);
        anuncio.setEstado(estado);
        anuncio.setTelefone(telefone);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);

        return anuncio;
    }

    private void exibirMensagemErro(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.anuncioImage1: {
                escolherImagem(1);
                break;
            }
            case R.id.anuncioImage2: {
                escolherImagem(2);
                break;
            }
            case R.id.anuncioImage3: {
                escolherImagem(3);
                break;
            }
        }
    }

    private void escolherImagem(int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Recuperar imagem selecionada
            Uri imagemSelecionada = data.getData();
            String caminhoFoto = imagemSelecionada.toString();

            int posicao = 0;

            if (requestCode == 1) {
                posicao = 1;
                imagem1.setImageURI(imagemSelecionada);
            } else if (requestCode == 2) {
                posicao = 2;
                imagem2.setImageURI(imagemSelecionada);
            } else if (requestCode == 3) {
                posicao = 3;
                imagem3.setImageURI(imagemSelecionada);
            }
            listaFotosOkRecuperadas.add(caminhoFoto);
            listaFotosRecuperadas.set(posicao - 1, caminhoFoto);
            /*if (listaFotosRecuperadas.get(posicao - 1) != null && posicao != 0) {
                listaFotosRecuperadas.set(posicao - 1, caminhoFoto);
            } else {
                listaFotosRecuperadas.add(caminhoFoto);
            }*/
            //listaFotosRecuperadas.add(caminhoFoto);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("tamanho lista", String.valueOf(listaFotosRecuperadas.size()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
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

    private void inicializarComponentes() {
        editTitulo = findViewById(R.id.editTituloMeuAnuncio);
        editDescricao = findViewById(R.id.editDescricaoMeuAnuncio);
        editValor = findViewById(R.id.editValorMeuAnuncio);
        editTelefone = findViewById(R.id.editTelefoneMeuAnuncio);
        spinnerCategoria = findViewById(R.id.meuSpinnerCategoria);
        spinnerEstado = findViewById(R.id.meuSpinnerEstado);
        imagem1 = findViewById(R.id.anuncioImage1);
        imagem2 = findViewById(R.id.anuncioImage2);
        imagem3 = findViewById(R.id.anuncioImage3);
        imagem1.setOnClickListener(this); //A própria classe cuida de ouvir qual imagem foi clicada, no método onClick
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);

        //Condigura localidade para pt BR
        editValor.setLocale(new Locale("pt", "BR"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
