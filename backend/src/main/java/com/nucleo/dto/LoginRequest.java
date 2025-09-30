package com.nucleo.dto;

public class LoginRequest {
    private String email;
    private String senha;

    // Construtor vazio (OBRIGATÓRIO para o Jackson)
    public LoginRequest() {
    }

    // Construtor com campos
    public LoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    // Getters e Setters (OBRIGATÓRIOS para o Jackson)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}