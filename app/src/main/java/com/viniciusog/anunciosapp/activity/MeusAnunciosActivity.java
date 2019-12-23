package com.viniciusog.anunciosapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.viniciusog.anunciosapp.R;
import com.viniciusog.anunciosapp.adapter.AdapterAnuncios;
import com.viniciusog.anunciosapp.helper.ConfiguracaoFirebase;
import com.viniciusog.anunciosapp.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anunciosUsuarioRef;
    private android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Configurações iniciais
        anunciosUsuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("meus_anuncios")
                .child(ConfiguracaoFirebase.getIdUsuario());

        //Iniciar componentes
        inicializarComponentes();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeusAnunciosActivity.this, CadastrarAnuncioActivity.class));
            }
        });

        //Configurar recyclerView
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter(adapterAnuncios);

        recuperarAnuncios();

        swipe();
    }

    private void recuperarAnuncios() {
        //Abre a dialog de carregamento
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Limpa a lista de anúncios
                anuncios.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Adiciona os anuncios existentes na lista de anuncios
                    anuncios.add(ds.getValue(Anuncio.class));
                }

                if ( anuncios.size() > 0) {
                    //Inverte a lista para mostrar os anúncios mais novos primeiro
                    Collections.reverse(anuncios);
                    adapterAnuncios.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(MeusAnunciosActivity.this, "Não existe anúncios a serem recuperados.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(MeusAnunciosActivity.this, "Erro ao recuperar anúncios!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inicializarComponentes() {
        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);
    }

    private void swipe() {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE; //Movimentação invativa
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirAnuncio(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerAnuncios);
    }

    private void excluirAnuncio(final RecyclerView.ViewHolder viewHolder) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Excluir anúncio da conta");
        alert.setMessage("Você deseja realmente excluir este anúncio?");
        alert.setCancelable(false);

        //Configura a dialog de carregamento
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anúncios")
                .setCancelable(false)
                .build();

        alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.show();
                //Pega a posição do item que foi clicado
                int posicao = viewHolder.getAdapterPosition();
                Anuncio anuncioSelecionado = anuncios.get( posicao );
                anuncioSelecionado.remover();
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.show();
                //Recarregar anúncios
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }
}