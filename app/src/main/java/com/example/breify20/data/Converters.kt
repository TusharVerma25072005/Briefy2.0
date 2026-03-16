package com.example.breify20.data

import androidx.room.TypeConverter
import com.example.breify20.ui.screens.EmailPriority


class Converters {

    @TypeConverter
    fun fromPriority(priority: EmailPriority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): EmailPriority {
        return EmailPriority.valueOf(value)
    }
}