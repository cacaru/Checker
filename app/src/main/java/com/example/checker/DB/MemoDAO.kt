package com.example.checker.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.util.Date
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertmemo(vararg memo: Memo)

    @Delete
    fun deletememo(vararg memo: Memo)

    @Update
    fun updatememo(vararg memo: Memo)

    @Query("SELECT * FROM Memo")
    fun getAllMemo() : LiveData<List<Memo>>

    @Query("SELECT * FROM Memo WHERE date >= :search_start_date AND date <= :search_end_date")
    fun findMemo(search_start_date : Date, search_end_date : Date) : Flow<List<Memo>>

    @Query("SELECT * FROM Memo WHERE date == :search_date")
    fun findOneMemo(search_date : Date) : Flow<Memo>
}