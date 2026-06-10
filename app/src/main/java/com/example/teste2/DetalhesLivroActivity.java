package com.example.teste2;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class DetalhesLivroActivity extends AppCompatActivity {

    private TextView txtTituloView, txtAutorView, txtEditoraView, txtAnoView;
    private Spinner spinnerSituacao;
    private RadioGroup radioGroupStatus;
    private EditText edtObservacao;

    // Variáveis para guardar os dados brutos recebidos da Intent
    private String tituloRaw, autoresRaw, editoraRaw, anoRaw;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_livro);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        txtTituloView = findViewById(R.id.txtTituloDetalhe);
        txtAutorView = findViewById(R.id.txtAutorDetalhe);
        txtEditoraView = findViewById(R.id.txtEditoraDetalhe);
        txtAnoView = findViewById(R.id.txtAnoDetalhe);
        spinnerSituacao = findViewById(R.id.spinnerSituacao);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        edtObservacao = findViewById(R.id.edtObservacao);

        // Receber dados da Intent
        if (getIntent() != null) {
            tituloRaw = getIntent().getStringExtra("titulo");
            autoresRaw = getIntent().getStringExtra("autor");
            editoraRaw = getIntent().getStringExtra("editora");
            int anoInt = getIntent().getIntExtra("ano", 0);
            anoRaw = anoInt != 0 ? String.valueOf(anoInt) : "Indisponível";

            txtTituloView.setText("Título: " + (tituloRaw != null ? tituloRaw : "Indisponível"));
            txtAutorView.setText("Autor: " + (autoresRaw != null ? autoresRaw : "Desconhecido"));
            txtEditoraView.setText("Editora: " + (editoraRaw != null ? editoraRaw : "Indisponível"));
            txtAnoView.setText("Ano de Publicação: " + anoRaw);
        }
    }

    public void salvarDadosLivro(View view) {
        String situacao = spinnerSituacao.getSelectedItem().toString();
        int selectedId = radioGroupStatus.getCheckedRadioButtonId();
        
        if (selectedId == -1) {
            Toast.makeText(this, "Por favor, selecione um status de leitura.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        RadioButton rbStatus = findViewById(selectedId);
        String status = rbStatus.getText().toString();
        String observacao = edtObservacao.getText().toString();

        // Criar o objeto para salvar no Firestore conforme a estrutura solicitada
        Map<String, Object> livro = new HashMap<>();
        livro.put("titulo", tituloRaw != null ? tituloRaw : "");
        livro.put("autores", autoresRaw != null ? autoresRaw : "");
        livro.put("editora", editoraRaw != null ? editoraRaw : "");
        livro.put("anoPublicacao", anoRaw);
        livro.put("observacao", observacao);
        livro.put("situacao", situacao);
        livro.put("statusLeitura", status);
        
        // Localização padrão 0,0 como solicitado
        livro.put("localizacao", new GeoPoint(0.0, 0.0));

        // Salvar na coleção "livros"
        db.collection("livros")
                .add(livro)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Livro salvo com sucesso no Firebase!", Toast.LENGTH_LONG).show();
                    finish(); // Fecha a tela após salvar
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao salvar livro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}