package com.viniciusog.anunciosapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.santalu.maskedittext.MaskEditText;
import com.squareup.picasso.Picasso;
import com.viniciusog.anunciosapp.R;
import com.viniciusog.anunciosapp.model.Anuncio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class DetalhesMeuAnuncioActivity extends AppCompatActivity {

    private EditText editTitulo, editDescricao;
    private CurrencyEditText editValor;
    private MaskEditText editTelefone;
    private Spinner spinnerEstado, spinnerCategoria;

    private ImageView imagem1, imagem2, imagem3;
    private Button buttonSalvarMeuAnuncio;
    private android.app.AlertDialog dialog;

    private Anuncio meuAnuncioSelecionado;
    private List<String> listaFotosRecuperadas = new ArrayList<>(Arrays.asList("", "", ""));
    private List<String> listaFotosOkRecuperadas = new ArrayList<>();

    private boolean imagemFoiAlterada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_meu_anuncio);

        Log.i("aquidetalhes", "oncreate");

        //inicializar componentes
        inicializarComponentes();

        //Configurações iniciais
        getSupportActionBar().setTitle("Editar Anuncio");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carregarSpinner();

        meuAnuncioSelecionado = (Anuncio) getIntent().getExtras().getSerializable("meuAnuncioSelecionado");

        if (meuAnuncioSelecionado != null) {

            //Adiciona a lista de fotos do anuncio recuperado à nova lista de fotos recuperadas
            for (String urlImagem : meuAnuncioSelecionado.getFotos()) {
                listaFotosRecuperadas.set(meuAnuncioSelecionado.getFotos().indexOf(urlImagem), urlImagem);
            }
            //não usa mais -> listaFotosRecuperadas.addAll(meuAnuncioSelecionado.getFotos());

            editTelefone.setText(meuAnuncioSelecionado.getTelefone());
            editTitulo.setText(meuAnuncioSelecionado.getTitulo());
            editDescricao.setText(meuAnuncioSelecionado.getDescricao());
            editValor.setText(meuAnuncioSelecionado.getValor());

            Picasso.get().load(Uri.parse(meuAnuncioSelecionado.getFotos().get(0))).into(imagem1);
            Picasso.get().load(Uri.parse(meuAnuncioSelecionado.getFotos().get(1))).into(imagem2);
            Picasso.get().load(Uri.parse(meuAnuncioSelecionado.getFotos().get(2))).into(imagem3);

            List<String> estados = Arrays.asList(getResources().getStringArray(R.array.estados));
            List<String> categorias = Arrays.asList(getResources().getStringArray(R.array.categorias));

            spinnerEstado.setSelection(estados.indexOf(meuAnuncioSelecionado.getEstado()));
            spinnerCategoria.setSelection(categorias.indexOf(meuAnuncioSelecionado.getCategoria()));
        }

        imagem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escolherImagem(1);
            }
        });
        imagem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escolherImagem(2);
            }
        });
        imagem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escolherImagem(3);
            }
        });
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

    private void inicializarComponentes() {
        editTitulo = findViewById(R.id.editTituloMeuAnuncio);
        editTelefone = findViewById(R.id.editTelefoneMeuAnuncio);
        editDescricao = findViewById(R.id.editDescricaoMeuAnuncio);
        editValor = findViewById(R.id.editValorMeuAnuncio);
        spinnerEstado = findViewById(R.id.meuSpinnerEstado);
        spinnerCategoria = findViewById(R.id.meuSpinnerCategoria);
        buttonSalvarMeuAnuncio = findViewById(R.id.buttonSalvarMeuAnuncio);

        imagem1 = findViewById(R.id.meuAnuncioImage1);
        imagem2 = findViewById(R.id.meuAnuncioImage2);
        imagem3 = findViewById(R.id.meuAnuncioImage3);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void atualizarAnuncio(View view) {

        //Configura a dialog de carregamento
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Atualizando anúncio")
                .setCancelable(false)
                .build();
        dialog.show();

        if (dadosSaoValidos()) {
            Anuncio anuncioASerSalvo = new Anuncio();
            anuncioASerSalvo.setIdAnuncio(meuAnuncioSelecionado.getIdAnuncio());
            anuncioASerSalvo.setFotos(listaFotosOkRecuperadas);
            anuncioASerSalvo.setValor(editValor.getText().toString());
            anuncioASerSalvo.setTitulo(editTitulo.getText().toString());
            anuncioASerSalvo.setDescricao(editDescricao.getText().toString());
            anuncioASerSalvo.setTelefone(editTelefone.getRawText());
            anuncioASerSalvo.setEstado(spinnerEstado.getSelectedItem().toString());
            anuncioASerSalvo.setCategoria(spinnerCategoria.getSelectedItem().toString());

            try {
                //Remove o anúncio do nó depois de ser atualizado
                anuncioASerSalvo.atualizar();
                //meuAnuncioSelecionado.removerAnuncioPublico();
                anuncioASerSalvo.salvarAnuncioPublico();

                //Notificar MeusAnunciosActivity que os dados mudaram
                MeusAnunciosActivity.podeNotificarAdapter = true;
                //Notificar AnunciosActivity que os dados mudaram
                AnunciosActivity.podeNotificarAdapter = true;

                dialog.dismiss();
                Toast.makeText(this, "Sucesso ao atualizar anúncio.", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                dialog.dismiss();
                Toast.makeText(this, "Erro ao atualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            dialog.dismiss();
            Toast.makeText(this, "Insira algo novo em seu anúncio para atualizar.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean dadosSaoValidos() {
        String titulo, descricao, valor, telefone, estado, categoria;

        titulo = meuAnuncioSelecionado.getTitulo();
        telefone = meuAnuncioSelecionado.getTelefone();
        descricao = meuAnuncioSelecionado.getDescricao();
        valor = meuAnuncioSelecionado.getValor();
        estado = meuAnuncioSelecionado.getEstado();
        categoria = meuAnuncioSelecionado.getCategoria();

        boolean valido = false;
        if (imagemFoiAlterada
                || !titulo.equals(editTitulo.getText())
                || !telefone.equals(editTelefone.getText())
                || !descricao.equals(editDescricao.getText())
                || !valor.equals(editValor.getText())
                || !estado.equals(spinnerEstado.getSelectedItem().toString())
                || !categoria.equals(spinnerCategoria.getSelectedItem().toString())) {

            valido = true;

        }
        return valido;
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

            //A cada imagem selecionada e configurada, será setada esta nova foto na mesma posição da antiga foto
            // na lista de fotos recuperadas
            if (requestCode == 1) {
                imagem1.setImageURI(imagemSelecionada);
                listaFotosRecuperadas.set(0, caminhoFoto);
                imagemFoiAlterada = true;
            } else if (requestCode == 2) {
                imagem2.setImageURI(imagemSelecionada);
                listaFotosRecuperadas.set(1, caminhoFoto);
                imagemFoiAlterada = true;
            } else if (requestCode == 3) {
                imagem3.setImageURI(imagemSelecionada);
                listaFotosRecuperadas.set(2, caminhoFoto);
                imagemFoiAlterada = true;
            }
            listaFotosOkRecuperadas.add(caminhoFoto);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MeusAnunciosActivity.podeNotificarAdapter = false;
        AnunciosActivity.podeNotificarAdapter = false;
    }
}