package com.example.breify20.data

import androidx.room.TypeConverter
import com.example.breify20.model.email.Category
import com.example.breify20.ui.screens.EmailPriority


class Converters {

    @TypeConverter
    fun fromPriority(priority: EmailPriority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): EmailPriority {
        return try{
            EmailPriority.valueOf(value.uppercase())
        }catch (e : Exception){
            EmailPriority.NORMAL
        }
    }

    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(value: String): Category {
        return try {
            Category.valueOf(value.uppercase())
        } catch (e: Exception) {
            Category.WORK
        }
    }
}