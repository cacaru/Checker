package com.example.checker.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import java.util.Date
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckListViewDAO {

    @Query("SELECT a.id AS id, a.name AS checklist_name, b.name AS attr_name, b.color AS attr_color, a.complete AS complete FROM CheckList AS a LEFT JOIN AttributeList as b ON a.attr_id = b.attr_id")
    fun getChecklistForView() : LiveData<List<CheckListViewer>>;

    @Query("SELECT a.id AS id, a.name AS checklist_name, b.name AS attr_name, b.color AS attr_color, a.start_date AS start_date, a.end_date AS end_date , a.complete AS complete, a.content AS content FROM CheckList AS a LEFT JOIN AttributeList as b ON a.attr_id = b.attr_id WHERE (a.start_date <= :today AND a.end_date >= :today)")
    fun getDaylistForView(today : Date) : Flow<List<CheckListDetailViewer>>;

    @Query("SELECT a.id AS id, a.name AS checklist_name, b.name AS attr_name, b.color AS attr_color, a.start_date AS start_date, a.end_date AS end_date , a.complete AS complete, a.content AS content FROM CheckList AS a LEFT JOIN AttributeList as b ON a.attr_id = b.attr_id WHERE a.id == :id")
    fun findChecklistDetailView(id : Int) : Flow<CheckListDetailViewer>;

    @Query("SELECT a.id AS id, a.name AS checklistrepeat_name, b.name AS attr_name, b.color AS attr_color, a.repeat_type AS repeat_type FROM checklistrepeat AS a LEFT JOIN AttributeList AS b ON a.attr_id = b.attr_id")
    fun getChecklistrepeatForView() : LiveData<List<CheckListRepeatViewer>>

    @Query("SELECT a.id AS id, a.name AS checklistrepeat_name, b.name AS attr_name, b.color AS attr_color, a.start_date AS start_date, a.repeat_type AS repeat_type, a.week_val AS week_val, a.repeat_cycle AS repeat_cycle, a.complete AS complete, a.content AS content FROM CheckListRepeat AS a LEFT JOIN AttributeList as b ON a.attr_id = b.attr_id WHERE a.start_date <= :today")
    fun getChecklistRepeatDetailView(today : Date) : Flow<List<CheckListRepeatDetailViewer>>;

    @Query("SELECT a.id AS id, a.name AS checklistrepeat_name, b.name AS attr_name, b.color AS attr_color, a.start_date AS start_date, a.repeat_type AS repeat_type, a.week_val AS week_val, a.repeat_cycle AS repeat_cycle, a.complete AS complete, a.content AS content FROM CheckListRepeat AS a LEFT JOIN AttributeList as b ON a.attr_id = b.attr_id WHERE a.id == :id")
    fun findChecklistRepeatDetailView(id : Int) : Flow<CheckListRepeatDetailViewer>;

}