package com.example.checker.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CheckListRepeatDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChecklistrepeat(vararg CheckListRepeat: CheckListRepeat)

    @Delete
    fun deleteChecklistrepeat(vararg CheckListRepeat: CheckListRepeat)

    @Update
    fun updateChecklistrepeat(vararg CheckListRepeat: CheckListRepeat)

    @Query("SELECT * FROM CheckListRepeat")
    fun getAllChecklistrepeat() : LiveData<List<CheckListRepeat>>

    @Query("SELECT * FROM CheckListRepeat WHERE id = :id")
    fun findChecklistrepeat(id : Int) : Flow<CheckListRepeat>

    // 달력 생성 시 시작 일자가 그 달 안에 포함되는 모든 반복체크리스트 가져오기
    @Query("SELECT * FROM CheckListRepeat WHERE (start_date >= :search_start_date AND start_date <= :search_end_date)")
    fun findChecklistrepeat(search_start_date : Date, search_end_date : Date) : Flow<List<CheckListRepeat>>

    @Query("UPDATE CheckListRepeat SET complete = :complete_val WHERE id = :id")
    fun updateOneRepeatList(id : Int, complete_val : List<Boolean>)

    @Query("DELETE FROM CheckListRepeat WHERE id = :id")
    fun deleteOneRepeatList(id : Int)
}