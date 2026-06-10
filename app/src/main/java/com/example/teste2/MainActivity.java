package com.example.teste2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText edtPesquisa;
    private TextView txtResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtPesquisa = findViewById(R.id.edtPesquisa);
        txtResultado = findViewById(R.id.txtResultado);
    }

    // Método disparado pelo clique do Botão (Slide 15/16)
    public void buscarLivro(View view) {
        String termoBusca = edtPesquisa.getText().toString().trim();

        if (termoBusca.isEmpty()) {
            txtResultado.setText("Por favor, insira um termo para buscar.");
            return;
        }

        txtResultado.setText("Buscando livros...");

        // Nova thread paralela para requisição de rede (Slide 8)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Formata o termo digitado para evitar quebras na URL
                    String termoFormatado = URLEncoder.encode(termoBusca, "UTF-8");

                    // Usando o endpoint estável da Open Library limitado a 3 resultados (Livre de Erro 429)
                    String urlString = "https://openlibrary.org/search.json?q=" + termoFormatado + "&limit=3";

                    URL url = new URL(urlString);
                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                    conexao.setRequestMethod("GET"); // Método GET (Slide 16)
                    conexao.setConnectTimeout(10000);
                    conexao.setReadTimeout(10000);

                    int responseCode = conexao.getResponseCode(); // Status do servidor (Slide 17)

                    if (responseCode == 200) { // HTTP 200 OK (Slide 17)
                        BufferedReader resposta = new BufferedReader(new InputStreamReader(conexao.getInputStream())); // (Slide 9)
                        String aux;
                        StringBuilder jsonEmString = new StringBuilder();

                        while ((aux = resposta.readLine()) != null) { // (Slide 9)
                            jsonEmString.append(aux);
                        }
                        resposta.close();

                        // Convertendo o JSON usando GSON (Slide 10)
                        Gson gson = new Gson();
                        ResultadoLivro resultadoApi = gson.fromJson(jsonEmString.toString(), ResultadoLivro.class);

                        StringBuilder textoFormatado = new StringBuilder();

                        if (resultadoApi != null && resultadoApi.getDocs() != null && !resultadoApi.getDocs().isEmpty()) {

                            // Varre a lista de documentos retornados
                            for (ResultadoLivro.BookDoc doc : resultadoApi.getDocs()) {

                                textoFormatado.append("📖 Título: ").append(doc.getTitle() != null ? doc.getTitle() : "Sem título").append("\n");

                                if (doc.getAuthorName() != null && !doc.getAuthorName().isEmpty()) {
                                    textoFormatado.append("✍️ Autor: ").append(doc.getAuthorName().get(0)).append("\n");
                                } else {
                                    textoFormatado.append("✍️ Autor: Desconhecido\n");
                                }

                                if (doc.getFirstSentence() != null && !doc.getFirstSentence().isEmpty()) {
                                    String resumo = doc.getFirstSentence().get(0);
                                    int limite = Math.min(resumo.length(), 130);
                                    textoFormatado.append("📝 Fragmento: ").append(resumo.substring(0, limite)).append("...\n");
                                } else {
                                    textoFormatado.append("📝 Fragmento: Introdução indisponível para este exemplar.\n");
                                }

                                textoFormatado.append("\n---------------------\n\n");
                            }
                        } else {
                            textoFormatado.append("Nenhum livro encontrado para este termo.");
                        }

                        // Modifica os elementos visuais na UI Thread (Slide 10)
                        String finalTexto = textoFormatado.toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtResultado.setText(finalTexto); // (Slide 10)
                            }
                        });

                    } else {
                        runOnUiThread(() -> txtResultado.setText("Erro no servidor de livros. Código HTTP: " + responseCode));
                    }

                } catch (Exception e) { // Tratamento contra falhas de conexão (Slide 11)
                    e.printStackTrace();
                    runOnUiThread(() -> txtResultado.setText("Erro de conexão: " + e.getMessage()));
                }
            }
        }).start(); // Inicializa a Thread (Slide 11)
    }
}