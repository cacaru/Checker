package com.example.checker.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "CheckListRepeat",
    foreignKeys = [ ForeignKey(
        entity = AttributeList::class,
        parentColumns = ["attr_id"],
        childColumns = ["attr_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class CheckListRepeat(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    val id : Int = 0,

    @ColumnInfo(name="attr_id")
    val attr_id : Int,

    @ColumnInfo(name="name")
    val name : String,

    @ColumnInfo(name="complete")
    val complete : List<Boolean>, // 최대 1만개 까지 저장

    @ColumnInfo(name="start_date")
    val start_date : Date,

    @ColumnInfo(name="repeat_type")
    val repeat_type : Int,

    @ColumnInfo(name="week_val")
    val week_val : Int,

    @ColumnInfo(name="repeat_cycle")
    val repeat_cycle : Int,

    @ColumnInfo(name="content")
    val content : String


): Serializable