package com.example.checker.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.Serializable
import java.util.Date

@Entity(tableName = "CheckList",
    foreignKeys = [ ForeignKey(
        entity = AttributeList::class,
        parentColumns = ["attr_id"],
        childColumns = ["attr_id"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class CheckList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    val id : Int = 0,

    @ColumnInfo(name="attr_id")
    val attr_id : Int,

    @ColumnInfo(name="name")
    val name : String,

    @ColumnInfo(name="complete")
    val complete : List<Boolean>,

    @ColumnInfo(name="start_date")
    val start_date : Date,

    @ColumnInfo(name="end_date")
    val end_date : Date,

    @ColumnInfo(name="content")
    val content : String
) : Serializable