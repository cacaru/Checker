package com.example.checker.DB

import android.graphics.Color
import androidx.room.TypeConverter

class ColorTypeConverter {
    @TypeConverter
    fun fromColor(color: Color) : Int? {
        return color.toArgb()
    }

    @TypeConverter
    fun toColor(color : Long) : Color {
        return Color.valueOf(color)
    }
}