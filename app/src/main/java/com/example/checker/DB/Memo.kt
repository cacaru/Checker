package com.example.checker.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Memo(
    @PrimaryKey
    val date : Date,

    @ColumnInfo(name="content")
    val content : String,
)
