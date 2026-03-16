package com.example.breify20.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var db: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {

        return db ?: synchronized(this) {

            db ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "briefy_db"
            ).build().also {
                db = it
            }

        }

    }
}