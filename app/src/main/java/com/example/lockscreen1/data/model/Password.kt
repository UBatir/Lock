package com.example.lockscreen1.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "password")
class Password (
    @PrimaryKey(autoGenerate = true) val id:Int=1,
    @ColumnInfo(name="key") var key:String
)