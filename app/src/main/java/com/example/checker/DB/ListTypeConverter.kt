package com.example.checker.DB

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson

class ListTypeConverter {
    @TypeConverter
    fun listToJson(value : List<Boolean>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value : String) : List<Boolean> {
        return Gson().fromJson(value, Array<Boolean>::class.java).toList()
    }
}