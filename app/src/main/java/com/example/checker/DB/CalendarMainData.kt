package com.example.checker.DB

import java.util.Date

data class CalendarMainData(
    val name : String,
    val attr_color : String,
    val start_date : Date,
    val end_date : Date
)

data class CalendarMainRepeatData(
    val name : String,
    val attr_color : String,
    val start_date : Date,
    val repeat_type : Int,
    val week_val : Int,
    val repeat_cycle : Int
)
