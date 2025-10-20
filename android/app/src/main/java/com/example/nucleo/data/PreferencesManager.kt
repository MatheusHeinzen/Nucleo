package com.example.nucleo.data

import android.content.Context
import android.content.SharedPreferences
import com.example.nucleo.model.Transaction
import com.example.nucleo.model.TransactionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("nucleo_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        prefs.edit().putString("transactions", json).apply()
    }
    
    fun loadTransactions(): List<Transaction> {
        val json = prefs.getString("transactions", null) ?: return emptyList()
        val type = object : TypeToken<List<Transaction>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun saveBalance(balance: Double) {
        prefs.edit().putFloat("balance", balance.toFloat()).apply()
    }
    
    fun loadBalance(): Double {
        return prefs.getFloat("balance", 295.0f).toDouble()
    }
    
    fun saveNextId(nextId: Int) {
        prefs.edit().putInt("next_id", nextId).apply()
    }
    
    fun loadNextId(): Int {
        return prefs.getInt("next_id", 4)
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
