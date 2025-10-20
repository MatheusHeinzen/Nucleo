package com.example.nucleo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nucleo.model.LoginRequest

class LoginViewModel : ViewModel() {
    
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        // Simulação de login - em produção, isso seria uma chamada de API
        if (email.isNotEmpty() && password.isNotEmpty()) {
            onSuccess()
        }
    }
}
