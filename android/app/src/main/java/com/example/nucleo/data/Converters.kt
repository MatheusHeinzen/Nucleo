package com.example.nucleo.data

import androidx.room.TypeConverter
import com.example.nucleo.model.TransactionType

class Converters {
    
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }
    
    @TypeConverter
    fun toTransactionType(type: String): TransactionType {
        return TransactionType.valueOf(type)
    }
}
