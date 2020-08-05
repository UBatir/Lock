package com.example.lockscreen1.data.dao

import androidx.room.*
import com.example.lockscreen1.data.model.Password

@Dao
interface PasswordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(model:Password)

    @Query("SELECT * FROM password")
    fun getPassword():Password

    @Delete
    fun deletePassword(model: Password)
}