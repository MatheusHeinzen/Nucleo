package com.example.nucleo.model

data class User(
    val id: String,
    val email: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)
