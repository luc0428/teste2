package com.example.teste2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// Classes do Google Play Services importadas conforme o Slide 6 da Aula 09
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class CadastroActivity extends AppCompatActivity {

    private TextView txtLivro, txtAutor;
    private TextView latitudeTextView, longitudeTextView;

    // Variáveis globais para armazenar os valores finais do GPS (Desafio 2)
    private double latitudeAtual = 0.0;
    private double longitudeAtual = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // 1. Vinculando os componentes da tela (Slide 5)
        txtLivro = findViewById(R.id.txtLivroSelecionado);
        txtAutor = findViewById(R.id.txtAutorSelecionado);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);

        // Recupera o livro enviado pela MainActivity via Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            txtLivro.setText("Livro: " + extras.getString("CHAVE_TITULO"));
            txtAutor.setText("Autor: " + extras.getString("CHAVE_AUTOR"));
        }

        // DISPARO DA LOGICA: Assim que a tela abre, ela tenta ler o GPS
        obterLocalizacaoDoUsuario();
    }

    // PARTE 1: Solicitar permissão em tempo de execução (Slide 5)
    private void obterLocalizacaoDoUsuario() {
        try {
            // Verifica se a permissão de localização já foi concedida pelo usuário
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Se não foi concedida, abre a caixinha de diálogo do Android perguntando (Código identificador: 200)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            } else {
                // Se o usuário já tinha permitido antes, captura o GPS direto
                capturarCoordenadasGps();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro de permissão: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // PARTE 2: Capturar a latitude e longitude atuais (Slide 6 e 7)
    private void capturarCoordenadasGps() {
        // Checagem de segurança exigida pelo Android Studio para rodar o cliente de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Instancia o cliente de localização nativo do Google
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            // Solicita a última localização conhecida do hardware (Slide 6)
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Se o sensor do GPS responder com sucesso e não estiver nulo
                    if (location != null) {
                        // Salva as coordenadas do satélite nas nossas variáveis
                        latitudeAtual = location.getLatitude();
                        longitudeAtual = location.getLongitude();

                        // Atualiza os componentes visuais para o usuário ver na tela (Slide 7)
                        latitudeTextView.setText("Latitude: " + latitudeAtual);
                        longitudeTextView.setText("Longitude: " + longitudeAtual);
                    } else {
                        Toast.makeText(CadastroActivity.this, "GPS desligado ou indisponível no emulador!", Toast.LENGTH_LONG).show();
                        latitudeTextView.setText("Latitude: Não disponível");
                        longitudeTextView.setText("Longitude: Não disponível");
                    }
                }
            });
        }
    }

    // PARTE 3: Ouvir a resposta da caixinha de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Verifica se a resposta veio da nossa requisição de código 200
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Se o usuário clicou em "Permitir", aciona o GPS imediatamente
                capturarCoordenadasGps();
            } else {
                // Se ele recusou, avisa que o recurso foi bloqueado
                Toast.makeText(this, "Permissão de GPS recusada! Não mapeamos o livro.", Toast.LENGTH_LONG).show();
                latitudeTextView.setText("Latitude: Permissão negada");
                longitudeTextView.setText("Longitude: Permissão negada");
            }
        }
    }

    // PARTE 4: Associar as coordenadas capturadas ao Livro ao salvar
    public void salvarDadosLivro(View view) {
        String tituloFinal = txtLivro.getText().toString().replace("Livro: ", "");
        String autorFinal = txtAutor.getText().toString().replace("Autor: ", "");

        // Concatena tudo provando que as coordenadas foram vinculadas com sucesso ao objeto do livro
        StringBuilder logDados = new StringBuilder();
        logDados.append("=== LIVRO SALVO COM GEOLOCALIZAÇÃO ===\n")
                .append("Título: ").append(tituloFinal).append("\n")
                .append("Autor: ").append(autorFinal).append("\n")
                .append("Latitude Vinculada: ").append(latitudeAtual).append("\n")
                .append("Longitude Vinculada: ").append(longitudeAtual);

        Toast.makeText(this, "Livro cadastrado com localização!", Toast.LENGTH_LONG).show();

        // Imprime o resultado final combinado no seu Logcat
        Log.d("DESAFIO_2_SUCESSO", logDados.toString());

        // Fecha a tela de cadastro e retorna automaticamente para a MainActivity
        finish();
    }
}