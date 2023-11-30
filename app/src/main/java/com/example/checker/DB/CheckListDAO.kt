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
interface CheckListDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChecklist(vararg checkList: CheckList)

    @Delete
    fun deleteChecklist(vararg checkList: CheckList)

    @Update
    fun updateChecklist(vararg checkList: CheckList)

    @Query("SELECT * FROM checklist")
    fun getAllChecklist() : LiveData<List<CheckList>>

    @Query("SELECT * FROM checklist WHERE id = :id")
    fun findChecklist(id : Int) : Flow<CheckList>

    // 달력 생성 시 한 달 안에 포함되는 모든 체크리스트 가져오기
    @Query("SELECT * FROM checklist WHERE (start_date >= :search_start_date AND start_date <= :search_end_date) OR (end_date >= :search_start_date AND end_date <= :search_end_date)")
    fun findChecklist(search_start_date : Date, search_end_date : Date) : Flow<List<CheckList>>

    @Query("UPDATE checklist SET complete = :complete_val WHERE id = :id")
    fun updateOneList(id : Int, complete_val : List<Boolean>)

    @Query("DELETE FROM checklist WHERE id = :id")
    fun deleteOneList(id : Int)
}