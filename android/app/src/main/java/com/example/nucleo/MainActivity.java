package com.example.nucleo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nucleo.api.ApiClient;
import com.example.nucleo.api.ApiService;
import com.example.nucleo.model.UsuarioLogin;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etSenha;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Liga os elementos da tela às variáveis Java
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        // Configura o clique do botão
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fazerLogin();
            }
        });

        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        btnCadastrar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CadastroActivity.class));
        });
    }

    private void fazerLogin() {
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email é obrigatório");
            return;
        }
        if (senha.isEmpty()) {
            etSenha.setError("Senha é obrigatória");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // (1) Cria o objeto com os dados de login
        UsuarioLogin usuarioLogin = new UsuarioLogin(email, senha);

        // (2) Obtém o serviço da API
        ApiService apiService = ApiClient.getApiService();

        // (3) Faz a chamada ASSÍNCRONA para o endpoint de login
        Call<Void> call = apiService.login(usuarioLogin);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    intent.putExtra("NOME_USUARIO", email); // Ou o nome real do usuário, se a API retornar
                    startActivity(intent);
                    finish(); // Fecha a tela de login
                } else {
                    // Login falhou (Código HTTP de erro, como 401 Unauthorized)
                    Toast.makeText(MainActivity.this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Falha na rede ou na conversão de dados
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Log.e("LOGIN_ERROR", "Erro na chamada à API: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Erro de conexão. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}