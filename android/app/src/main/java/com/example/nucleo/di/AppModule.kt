package com.example.nucleo.di

import android.content.Context
import com.example.nucleo.repository.TransactionRepository

object AppModule {
    private var _transactionRepository: TransactionRepository? = null
    
    fun initialize(context: Context) {
        if (_transactionRepository == null) {
            _transactionRepository = TransactionRepository(context)
        }
    }
    
    val transactionRepository: TransactionRepository
        get() {
            return _transactionRepository ?: throw IllegalStateException("AppModule não foi inicializado. Chame initialize() primeiro.")
        }
}
