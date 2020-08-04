package com.example.lockscreen1.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lockscreen1.data.model.Password

@Dao
interface PasswordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(model:Password)

    @Query("SELECT * FROM password")
    fun getAllContact():Password
}