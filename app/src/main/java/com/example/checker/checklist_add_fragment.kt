package com.example.checker

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.nfc.Tag
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.Adapter.AttributeAdapter
import com.example.checker.DB.AttributeList
import com.example.checker.DB.CheckList
import com.example.checker.DB.CheckListRepeat
import com.example.checker.DB.VM
import com.example.checker.databinding.ChecklistAddFragmentBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [checklist_add_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class checklist_add_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var navController: NavController
    lateinit var binding: ChecklistAddFragmentBinding
    lateinit var recyclerView: RecyclerView;
    lateinit var manager: RecyclerView.LayoutManager;
    lateinit var vm: VM
    lateinit var adapter : AttributeAdapter;
    lateinit var min_day : Calendar;
    lateinit var max_day : Calendar;
    lateinit var selected_attr : AttributeList;
    var selected_attr_item : TextView? = null;
    private var is_dday = false;

    // date type
    private var date_type_is_period : Boolean = true;
    // 0 : 매일 , 1: 매주, 2: 정기적으로
    private var date_repeat_type : Int = 0;
    private var date_repeat_regularly_type : Int = 0;

    // 요일 선택 저장
    private val date_repeat_weekly_val : ArrayList<Boolean> = ArrayList();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        for(i in 0..6)
            date_repeat_weekly_val.add(false);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding
        binding = ChecklistAddFragmentBinding.inflate(layoutInflater);

        adapter = AttributeAdapter { item , attr ->
            selected_attr = attr;
            if ( selected_attr_item != null ){
                // 기존의 것을 기본 값으로 변경해 줌
                selected_attr_item?.background = ContextCompat.getDrawable(this.requireContext(), R.drawable.checklist_attr_border);
                //selected_attr_item?.setBackgroundResource(R.drawable.checklist_attr_border);
            }
            selected_attr_item = item;
            selected_attr_item?.background = ContextCompat.getDrawable(this.requireContext(), R.drawable.checklist_attr_select_border);
            //selected_attr_item?.setBackgroundResource(R.drawable.checklist_attr_select_border);
        }

        vm = ViewModelProvider(this).get(VM::class.java)
        vm.attr_all_data.observe(viewLifecycleOwner, Observer {
                data -> adapter.setItem(data);
        })
        return binding.root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.checklist_add_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager = GridLayoutManager(view.context, 2, GridLayoutManager.HORIZONTAL, false);

        recyclerView = binding.checkListAddRecyclerView;

        recyclerView.adapter = adapter;
        recyclerView.layoutManager = manager;

        navController = Navigation.findNavController(view);

        // 기간 설정
        val today_cal = Calendar.getInstance();
        val today_year = today_cal.get(Calendar.YEAR);
        val today_day_temp : Int = today_cal.get(Calendar.DAY_OF_MONTH);
        val today_day = if ( today_day_temp >= 10 ) today_day_temp.toString() else "0"+today_day_temp.toString();
        val today_month : String = if (today_cal.get(Calendar.MONTH) + 1 < 10 ) "0"+(today_cal.get(Calendar.MONTH)+1).toString() else (today_cal.get(Calendar.MONTH)+1).toString()
        val today_text : String = "$today_year.$today_month.$today_day";
        // 기간의 시작날짜와 끝날짜 초기화
        binding.checklistStartDateText.text = today_text;
        binding.checklistEndDateText.text = today_text;

        // 두 날짜를  apply 형식으로 저장하기 위해 두개의 calendar 객체 선언
        val min_cal = Calendar.getInstance();
        val max_cal = Calendar.getInstance();
        min_day = min_cal.apply { set(today_year, today_month.toInt()-1,today_day_temp, 0, 0, 0) }
        max_day = max_cal.apply { set(today_year, today_month.toInt()-1, today_day_temp, 0, 0, 0) }

        // 특정 일자에서 추가하러 올 경우 시작날짜와 끝 날짜를 특정날짜로 설정하기
        setFragmentResultListener("DayListAdd") { key, bundle ->
            is_dday = true;
            val d_day = bundle.getString("Dday")!!;
            // d_day로 min_day, max_day 재설정
            // yyyy-MM-dd
            val d_day_year : Int = d_day.substring(0,4).toInt();
            val d_day_month : Int = d_day.substring(5,7).toInt();
            val d_day_day : Int = d_day.substring(8,10).toInt();
            val d_day_text : String = "$d_day_year.$d_day_month.$d_day_day";

            binding.checklistStartDateText.text = d_day_text;
            binding.checklistEndDateText.text = d_day_text;
            min_day = min_cal.apply { set(d_day_year, d_day_month-1, d_day_day) }
            max_day = max_cal.apply { set(d_day_year, d_day_month-1, d_day_day) }
        }

        // text_color는 black 계열에서는 흰색으로 이외에는 검은색으로
        val sf = this.requireActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val theme_val = sf.getInt("theme", 0)
        val text_color = when(theme_val){
            0, 1, 2, 3, 9 -> ContextCompat.getColor(this.requireActivity(), R.color.black)
            4, 5, 6, 7, 8 -> ContextCompat.getColor(this.requireActivity(), R.color.white)
            else -> ContextCompat.getColor(this.requireActivity(), R.color.blue);
        }

        // 기간 설정 - 시작 일자 달력 클릭 이벤트
        binding.checklistStartDateCalendar.setOnClickListener {
            val start_date_text = DatePickerDialog.OnDateSetListener { view, year, month, day ->
                var check_day : String = "";
                if ( month+1 >= 10 && day >= 10)
                    check_day = "${year}.${month+1}.${day}";
                else if ( month + 1 >= 10 && day < 10 )
                    check_day = "${year}.${month+1}.0${day}";
                else if ( month + 1 < 10 && day >= 10 )
                    check_day = "${year}.0${month+1}.${day}";
                else
                    check_day = "${year}.0${month+1}.0${day}";
                binding.checklistStartDateText.text = check_day;
                min_day = min_cal.apply { set(year, month, day, 0, 0 ,0) };
                //Log.d("checker", "start_datedialog");
            }
            val dialog = DatePickerDialog(this.requireContext(), start_date_text, min_cal.get(Calendar.YEAR), min_cal.get(Calendar.MONTH), min_cal.get(Calendar.DAY_OF_MONTH));
            dialog.setTitle("시작 날짜를 골라주세요.");
            dialog.datePicker.background = ContextCompat.getDrawable(this.requireContext(), R.drawable.calendar_cell_border)
            dialog.show();
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(text_color);
            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(text_color);

        }
        // 기간 설정 - 마지막 일자 달력 클릭 이벤트
        binding.checklistEndDateCalendar.setOnClickListener {
            val end_date_text = DatePickerDialog.OnDateSetListener { view, year, month, day ->
                var check_day : String = "";
                if ( month+1 >= 10 )
                    check_day = "${year}.${month+1}.${day}"
                else
                    check_day = "${year}.0${month+1}.${day}"
                binding.checklistEndDateText.text = check_day;
                max_day = max_cal.apply { set(year, month, day, 0, 0, 0) }
                //Log.d("checker", min_day.time.toString());
            }

            val dialog2 = DatePickerDialog(this.requireContext(), end_date_text, max_cal.get(Calendar.YEAR), max_cal.get(Calendar.MONTH), max_cal.get(Calendar.DAY_OF_MONTH));
            dialog2.setTitle("끝나는 날짜를 골라주세요.");
            dialog2.datePicker.minDate = min_day.timeInMillis;
            dialog2.show();
            dialog2.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(text_color)
            dialog2.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(text_color)
        }

        // 뒤로가기
        binding.checklistAddpagePreBtn.setOnClickListener {
            when {
                is_dday -> navController.navigate(R.id.action_checklist_add_fragment_to_calendar_main_fragment2)
                else-> navController.popBackStack();
            }
        }
        // 속성 수정하러 가기
        binding.checklistAttrAddBtn.setOnClickListener {
            navController.navigate(R.id.action_checklist_add_fragment_to_attribute_list_fragment);
        }

        // 기간 - 반복 선택
        val period_frame = binding.calendarView;
        val repeat_frame = binding.repeatView;

        // 반복 선택의 radio group 조절
        val repeat_radio_group = binding.repeatSelectGroup;
        val repeat_detail_daily = binding.repeatDailyView;
        val repeat_detail_weekly = binding.repeatWeeklyView;
        val repeat_detail_regular = binding.repeatRegularView;
        repeat_radio_group.check(R.id.radio_repeat_daliy)
        repeat_detail_daily.isVisible = true;
        repeat_detail_weekly.isVisible = false;
        repeat_detail_regular.isVisible = false;
        repeat_radio_group.setOnCheckedChangeListener { radioGroup, i ->
            repeat_detail_daily.isVisible = false;
            repeat_detail_weekly.isVisible = false;
            repeat_detail_regular.isVisible = false;
            when(i){
                R.id.radio_repeat_daliy -> {
                    date_repeat_type = 0;
                    repeat_detail_daily.isVisible = true;
                }
                R.id.radio_repeat_weekly -> {
                    date_repeat_type = 1;
                    repeat_detail_weekly.isVisible = true;
                }
                R.id.radio_repeat_regularly -> {
                    date_repeat_type = 2;
                    repeat_detail_regular.isVisible = true;
                }
                else -> {
                    Log.d("checker_test", "radio err" + i.toString())
                }
            }
        }

        // 기본 == 기간 선택
        date_type_is_period = true;
        period_frame.isVisible = true;
        repeat_frame.isVisible = false;
        binding.checklistSelectDateTypeText.setText(R.string.checklist_attr_date_period);

        val TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }
            override fun afterTextChanged(s: Editable?) {
                // 31까지만 입력받음
                val input_val = s.toString();
                if( input_val.length > 0 ){
                    var temp_val = input_val.toInt();
                    if ( temp_val > 31 ){
                        temp_val = 31;
                        s?.clear()
                        s?.append(temp_val.toString());
                    }
                }
            }
        }

        // 정기 기간 설정
        val spinner_array = resources.getStringArray(R.array.checklist_regular_set);
        val spinner_adpater = ArrayAdapter(view.context, R.layout.checklist_regular_spinner_textview, spinner_array);
        binding.repeatRegularFeatureSpinner.adapter = spinner_adpater;
        binding.repeatRegularFeatureSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                when (position) {
                    0 -> { // 매
                        //Log.d("checker_add_spinner", "spinner click 매")
                        date_repeat_regularly_type = 1;
                        binding.repeatRegularDayVal.removeTextChangedListener(TextWatcher)
                    }

                    1 -> { // 매월
                        //Log.d("checker_add_spinner", "spinner click 매 월")
                        date_repeat_regularly_type = 2;
                        // 정기 일 적는 edittext 리스너 등록
                        binding.repeatRegularDayVal.addTextChangedListener(TextWatcher)
                        // 현재 작성되어 있는 값이 31 이상이면 변경
                        val input_val = binding.repeatRegularDayVal.text.toString()
                        if( input_val.length > 0 ) {
                            if ( input_val.toInt() > 31 ){
                                binding.repeatRegularDayVal.text.clear()
                                binding.repeatRegularDayVal.text.append("31");
                            }
                        }
                    }

                    else -> { // 오류
                        Log.d("checker_add_Err", "spinner click err")
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }

        // 월 화 수 목 금 토 일 선택 버튼
        binding.repeatMonday.setOnClickListener {
            repeat_weekly_val(0)
        }
        binding.repeatTuesday.setOnClickListener {
            repeat_weekly_val(1)
        }
        binding.repeatWednesday.setOnClickListener {
            repeat_weekly_val(2)
        }
        binding.repeatThursday.setOnClickListener {
            repeat_weekly_val(3)
        }
        binding.repeatFriday.setOnClickListener {
            repeat_weekly_val(4)
        }
        binding.repeatSaturday.setOnClickListener {
            repeat_weekly_val(5)
        }
        binding.repeatSunday.setOnClickListener {
            repeat_weekly_val(6)
        }

        //전환 버튼
        binding.checklistTypeSelecter.setOnClickListener {
            period_frame.isVisible = !period_frame.isVisible
            repeat_frame.isVisible = !repeat_frame.isVisible;
            date_type_is_period = !date_type_is_period;
            // 안내 문구 변경
            when(date_type_is_period) {
                true -> binding.checklistSelectDateTypeText.setText(R.string.checklist_attr_date_period)
                false -> binding.checklistSelectDateTypeText.setText(R.string.checklist_attr_date_repeat)
            }
        }

            // 체크리스트 항목 추가하기
        binding.checklistAddBtn.setOnClickListener {
            // 이름
            val name : String = binding.checklistAddTitleText.text.toString();
            var has_name : Boolean = false;
            if (name.isEmpty()){
                val toast1 = Toast.makeText(view.context, "목표를 1자 이상 입력해주세요.", Toast.LENGTH_SHORT)
                toast1.setGravity(Gravity.BOTTOM, 0, 200)
                toast1.show();
                //Log.d("checker_add_btn_click", "이름이 0자임");
            }
            else{
                has_name = true;
                //Log.d("checker_add_btn_click", "이름이 1자이상 임");
            }
            // 속성
            var attr_id : Int = -1;
            var has_attr : Boolean = false;
            if ( this::selected_attr.isInitialized ){
                attr_id = selected_attr.attr_id;
                has_attr = true;
            }
            else {
                attr_id = -1;
                val toast2 = Toast.makeText(view.context, "속성을 선택해주세요.", Toast.LENGTH_SHORT)
                toast2.setGravity(Gravity.BOTTOM, 0, 100)
                toast2.show();
            }
            // 내용
            val context : String = binding.checklistAddContentText.text.toString()

            if ( !has_name || !has_attr ) {
                return@setOnClickListener
            }

            // 이곳이 실행된다 == has_name, attr이 충족되었다.
            // 기간 선택
            when(date_type_is_period){
                true -> {
                    // checklist 에저장
                    // start_date, end_date 지정
                    // 달력에 저장된 값
                    // start_date 당일부터 end_Date 당일까지의 날짜 수 계산
                    val dif : Int = ((max_day.timeInMillis - min_day.timeInMillis) / (24 * 60 * 60 * 1000 )).toInt() + 1
                    val temp_complete : ArrayList<Boolean> = ArrayList<Boolean>();
                    for(i in 1 .. dif){
                        temp_complete.add(false);
                    }
                    val complete : List<Boolean> = temp_complete.toList();
                    var temp_day = min_day.get(Calendar.YEAR).toString() + "-" + (min_day.get(Calendar.MONTH)+1).toString() + "-" + min_day.get(Calendar.DAY_OF_MONTH)
                    val start_date : Date = SimpleDateFormat("yyyy-MM-dd").parse(temp_day);
                    temp_day = max_day.get(Calendar.YEAR).toString() + "-" + (max_day.get(Calendar.MONTH)+1).toString() + "-" + max_day.get(Calendar.DAY_OF_MONTH)
                    val end_date : Date = SimpleDateFormat("yyyy-MM-dd").parse(temp_day);

                    // checklist로 변환해서 VM으로 추가하기
                    vm.insert_checklist(CheckList(0, attr_id, name, complete, start_date, end_date, context));
                    navController.popBackStack();

                }
                false ->{
                    // checklist_repeat 에 저장
                    // start_date 만 지정 -- 체크리스트를 만든 오늘
                    val temp_complete : ArrayList<Boolean> = ArrayList<Boolean>();
                    for(i in 1 .. 1000){
                        temp_complete.add(false);
                    }
                    val today = min_day.get(Calendar.YEAR).toString() + "-" + (min_day.get(Calendar.MONTH)+1).toString() + "-" + min_day.get(Calendar.DAY_OF_MONTH)
                    val today_date = SimpleDateFormat("yyyy-MM-dd").parse(today)

                    // repeat_type
                    // week_val
                    // repeat_cycle

                    var week_val = 0;
                    var repeat_cycle_val = 0;

                    // repeat_type에 따라 필요한 데이터가 수집되었는지 확인
                    when(date_repeat_type){
                        0 -> { // 매일
                            //필요 데이터 없음
                        }
                        1 -> { // 매주
                            // 어떤 요일에 반복할지
                            // 00000000
                            // 월화수목금토일
                            // /1000000 = 1 :: 월 ~~
                            // 1011011 -> 월 수목 토일 반복됨
                            // 선택된 요일이 없으면 Toast 뿌리고 끝내기
                            // week_val
                            var temp_val : String = "";
                            for(i in 0 .. 6){
                                if ( date_repeat_weekly_val[i] ){
                                    temp_val += "1"
                                }
                                else{
                                    temp_val += "0"
                                }
                            }
                            week_val = temp_val.toInt()
                            if ( week_val == 0 ){
                                val toast3 = Toast.makeText(view.context, "요일을 선택해주세요.", Toast.LENGTH_SHORT)
                                toast3.setGravity(Gravity.BOTTOM, 0, 100)
                                toast3.show();
                                return@setOnClickListener
                            }
                        }
                        2 -> { // 정기
                            // 1 매 n일 마다
                            // 2 매월 n일 마다
                            // n의 max 값 == 99
                            // 2면 n <= 31
                            // repeat_cycle
                            repeat_cycle_val += date_repeat_regularly_type * 100
                            val nval = binding.repeatRegularDayVal.text.toString()
                            if ( nval.isEmpty() ) {
                                val toast3 = Toast.makeText(view.context, "정기 일을 입력해주세요.", Toast.LENGTH_SHORT)
                                toast3.setGravity(Gravity.BOTTOM, 0, 100)
                                toast3.show();
                                return@setOnClickListener
                            }
                            else {
                                repeat_cycle_val += nval.toInt();
                            }

                            Log.d("checker_test", repeat_cycle_val.toString());
                        }
                        else -> {
                            //err
                        }
                    }

                    val complete : List<Boolean> = temp_complete.toList()
                    // 모든 내용이 담겼으므로 vm을 통해 checklistrepeat을 생성
                    val data = CheckListRepeat(0, attr_id, name, complete, today_date, date_repeat_type, week_val, repeat_cycle_val, context)
                    vm.insert_checklistrepeat(data)
                    // 뒤로가기
                    navController.popBackStack()
                }
            }
        }
    }

    private fun repeat_weekly_val(dow : Int){
        when(dow){
            0 -> { // 월
                date_repeat_weekly_val[0] = !date_repeat_weekly_val[0]
                when(date_repeat_weekly_val[0]){
                    true -> {
                        binding.repeatMonday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                    }
                    false -> {
                        binding.repeatMonday.setBackgroundResource(R.color.white)
                    }
                }
            }
            1 -> { // 화
                date_repeat_weekly_val[1] = !date_repeat_weekly_val[1]
                when(date_repeat_weekly_val[1]) {
                    true -> {
                        binding.repeatTuesday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                    }
                    false -> {
                        binding.repeatTuesday.setBackgroundResource(R.color.white)
                    }
                }
            }
            2 -> { // 수
                date_repeat_weekly_val[2] = !date_repeat_weekly_val[2]
                when(date_repeat_weekly_val[2]){
                    true -> {
                        binding.repeatWednesday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                    }
                    false -> {
                        binding.repeatWednesday.setBackgroundResource(R.color.white)
                    }
                }
            }
            3 -> { // 목
                date_repeat_weekly_val[3] = !date_repeat_weekly_val[3]
                when(date_repeat_weekly_val[3]){
                    true -> {
                        binding.repeatThursday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                    }
                    false -> {
                        binding.repeatThursday.setBackgroundResource(R.color.white)
                    }
                }
            }
            4 -> { // 금
                date_repeat_weekly_val[4] = !date_repeat_weekly_val[4]
                when(date_repeat_weekly_val[4]){
                    true -> {
                        binding.repeatFriday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                    }
                    false -> {
                        binding.repeatFriday.setBackgroundResource(R.color.white)
                    }
                }
            }
            5 -> { // 토
                date_repeat_weekly_val[5] = !date_repeat_weekly_val[5]
                when(date_repeat_weekly_val[5]){
                    true -> {
                        binding.repeatSaturday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                    }
                    false -> {
                        binding.repeatSaturday.setBackgroundResource(R.color.white)
                    }
                }
            }
            6 -> { // 일
                date_repeat_weekly_val[6] = !date_repeat_weekly_val[6]
                when(date_repeat_weekly_val[6]){
                    true -> {
                        binding.repeatSunday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                    }
                    false -> {
                        binding.repeatSunday.setBackgroundResource(R.color.white)
                    }
                }
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment checklist_add_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            checklist_add_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
