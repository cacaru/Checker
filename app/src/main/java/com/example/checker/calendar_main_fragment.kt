package com.example.checker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.Adapter.CalendarAdapter
import com.example.checker.DB.CalendarMainData
import com.example.checker.DB.CalendarMainRepeatData
import com.example.checker.DB.VM
import com.example.checker.Item.CalendarItem
import com.example.checker.Item.SimpleListItem
import com.example.checker.databinding.CalendarMainFragmentBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [calendar_main_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class calendar_main_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var navController: NavController
    private lateinit var binding: CalendarMainFragmentBinding
    private lateinit var today : LocalDate
    private lateinit var formatter: DateTimeFormatter
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager

    //private lateinit var day : Date\
    private val TAG : String = "checker_calendar_test"
    // db 연동
    private lateinit var vm : VM
    private lateinit var context : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        today = LocalDate.now()
        formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")

        vm = ViewModelProvider(this).get(VM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // binding을 통해 fragment 안의 id에 접근 가능함
        binding = CalendarMainFragmentBinding.inflate(layoutInflater)
        return binding.root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.calendar_main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view);

        context = view.context;
        // 화면을 가로로 최대 7개만 출력하도록 조정
        manager = GridLayoutManager(context, 7)
        recyclerView = binding.calendarMainCalendar
        recyclerView.layoutManager = manager

        // 월 이동 버튼 추가
        binding.calendarMainMonthNextBtn.setOnClickListener {
            setBtnAction("plus")
        }
        binding.calendarMainMonthPreviousBtn.setOnClickListener {
            setBtnAction("minus")
        }

        settingCalendar()

        // 체크리스트 항목 설정 페이지로 이동
        binding.calendarMainCheckerSettingBtn.setOnClickListener {
            navController.navigate(R.id.action_calendar_main_fragment_to_checklist_all_fragment)
        }

        // 설정 페이지로 이동
        binding.settingBtn.setOnClickListener {
            navController.navigate(R.id.action_calendar_main_fragment_to_option_fragment)
        }
    }

    // 캘린더 작성하기
    private fun settingCalendar() {
        // 상단 월 작성
        binding.calendarMainMonthText.text = today.format(formatter)
        // 달력 내부 설정 시작

        // 달력 내부 정보 가져오기
        //해당 월의 첫째 날
        var local_date_val = today.withDayOfMonth(1)
        val search_start_date: Date = Date.from(local_date_val.atStartOfDay(ZoneId.systemDefault()).toInstant())
        //해당 월의 마지막 날
        local_date_val = today.withDayOfMonth(today.lengthOfMonth())
        val search_end_date: Date = Date.from(local_date_val.atStartOfDay(ZoneId.systemDefault()).toInstant())

        vm.find_calendarmaindata(search_start_date, search_end_date).observe(viewLifecycleOwner) {
            val normal_list_data = it;
            vm.find_calendarmain_repeat_data().observe(viewLifecycleOwner) {
                // 내부 그리기
                val itemList = settingCalendarList(normal_list_data, it)
                // memo 추가하기
                vm.find_memo(search_start_date, search_end_date).observe(viewLifecycleOwner) {

                    if (it.isNotEmpty()){
                        for( item in it ){
                            val day_val = Integer.parseInt(item.date.toString().split(" ")[2])
                            //Log.d("checker_main_show", day_val.toString());

                            for(i in 0 until itemList.size){
                                if(itemList[i].day.isNotEmpty()){
                                    val day = Integer.parseInt(itemList[i].day);
                                    if ( day == day_val ){
                                        itemList[i].content = item.content;
                                        break;
                                    }
                                    else if ( day > day_val ) break;
                                }
                            }
                        }
                    }

                    val day_click_listener = { day : Int , simple_item : ArrayList<SimpleListItem> ->
                        // 다이얼로그 생성
                        show_summary_dialog(day, simple_item)
                    }
                    val adapter = CalendarAdapter(itemList, day_click_listener, context)
                    //adapter.notifyDataSetChanged()

                    recyclerView.adapter = adapter
                }
            }
        }

    }

    private fun show_summary_dialog(day : Int, simple_item : ArrayList<SimpleListItem> ){
        // 번들로 데이터 저장 후 navigation 이동
        val bundle = Bundle();
        // 일자만 저장
        bundle.putInt("Day", day);
        // 오늘 날짜를 통으로 저장
        var text = today.format(DateTimeFormatter.ofPattern("yyyy-MM-"));
        if ( day < 10 ) text += "0$day";
        else text += "$day";
        bundle.putString("Today", text);
        bundle.putSerializable("SimpleList", simple_item);
        setFragmentResult("DayList", bundle);
        navController.navigate(R.id.action_calendar_main_fragment_to_daylist_summary_fragment);
    }

    // 어뎁터에 전달할 데이터 만들기
    private fun settingCalendarList(normal_list_data : List<CalendarMainData>, repeat_list_data : List<CalendarMainRepeatData>) : ArrayList<CalendarItem> {
        val item = ArrayList<CalendarItem>()
        val ym = YearMonth.from(today)

        // 해당 월의 최대 수
        val max : Int = ym.lengthOfMonth()

        // 해당 월의 첫번째 날의 요일 -> 월 : 1, 일 : 7 // 1 2 3 4 5 6 7
        val first_week : Int = today.withDayOfMonth(1).dayOfWeek.value
        var day_of_week : Int = first_week

        for (i in 1..37){
            val attr_data = ArrayList<String>();
            val list_data = ArrayList<SimpleListItem>();
            if ( day_of_week > 7 ) day_of_week = 1
            if ( i <= first_week || i > max + first_week ){
                item.add(CalendarItem("",0, attr_data ,list_data ,""))
            }
            else{
                item.add(CalendarItem((i-first_week).toString(), day_of_week++, attr_data, list_data,""))
            }
        }
        // first_week가 7이면 첫 줄이 전부 공백이므로 첫 7개의 데이터를 지워야 보기 좋음
        if ( first_week == 7 ){
            item.removeAt(0)
            item.removeAt(0)
            item.removeAt(0)
            item.removeAt(0)
            item.removeAt(0)
            item.removeAt(0)
            item.removeAt(0)
        }
        // 마지막 공백 데이터들을 지움
        while(item[item.size-1].day == "" ){
            item.removeAt(item.size-1)
        }

        // data를 돌면서 item에 체크리스트 속성 갯수를 저장
        for (list in normal_list_data){
            val calendar = Calendar.getInstance()
            calendar.time = list.start_date
            val start_month : Int = calendar.get(Calendar.MONTH) + 1
            var start_day : Int = Integer.parseInt(list.start_date.toString().split(" ")[2])
            var end_day : Int = Integer.parseInt(list.end_date.toString().split(" ")[2])

            when(today.monthValue == start_month){
                true -> {
                    // 이번달에 시작한 값이고 다음달에 걸쳐서 진행된다면 end_day에 start_day를 추가
                    if ( start_day > end_day ) end_day += start_day
                }
                false -> {
                    // 이번달에 시작한 값이 아니면 start_day를 음수로 변경
                    start_day = -start_day;
                }
            }

            for(i in 0 until item.size){
                if ( item[i].day.length > 0){
                    val day = Integer.parseInt(item[i].day)
                    if ( day >= start_day && day <= end_day) {

                        // 중복하지 않고 넣기
                        if ( !item[i].attr_list.contains(list.attr_color) ){
                            item[i].attr_list.add(list.attr_color)
                        }
                        // 해당 일자에 필요한 데이터 넣기
                        item[i].has_list.add(SimpleListItem(list.name, list.attr_color));
                    }
                    if ( day > end_day ) break;
                }
            }
        }

        // repeat_data를 돌면서 item에 속성 갯수 추가하기
        for (list in repeat_list_data){
            // start_day가 이번달 이전부터라면 type에따라 시작 일이 달라짐
            // type에 따라 나누기

            // start day가 지난달 부터라면 1일부터 시작
            // 이번달에 속해있으면 해당 일자 이후 부터 진행
            val this_year : Int = today.year
            val this_month : Int = today.monthValue
            val date = Calendar.getInstance()
            date.time = list.start_date
            val start_day_year : Int = date.get(Calendar.YEAR)
            val start_day_month : Int = date.get(Calendar.MONTH) + 1
            var start_day : Int = 0
            when ( this_year == start_day_year && this_month == start_day_month ){
                true -> {
                    start_day = date.get(Calendar.DATE);
                }
                false -> {
                    start_day = 1;
                }
            }

            when(list.repeat_type){
                0 -> { // 매일
                    for(i in 0 until item.size){
                        if ( item[i].day.length > 0){
                            val day = Integer.parseInt(item[i].day)
                            if ( day >= start_day && day <= max){

                                // 중복하지 않고 넣기
                                if ( !item[i].attr_list.contains(list.attr_color) ){
                                    item[i].attr_list.add(list.attr_color)
                                }
                                // 해당 일자에 필요한 데이터 넣기
                                item[i].has_list.add(SimpleListItem(list.name, list.attr_color));
                            }
                        }
                    }
                }
                1 -> { // 주간
                    // 주간에서 해당하는 요일 찾기
                    // 요일 정보를 담고 있을 변수
                    // 0~6 월 ~ 일
                    val selected_day_of_week : ArrayList<Boolean> = ArrayList()
                    var week_val = list.week_val
                    var has_val = week_val / 1000000
                    when(has_val > 0 ) {
                        true -> selected_day_of_week.add(true);
                        false -> selected_day_of_week.add(false);
                    }
                    week_val %= 1000000
                    has_val = week_val / 100000
                    when(has_val > 0 ) {
                        true -> selected_day_of_week.add(true);
                        false -> selected_day_of_week.add(false);
                    }
                    week_val %= 100000
                    has_val = week_val / 10000
                    when(has_val > 0 ) {
                        true -> selected_day_of_week.add(true);
                        false -> selected_day_of_week.add(false);
                    }
                    week_val %= 10000
                    has_val = week_val / 1000
                    when(has_val > 0 ) {
                        true -> selected_day_of_week.add(true);
                        false -> selected_day_of_week.add(false);
                    }
                    week_val %= 1000
                    has_val = week_val / 100
                    when(has_val > 0 ) {
                        true -> selected_day_of_week.add(true);
                        false -> selected_day_of_week.add(false);
                    }
                    week_val %= 100
                    has_val = week_val / 10
                    when(has_val > 0 ) {
                        true -> selected_day_of_week.add(true);
                        false -> selected_day_of_week.add(false);
                    }
                    week_val %= 10
                    when(week_val > 0 ) {
                        true -> selected_day_of_week.add(true);
                        false -> selected_day_of_week.add(false);
                    }
                    for(i in 0 until item.size){
                        if ( item[i].day.length > 0){
                            val day = Integer.parseInt(item[i].day)
                            if ( day >= start_day && day <= max){
                                // 주간 값이 맞으면 값 넣기 시도
                                if ( selected_day_of_week[item[i].day_of_week - 1] ){
                                    // 중복하지 않고 넣기
                                    if ( !item[i].attr_list.contains(list.attr_color) ){
                                        item[i].attr_list.add(list.attr_color)
                                    }
                                    // 해당 일자에 필요한 데이터 넣기
                                    item[i].has_list.add(SimpleListItem(list.name, list.attr_color));
                                }
                            }
                            else if ( day > max ) break;
                        }
                    }
                }
                2 -> { // 정기

                    // 정기 종류에 따라 다르게 진행
                    val repeat_cycle_type = list.repeat_cycle / 100
                    val repeat_cycle_val = list.repeat_cycle % 100

                    when(repeat_cycle_type) {
                        1 -> { // 매 n일 마다
                            // start day가 지난달 부터라면 지난달의 마지막 n배수 이후부터 시작
                            // 이번달에 속해있으면 해당 일자 이후 부터 진행
                            var repeat_start_day : Int = 0;
                            when ( this_year == start_day_year && this_month == start_day_month ){
                                true -> {
                                    repeat_start_day = date.get(Calendar.DATE);
                                }
                                false -> {
                                    // 이번달 까지 n을 더해와서 시작 일자를 정해야 함 (n이 어떤 수냐에 따라 값이 달라짐 max == 99
                                    while(true){
                                        date.add(Calendar.DATE, repeat_cycle_val)
                                        val temp_year = date.get(Calendar.YEAR)
                                        val temp_month = date.get(Calendar.MONTH) + 1
                                        if ( temp_year >= this_year && this_month >= temp_month ){
                                            repeat_start_day = date.get(Calendar.DATE)
                                            break;
                                        }
                                    }
                                }
                            }
                            val temp_year = date.get(Calendar.YEAR)
                            val temp_month = date.get(Calendar.MONTH) + 1
                            // 현재 년 월에 값이 도달 할 수 있을 때만 진행
                            if ( temp_year == this_year && temp_month == this_month ){
                                for(i in 0 until item.size){
                                    if ( item[i].day.length > 0){
                                        val day = Integer.parseInt(item[i].day)
                                        if ( day == repeat_start_day ){
                                            // 중복하지 않고 넣기
                                            if ( !item[i].attr_list.contains(list.attr_color) ){
                                                item[i].attr_list.add(list.attr_color)
                                            }
                                            // 해당 일자에 필요한 데이터 넣기
                                            item[i].has_list.add(SimpleListItem(list.name, list.attr_color));
                                            date.add(Calendar.DATE, repeat_cycle_val)
                                            repeat_start_day = date.get(Calendar.DATE)
                                        }
                                    }
                                }
                            }
                        }
                        2 -> { // 매 월 n일 마다
                            // 시작 하는 달인데 정기 일보다 당일이 크면 시작 하는 달은 넘기기
                            if ( !(today.monthValue == start_day_month && today.dayOfMonth > repeat_cycle_val) ){
                                for(i in 0 until item.size){
                                    if ( item[i].day.length > 0){
                                        val day = Integer.parseInt(item[i].day)
                                        if ( day == repeat_cycle_val ){
                                            // 중복하지 않고 넣기
                                            if ( !item[i].attr_list.contains(list.attr_color) ){
                                                item[i].attr_list.add(list.attr_color)
                                            }
                                            // 해당 일자에 필요한 데이터 넣기
                                            item[i].has_list.add(SimpleListItem(list.name, list.attr_color));
                                        }
                                        if ( day > repeat_cycle_val ){ break; }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return item
    }

    // 년 월 조작
    private fun setBtnAction(act : String){
        when(act){
            "plus" -> { today = today.plusMonths(1) }
            "minus" -> { today = today.minusMonths(1) }
            else -> {}
        }
        settingCalendar()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment calendar_main_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            calendar_main_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}