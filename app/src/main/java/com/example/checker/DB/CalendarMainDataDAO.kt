package com.example.checker.DB

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CalendarMainDataDAO {

    @Query("SELECT a.name as name, b.color as attr_color, a.start_date as start_date, a.end_date as end_date FROM checklist as a LEFT JOIN attributelist as b ON a.attr_id == b.attr_id WHERE (a.start_date >= :search_start_date AND a.start_date <= :search_end_date) OR (a.end_date >= :search_start_date AND a.end_date <= :search_end_date)")
    fun findCalendarMainData(search_start_date : Date, search_end_date : Date) : Flow<List<CalendarMainData>>

    @Query("SELECT a.name as name, b.color as attr_color, a.start_date as start_date, a.repeat_type AS repeat_type, a.week_val AS week_val, a.repeat_cycle AS repeat_cycle FROM checklistrepeat as a LEFT JOIN attributelist as b ON a.attr_id == b.attr_id")
    fun findCalendarMainRepeatData() : Flow<List<CalendarMainRepeatData>>
}