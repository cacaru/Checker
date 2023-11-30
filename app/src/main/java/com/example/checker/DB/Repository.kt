package com.example.checker.DB

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import java.util.Date

class Repository(database: CheckListDB) {

    private val checklistDao : CheckListDAO = database.checklistDao();
    private val attributelistDao : AttributeListDAO = database.attributelistDao();
    private val memoDao : MemoDAO = database.memoDao();
    private val checklistviewDao : CheckListViewDAO = database.checklistviewDao();
    private val calendarmaindataDao : CalendarMainDataDAO = database.calendarmaindataDao()
    private val checklistrepeatDao : CheckListRepeatDAO = database.checklistrepeatDao()

    // all data list
    val checklist_all_data : LiveData<List<CheckList>> = checklistDao.getAllChecklist();
    val attr_all_data : LiveData<List<AttributeList>> = attributelistDao.getAllAttr();
    val memo_all_data : LiveData<List<Memo>> = memoDao.getAllMemo();
    val checklist_view_data : LiveData<List<CheckListViewer>> = checklistviewDao.getChecklistForView();
    val checklistrepeat_view_data : LiveData<List<CheckListRepeatViewer>> = checklistviewDao.getChecklistrepeatForView()

    companion object {
        private var sInstance: Repository? = null
        fun getInstance(database: CheckListDB): Repository {
            return sInstance
                ?: synchronized(this) {
                    val instance = Repository(database)
                    sInstance = instance
                    instance
                }
        }
    }

    // insert delete update 등 query area

    // checklist
    suspend fun insert_checklist(data : CheckList){
        checklistDao.insertChecklist(data);
    }

    suspend fun delete_checklist(data : CheckList){
        checklistDao.deleteChecklist(data);
    }

    suspend fun update_checklist(data : CheckList){
        checklistDao.updateChecklist(data);
    }
    fun find_checklist(find_id : Int): Flow<CheckList> {
        return checklistDao.findChecklist(find_id);
    }
    fun find_checklist(search_start_date : Date, search_end_date : Date) : Flow<List<CheckList>> {
        return checklistDao.findChecklist(search_start_date, search_end_date);
    }
    suspend fun update_one_list(id: Int, complete_val : List<Boolean>){
        checklistDao.updateOneList(id, complete_val);
    }
    suspend fun delete_one_list(id : Int){
        checklistDao.deleteOneList(id);
    }

    fun find_checklist_detail(find_id: Int) : Flow<CheckListDetailViewer> {
        return checklistviewDao.findChecklistDetailView(find_id);
    }

    fun find_calendarmaindata(search_start_date: Date, search_end_date: Date) : Flow<List<CalendarMainData>> {
        return calendarmaindataDao.findCalendarMainData(search_start_date, search_end_date);
    }
    // daylist viewer
    fun get_daylistdata(today : Date) : Flow<List<CheckListDetailViewer>> {
        return checklistviewDao.getDaylistForView(today);
    }
    fun get_daylistrepeatdata(today : Date) : Flow<List<CheckListRepeatDetailViewer>> {
        return checklistviewDao.getChecklistRepeatDetailView(today);
    }

    // checklist repeat
    suspend fun insert_checklistrepeat(data : CheckListRepeat){
        checklistrepeatDao.insertChecklistrepeat(data)
    }
    suspend fun delete_checklistrepeat(data : CheckListRepeat){
        checklistrepeatDao.deleteChecklistrepeat(data);
    }
    suspend fun update_checklistrepeat(data : CheckListRepeat){
        checklistrepeatDao.updateChecklistrepeat(data);
    }
    fun find_checklistrepeat(id : Int) : Flow<CheckListRepeat>{
        return checklistrepeatDao.findChecklistrepeat(id);
    }
    fun find_checklistrepeat(search_start_date: Date, search_end_date: Date) : Flow<List<CheckListRepeat>> {
        return checklistrepeatDao.findChecklistrepeat(search_start_date, search_end_date);
    }
    suspend fun update_one_checklistrepeat(id : Int, complete_val: List<Boolean>) {
        checklistrepeatDao.updateOneRepeatList(id, complete_val);
    }
    suspend fun delete_one_checklistrepeat(id : Int){
        checklistrepeatDao.deleteOneRepeatList(id);
    }

    fun find_checklistrepeat_detail(find_id: Int) : Flow<CheckListRepeatDetailViewer> {
        return checklistviewDao.findChecklistRepeatDetailView(find_id);
    }

    fun find_calendarmainrepeatdata() : Flow<List<CalendarMainRepeatData>> {
        return calendarmaindataDao.findCalendarMainRepeatData();
    }

    // attributelist
    suspend fun insert_attr(data : AttributeList){
        attributelistDao.insertAttribute(data);
    }

    suspend fun delete_attr(data : AttributeList){
        attributelistDao.deleteAttribute(data);
    }
    suspend fun update_attr(data : AttributeList){
        attributelistDao.updateAttribute(data);
    }

    // memo
    suspend fun insert_memo(data : Memo){
        memoDao.insertmemo(data);
    }

    suspend fun delete_memo(data : Memo){
        memoDao.deletememo(data);
    }

    suspend fun update_memo(data : Memo){
        memoDao.updatememo(data);
    }
    fun find_memo(search_start_date: Date, search_end_date: Date) : Flow<List<Memo>> {
        return memoDao.findMemo(search_start_date, search_end_date);
    }
    fun find_day_memo(search_date : Date) : Flow<Memo> {
        return memoDao.findOneMemo(search_date);
    }

}