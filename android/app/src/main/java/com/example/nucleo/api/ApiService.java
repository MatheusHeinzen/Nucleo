package com.example.nucleo.api;

import com.example.nucleo.model.UsuarioLogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    // Endpoint de Login - AGORA CORRETO!
    @POST("api/usuarios/login") // <- Caminho completo do novo endpoint
    Call<Void> login(@Body UsuarioLogin usuarioLogin); // Usa o objeto UsuarioLogin (email e senha)
}