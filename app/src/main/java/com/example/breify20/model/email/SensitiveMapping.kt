package com.example.breify20.model.email

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SensitiveMapping(
    @PrimaryKey
    val emailId: String,
    val pairs: String
)