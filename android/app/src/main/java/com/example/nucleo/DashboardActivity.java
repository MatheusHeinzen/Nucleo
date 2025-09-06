package com.example.nucleo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        TextView tvBoasVindas = findViewById(R.id.tvBoasVindas);
        Button btnSair = findViewById(R.id.btnSair);

        // Recebe o nome do usuário da tela de login
        String nomeUsuario = getIntent().getStringExtra("NOME_USUARIO");
        if (nomeUsuario != null) {
            tvBoasVindas.setText("Bem-vindo, " + nomeUsuario + "!");
        }

        btnSair.setOnClickListener(v -> {
            // Volta para a tela de login
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpa a pilha de activities
            startActivity(intent);
            finish();
        });
    }
}