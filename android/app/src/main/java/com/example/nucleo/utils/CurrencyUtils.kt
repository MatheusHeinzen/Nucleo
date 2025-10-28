package com.example.nucleo.utils

import java.text.NumberFormat
import java.util.*

object CurrencyUtils {
    private val brazilianFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    
    fun formatCurrency(value: Double): String {
        return brazilianFormat.format(value)
    }
    
    fun formatCurrency(value: Double, showSign: Boolean = false): String {
        val formatted = brazilianFormat.format(value)
        return if (showSign && value > 0) {
            "+$formatted"
        } else {
            formatted
        }
    }
    
    fun parseCurrency(value: String): Double? {
        return try {
            // Remove símbolos de moeda e espaços
            val cleanValue = value
                .replace("R$", "")
                .replace(" ", "")
                .replace(".", "")
                .replace(",", ".")
            
            cleanValue.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }
}

