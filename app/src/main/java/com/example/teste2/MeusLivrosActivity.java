package com.example.teste2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Locale;

public class MeusLivrosActivity extends AppCompatActivity {

    private LinearLayout containerMeusLivros;
    private TextView txtStatus;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_livros);

        containerMeusLivros = findViewById(R.id.containerMeusLivros);
        txtStatus = findViewById(R.id.txtStatusMeusLivros);
        db = FirebaseFirestore.getInstance();

        carregarLivros();
    }

    private void carregarLivros() {
        db.collection("livros")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    containerMeusLivros.removeAllViews();
                    
                    if (queryDocumentSnapshots.isEmpty()) {
                        txtStatus.setText("Você ainda não salvou nenhum livro.");
                        return;
                    }

                    txtStatus.setText("Total de livros: " + queryDocumentSnapshots.size());
                    LayoutInflater inflater = LayoutInflater.from(this);

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        View itemView = inflater.inflate(R.layout.item_livro, containerMeusLivros, false);

                        TextView txtTitulo = itemView.findViewById(R.id.txtTituloItem);
                        TextView txtAutor = itemView.findViewById(R.id.txtAutorItem);
                        TextView txtLoc = itemView.findViewById(R.id.txtLocalizacaoItem);

                        String titulo = document.getString("titulo");
                        String autores = document.getString("autores");
                        String status = document.getString("statusLeitura");
                        GeoPoint loc = document.getGeoPoint("localizacao");

                        txtTitulo.setText(titulo != null ? titulo : "Sem título");
                        txtAutor.setText((autores != null ? autores : "Autor desconhecido") + " - [" + status + "]");
                        
                        if (loc != null) {
                            txtLoc.setText(String.format(Locale.getDefault(), "📍 Localização: Lat %.2f, Lon %.2f", 
                                    loc.getLatitude(), loc.getLongitude()));
                            txtLoc.setVisibility(View.VISIBLE);
                        } else {
                            txtLoc.setVisibility(View.GONE);
                        }

                        itemView.setOnClickListener(v -> {
                            String detalhe = "Editora: " + document.getString("editora") + 
                                           "\nSituação: " + document.getString("situacao") +
                                           "\nObs: " + document.getString("observacao");
                            Toast.makeText(MeusLivrosActivity.this, detalhe, Toast.LENGTH_LONG).show();
                        });

                        containerMeusLivros.addView(itemView);
                    }
                })
                .addOnFailureListener(e -> {
                    txtStatus.setText("Erro ao carregar livros: " + e.getMessage());
                });
    }

    public void voltarInicio(View view) {
        finish();
    }
}