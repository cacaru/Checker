package com.example.checker.DB

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "AttributeList")
data class AttributeList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="attr_id")
    val attr_id : Int = 0,

    @ColumnInfo(name="name") val name : String,
    @ColumnInfo(name="color") val color: String,
) : Serializable