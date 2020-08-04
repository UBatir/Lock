package com.example.lockscreen1.data

import androidx.room.Database
import com.example.lockscreen1.data.model.Password
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lockscreen1.data.dao.PasswordDao

@Database(entities = [Password::class],version = 1)
abstract class PasswordDatabase:RoomDatabase() {

    companion object{
        private lateinit var INSTANCE:PasswordDatabase

        fun getInstance(context: Context):PasswordDatabase{
            if(!::INSTANCE.isInitialized){
                INSTANCE=Room.databaseBuilder(
                    context,
                    PasswordDatabase::class.java,
                    "database.db")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE
        }
    }
    abstract fun dao():PasswordDao
}