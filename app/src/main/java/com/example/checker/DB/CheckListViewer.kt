package com.example.checker.DB

import java.io.Serializable
import java.util.Date

data class CheckListViewer(
    val id : Int,
    val checklist_name : String,
    val attr_name : String,
    val attr_color : String,
    val complete : Boolean,
): Serializable

data class CheckListRepeatViewer(
    val id : Int,
    val checklistrepeat_name : String,
    val attr_name : String,
    val attr_color : String,
    val repeat_type : Int,
)

data class CheckListDetailViewer(
    val id : Int,
    val checklist_name : String,
    val attr_name : String,
    val attr_color : String,
    val start_date : Date,
    val end_date : Date,
    val complete : List<Boolean>,
    val content : String,
) : Serializable

data class CheckListRepeatDetailViewer(
    val id : Int,
    val checklistrepeat_name : String,
    val attr_name : String,
    val attr_color : String,
    val start_date : Date,
    val repeat_type : Int,
    val week_val : Int,
    val repeat_cycle : Int,
    val complete : List<Boolean>,
    val content : String,
)

data class ListViewer(
    val normal_list: CheckListViewer?,
    val repeat_list: CheckListRepeatViewer?
)

data class DayListViewer(
    val normal_list : CheckListDetailViewer?,
    val repeat_list : CheckListRepeatDetailViewer?
)