package com.example.teste2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class DetalhesLivroActivity extends AppCompatActivity {

    private TextView txtTituloView, txtAutorView, txtEditoraView, txtAnoView;
    private Spinner spinnerSituacao;
    private RadioGroup radioGroupStatus;
    private EditText edtObservacao;

    private String tituloRaw, autoresRaw, editoraRaw, anoRaw;

    private FirebaseFirestore db;

    // 🔥 GPS
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 0.0;
    private double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_livro);

        db = FirebaseFirestore.getInstance();

        txtTituloView = findViewById(R.id.txtTituloDetalhe);
        txtAutorView = findViewById(R.id.txtAutorDetalhe);
        txtEditoraView = findViewById(R.id.txtEditoraDetalhe);
        txtAnoView = findViewById(R.id.txtAnoDetalhe);
        spinnerSituacao = findViewById(R.id.spinnerSituacao);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        edtObservacao = findViewById(R.id.edtObservacao);

        // 🔥 Inicializa GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        verificarPermissao();

        // Dados da Intent
        if (getIntent() != null) {
            tituloRaw = getIntent().getStringExtra("titulo");
            autoresRaw = getIntent().getStringExtra("autor");
            editoraRaw = getIntent().getStringExtra("editora");

            int anoInt = getIntent().getIntExtra("ano", 0);
            anoRaw = anoInt != 0 ? String.valueOf(anoInt) : "Indisponível";

            txtTituloView.setText("Título: " + tituloRaw);
            txtAutorView.setText("Autor: " + autoresRaw);
            txtEditoraView.setText("Editora: " + editoraRaw);
            txtAnoView.setText("Ano de Publicação: " + anoRaw);
        }
    }

    // 🔥 PERMISSÃO
    private void verificarPermissao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);

        } else {
            pegarLocalizacao();
        }
    }

    // 🔥 PEGAR LOCALIZAÇÃO
    private void pegarLocalizacao() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        } else {
                            Toast.makeText(this, "Não foi possível obter localização", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // 🔥 RESPOSTA DA PERMISSÃO
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pegarLocalizacao();
            } else {
                Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 🔥 SALVAR
    public void salvarDadosLivro(View view) {

        // ⚠️ NÃO SALVA SEM LOCALIZAÇÃO
        if (latitude == 0.0 && longitude == 0.0) {
            Toast.makeText(this, "Aguardando localização... tente novamente", Toast.LENGTH_SHORT).show();
            return;
        }

        String situacao = spinnerSituacao.getSelectedItem().toString();
        int selectedId = radioGroupStatus.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Selecione um status", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rbStatus = findViewById(selectedId);
        String status = rbStatus.getText().toString();
        String observacao = edtObservacao.getText().toString();

        Map<String, Object> livro = new HashMap<>();
        livro.put("titulo", tituloRaw);
        livro.put("autores", autoresRaw);
        livro.put("editora", editoraRaw);
        livro.put("anoPublicacao", anoRaw);
        livro.put("observacao", observacao);
        livro.put("situacao", situacao);
        livro.put("statusLeitura", status);

        // 🔥 LOCALIZAÇÃO REAL
        livro.put("localizacao", new GeoPoint(latitude, longitude));

        db.collection("livros")
                .add(livro)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Livro salvo com localização!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}