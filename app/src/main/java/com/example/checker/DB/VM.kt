package com.example.checker.DB

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

// check list view model
class VM(application: Application) : AndroidViewModel(application) {
    val checklist_all_data : LiveData<List<CheckList>>;
    val attr_all_data : LiveData<List<AttributeList>>;
    private val memo_all_data : LiveData<List<Memo>>;
    val checklist_view_data : LiveData<List<CheckListViewer>>;
    val checklistrepeat_view_data : LiveData<List<CheckListRepeatViewer>>

    private val repository : Repository

    init{
        repository = Repository.getInstance(CheckListDB.getInstance(application))!!;
        checklist_all_data = repository.checklist_all_data;
        attr_all_data = repository.attr_all_data;
        memo_all_data = repository.memo_all_data;
        checklist_view_data = repository.checklist_view_data;
        checklistrepeat_view_data = repository.checklistrepeat_view_data
    }

    // custom query
    fun find_checklist(find_id : Int) : LiveData<CheckList>{
        return repository.find_checklist(find_id).asLiveData();
    }
    fun find_checklist(search_start_date : Date, search_end_date : Date) : LiveData<List<CheckList>> {
        return repository.find_checklist(search_start_date, search_end_date).asLiveData();
    }

    fun find_memo(search_start_date: Date, search_end_date: Date) : LiveData<List<Memo>>{
        return repository.find_memo(search_start_date, search_end_date).asLiveData();
    }

    fun find_calendarmaindata(search_start_date: Date, search_end_date: Date) : LiveData<List<CalendarMainData>> {
        return repository.find_calendarmaindata(search_start_date, search_end_date).asLiveData();
    }
    fun get_daylistdata(today : Date) : LiveData<List<CheckListDetailViewer>> {
        return repository.get_daylistdata(today).asLiveData();
    }

    fun find_checklist_detail(find_id: Int) : LiveData<CheckListDetailViewer> {
        return repository.find_checklist_detail(find_id).asLiveData();
    }

    fun find_day_memo(search_date : Date) : LiveData<Memo> {
        return repository.find_day_memo(search_date).asLiveData();
    }


    // checklistrepeat custom
    fun find_checklistrepeat(id : Int) : LiveData<CheckListRepeat> {
        return repository.find_checklistrepeat(id).asLiveData()
    }
    fun find_checklistrepeat(search_start_date: Date, search_end_date: Date) : LiveData<List<CheckListRepeat>> {
        return repository.find_checklistrepeat(search_start_date, search_end_date).asLiveData()
    }
    fun find_checklistrepeat_detail(id : Int) : LiveData<CheckListRepeatDetailViewer> {
        return repository.find_checklistrepeat_detail(id).asLiveData()
    }
    fun find_calendarmain_repeat_data() : LiveData<List<CalendarMainRepeatData>> {
        return repository.find_calendarmainrepeatdata().asLiveData()
    }

    fun get_daylist_repeat_data(today: Date) : LiveData<List<CheckListRepeatDetailViewer>> {
        return repository.get_daylistrepeatdata(today).asLiveData()
    }

    // insert delete update
    fun insert_checklist(checklist : CheckList) {
        viewModelScope.launch(Dispatchers.IO) { // 코루틴으로 실행 , Dispathchers.IO => 백그라운드에서 실행
            repository.insert_checklist(checklist);
        }
    }
    fun insert_attr(attr : AttributeList) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert_attr(attr);
        }
    }
    fun insert_memo(memo: Memo){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert_memo(memo);
        }
    }
    fun insert_checklistrepeat(data : CheckListRepeat){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert_checklistrepeat(data);
        }
    }

    fun delete_checklist(checklist: CheckList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete_checklist(checklist);
        }
    }
    fun delete_attr(attr: AttributeList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete_attr(attr);
        }
    }
    fun delete_memo(memo: Memo){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete_memo(memo);
        }
    }
    fun delete_checklistrepeat(data: CheckListRepeat){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete_checklistrepeat(data);
        }
    }
    fun delete_one_list(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete_one_list(id);
        }
    }
    fun delete_one_checklistrepeat(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete_one_checklistrepeat(id);
        }
    }


    fun update_checklist(checklist : CheckList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.update_checklist(checklist);
        }
    }
    fun update_attr(attr : AttributeList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.update_attr(attr);
        }
    }
    fun update_memo(memo: Memo){
        viewModelScope.launch(Dispatchers.IO) {
            repository.update_memo(memo);
        }
    }
    fun update_checklistrepeat(data: CheckListRepeat){
        viewModelScope.launch(Dispatchers.IO) {
            repository.update_checklistrepeat(data);
        }
    }
    fun update_one_checklistrepeat(id : Int, complete_val: List<Boolean>){
        viewModelScope.launch(Dispatchers.IO) {
            repository.update_one_checklistrepeat(id, complete_val);
        }
    }
    fun update_one_list(id: Int, complete_val : List<Boolean>){
        viewModelScope.launch(Dispatchers.IO) {
            repository.update_one_list(id, complete_val);
        }
    }

}
