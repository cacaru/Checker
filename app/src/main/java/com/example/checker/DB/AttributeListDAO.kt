package com.example.checker.DB

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AttributeListDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttribute(vararg attributeList: AttributeList)

    @Delete
    fun deleteAttribute(vararg attributeList: AttributeList)

    @Update
    fun updateAttribute(vararg attributeList: AttributeList)

    @Query("SELECT * FROM AttributeList")
    fun getAllAttr() : LiveData<List<AttributeList>>
}