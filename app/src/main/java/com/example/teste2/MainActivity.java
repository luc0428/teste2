package com.example.teste2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText edtPesquisa;
    private LinearLayout containerResultados;
    private TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtPesquisa = findViewById(R.id.edtPesquisa);
        containerResultados = findViewById(R.id.containerResultados);
        txtStatus = findViewById(R.id.txtStatus);
    }

    // metodo buscar livro
    public void buscarLivro(View view) {
        String termoBusca = edtPesquisa.getText().toString().trim();

        if (termoBusca.isEmpty()) {
            Toast.makeText(this, "Por favor, insira um termo para buscar.", Toast.LENGTH_SHORT).show();
            return;
        }

        txtStatus.setVisibility(View.VISIBLE);
        txtStatus.setText("Buscando livros...");
        containerResultados.removeAllViews();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String termoFormatado = URLEncoder.encode(termoBusca, "UTF-8");
                    String urlString = "https://openlibrary.org/search.json?q=" + termoFormatado + "&limit=10";

                    URL url = new URL(urlString);
                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                    conexao.setRequestMethod("GET");
                    conexao.setConnectTimeout(10000);
                    conexao.setReadTimeout(10000);

                    int responseCode = conexao.getResponseCode();

                    if (responseCode == 200) {
                        BufferedReader resposta = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                        String aux;
                        StringBuilder jsonEmString = new StringBuilder();

                        while ((aux = resposta.readLine()) != null) {
                            jsonEmString.append(aux);
                        }
                        resposta.close();

                        Gson gson = new Gson();
                        ResultadoLivro resultadoApi = gson.fromJson(jsonEmString.toString(), ResultadoLivro.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                exibirResultados(resultadoApi);
                            }
                        });

                    } else {
                        runOnUiThread(() -> txtStatus.setText("Erro no servidor: " + responseCode));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> txtStatus.setText("Erro de conexão: " + e.getMessage()));
                }
            }
        }).start();
    }

    private void exibirResultados(ResultadoLivro resultadoApi) {
        if (resultadoApi == null || resultadoApi.getDocs() == null || resultadoApi.getDocs().isEmpty()) {
            txtStatus.setText("Nenhum livro encontrado.");
            return;
        }

        txtStatus.setText("Resultados encontrados:");
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ResultadoLivro.BookDoc doc : resultadoApi.getDocs()) {
            View itemView = inflater.inflate(R.layout.item_livro, containerResultados, false);
            
            TextView txtTitulo = itemView.findViewById(R.id.txtTituloItem);
            TextView txtAutor = itemView.findViewById(R.id.txtAutorItem);

            String titulo = doc.getTitle() != null ? doc.getTitle() : "Sem título";
            String autor = (doc.getAuthorName() != null && !doc.getAuthorName().isEmpty()) 
                            ? doc.getAuthorName().get(0) : "Autor Desconhecido";
            
            txtTitulo.setText(titulo);
            txtAutor.setText(autor);

            // Clique para abrir detalhes
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DetalhesLivroActivity.class);
                intent.putExtra("titulo", titulo);
                intent.putExtra("autor", autor);
                
                String editora = (doc.getPublisher() != null && !doc.getPublisher().isEmpty()) 
                                ? doc.getPublisher().get(0) : "Indisponível";
                intent.putExtra("editora", editora);
                intent.putExtra("ano", doc.getFirstPublishYear());
                
                startActivity(intent);
            });

            containerResultados.addView(itemView);
        }
    }
}