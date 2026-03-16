package com.example.breify20.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.breify20.model.email.SensitiveMapping
import java.nio.charset.CodingErrorAction.IGNORE

@Dao
interface SensitiveMappingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMapping(mapping: SensitiveMapping)
    @Query("SELECT pairs FROM SensitiveMapping WHERE emailId = :emailId")
    suspend fun getPairs(emailId: String): String
}
