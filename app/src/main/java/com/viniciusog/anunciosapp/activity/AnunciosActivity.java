package com.viniciusog.anunciosapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.viniciusog.anunciosapp.R;
import com.viniciusog.anunciosapp.adapter.AdapterAnuncios;
import com.viniciusog.anunciosapp.helper.ConfiguracaoFirebase;
import com.viniciusog.anunciosapp.helper.RecyclerItemClickListener;
import com.viniciusog.anunciosapp.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    public static boolean podeNotificarAdapter;

    private Button buttonCategoria, buttonRegiao;
    private RecyclerView recyclerAnunciosPublicos;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AlertDialog dialog;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorEstado = false;


    private DatabaseReference anunciosPublicosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        //Inicializar componentes
        inicializarComponentes();

        //Configura RecyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncios, this);
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        //Recupera Anuncios Publicos
        recuperarAnunciosPublicos();
        anuncios.clear();

        //Adicionar evento de clique no recycler de anuncios
        recyclerAnunciosPublicos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerAnunciosPublicos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Anuncio anuncioSelecionado = anuncios.get(position);

                        //Cria uma intent e manda o anuncio selecionado para a activity DetalhesProdutosActivity
                        Intent i = new Intent(AnunciosActivity.this, DetalhesProdutosActivity.class);
                        i.putExtra("anuncioSelecionado", anuncioSelecionado);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }

    //Recupera anuncios do firebase
    private void recuperarAnunciosPublicos() {

        //Configura a dialog de carregamento
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios")
        ;

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                anuncios.clear();
                for (DataSnapshot estados : dataSnapshot.getChildren()) {
                    for (DataSnapshot categorias : estados.getChildren()) {
                        for (DataSnapshot dsAnuncios : categorias.getChildren()) {
                            anuncios.add(dsAnuncios.getValue(Anuncio.class));
                        }
                    }
                }

                if (anuncios.size() > 0) {
                    Collections.reverse(anuncios);
                    adapterAnuncios.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(AnunciosActivity.this, "Não existe anúncios a serem recuperados.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AnunciosActivity.this, "Erro ao carregar anúncios!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recuperarAnunciosPorEstados() {

        //Configura a dialog de carregamento
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Filtrando anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios")
                .child(filtroEstado);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                anuncios.clear();
                for (DataSnapshot categorias : dataSnapshot.getChildren()) {
                    for (DataSnapshot anuncio : categorias.getChildren()) {
                        anuncios.add(anuncio.getValue(Anuncio.class));
                    }
                }

                if (anuncios.size() > 0) {
                    Collections.reverse(anuncios);
                    adapterAnuncios.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    adapterAnuncios.notifyDataSetChanged();
                    Toast.makeText(AnunciosActivity.this, "Não existe anúncios para esta região.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(AnunciosActivity.this, "Erro ao recuperar anúncios por estado.", Toast.LENGTH_SHORT).show();
            }
        });
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

    public void filtrarPorEstado(View view) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Selecione o estado desejado");

        //***Configurar Spinner - inflar layout (Passar um layout para um objeto view)***\\
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltro);
        //Configura spinner estado
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);

        dialog.setView(viewSpinner);

        //Adiciona botões em alert dialog
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filtroEstado = spinnerEstado.getSelectedItem().toString();
                recuperarAnunciosPorEstados();
                filtrandoPorEstado = true;
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapterAnuncios.notifyDataSetChanged();
            }
        });

        AlertDialog alert = dialog.create();
        alert.show();
    }

    public void filtrarPorCategoria(View view) {

        if (filtrandoPorEstado == true) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Selecione a categoria desejada");

            //***Configurar Spinner - inflar layout (Passar um layout para um objeto view)***\\
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
            final Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);
            //Configura spinner de categorias
            String[] categorias = getResources().getStringArray(R.array.categorias);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    categorias
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategoria.setAdapter(adapter);

            dialog.setView(viewSpinner);

            //Adiciona botões em alert dialog
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarAnunciosPorCategoria();
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    adapterAnuncios.notifyDataSetChanged();
                }
            });

            AlertDialog alert = dialog.create();
            alert.show();
        } else {
            Toast.makeText(this, "Escolha primeiro uma região!", Toast.LENGTH_SHORT).show();
        }
    }

    private void recuperarAnunciosPorCategoria() {

        //Configura a dialog de carregamento
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Filtrando anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios")
                .child(filtroEstado)
                .child(filtroCategoria);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                anuncios.clear();
                Log.i("dados mudaram", String.valueOf(anuncios.size()));

                for (DataSnapshot anuncio : dataSnapshot.getChildren()) {
                    anuncios.add(anuncio.getValue(Anuncio.class));
                }

                if (anuncios.size() > 0) {
                    Log.i("tamanho anuncios: " ,String.valueOf(anuncios.size()) );
                    Collections.reverse(anuncios);
                    adapterAnuncios.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Log.i("tamanho anuncios: " ,String.valueOf(anuncios.size()) );
                    dialog.dismiss();
                    adapterAnuncios.notifyDataSetChanged();
                    Toast.makeText(AnunciosActivity.this, "Não existe anúncios para esta categoria.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(AnunciosActivity.this, "Erro ao recuperar anúncios por categoria.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inicializarComponentes() {
        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);
        buttonRegiao = findViewById(R.id.buttonRegiao);
        buttonCategoria = findViewById(R.id.buttonCategoria);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if ( podeNotificarAdapter ) {
            adapterAnuncios.notifyDataSetChanged();
            podeNotificarAdapter = false;
        }
    }
}