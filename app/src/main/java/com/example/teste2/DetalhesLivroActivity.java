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

public class DetalhesLivroActivity extends AppCompatActivity {

    private TextView txtTitulo, txtAutor, txtEditora, txtAno;
    private Spinner spinnerSituacao;
    private RadioGroup radioGroupStatus;
    private EditText edtObservacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_livro);

        txtTitulo = findViewById(R.id.txtTituloDetalhe);
        txtAutor = findViewById(R.id.txtAutorDetalhe);
        txtEditora = findViewById(R.id.txtEditoraDetalhe);
        txtAno = findViewById(R.id.txtAnoDetalhe);
        spinnerSituacao = findViewById(R.id.spinnerSituacao);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        edtObservacao = findViewById(R.id.edtObservacao);

        // Receber dados da Intent
        if (getIntent() != null) {
            String titulo = getIntent().getStringExtra("titulo");
            String autor = getIntent().getStringExtra("autor");
            String editora = getIntent().getStringExtra("editora");
            int ano = getIntent().getIntExtra("ano", 0);

            txtTitulo.setText("Título: " + (titulo != null ? titulo : "Indisponível"));
            txtAutor.setText("Autor: " + (autor != null ? autor : "Desconhecido"));
            txtEditora.setText("Editora: " + (editora != null ? editora : "Indisponível"));
            txtAno.setText("Ano de Publicação: " + (ano != 0 ? String.valueOf(ano) : "Indisponível"));
        }
    }

    public void salvarDadosLivro(View view) {
        String situacao = spinnerSituacao.getSelectedItem().toString();
        int selectedId = radioGroupStatus.getCheckedRadioButtonId();
        RadioButton rbStatus = findViewById(selectedId);
        String status = rbStatus.getText().toString();
        String observacao = edtObservacao.getText().toString();

        // Aqui você pode salvar no Firebase, Banco de Dados local, etc.
        // Por enquanto, apenas exibimos um Toast com as informações
        String resumo = "Salvo!\nSituação: " + situacao + "\nStatus: " + status;
        Toast.makeText(this, resumo, Toast.LENGTH_LONG).show();
        
        // Finaliza a activity após salvar
        // finish(); 
    }
}