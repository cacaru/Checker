package com.example.checker.Item

data class CalendarItem(
    val day : String,               // 날짜
    val day_of_week : Int,          // 요일
    val attr_list : ArrayList<String>,         // 달력에 표시될 속성 정보
    val has_list : ArrayList<SimpleListItem>,  // 그날에 저장된 체크리스트들
    var content : String,           // 메모 정보
)
