package com.example.checker

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
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
import com.example.checker.databinding.ChecklistModifyFragmentBinding
import kotlinx.coroutines.processNextEventInCurrentThread
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [checklist_modify_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class checklist_modify_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var navController: NavController
    private lateinit var binding: ChecklistModifyFragmentBinding
    private lateinit var recyclerView: RecyclerView;
    private lateinit var manager: RecyclerView.LayoutManager;
    private lateinit var vm: VM
    private lateinit var adapter : AttributeAdapter;
    private lateinit var min_day : Calendar;
    private lateinit var max_day : Calendar;
    private lateinit var selected_attr : AttributeList;

    private lateinit var now_list : CheckList;
    private lateinit var now_repeat_list : CheckListRepeat

    private lateinit var repeat_frame : LinearLayout
    private lateinit var period_frame : LinearLayout
    private lateinit var daily_view : LinearLayout
    private lateinit var weekly_view : LinearLayout
    private lateinit var regular_view : LinearLayout
    private lateinit var repeat_radio_group : RadioGroup

    private var selected_attr_item : TextView? = null;

    // date type
    private var period_type = true;
    // 0 : 매일 , 1: 매주, 2: 정기적으로
    private var date_repeat_type : Int = 0;
    private var date_repeat_regularly_type : Int = 0;

    // 요일 선택 저장
    private val date_repeat_weekly_val : ArrayList<Boolean> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        for(i in 0..6)
            date_repeat_weekly_val.add(false);
        //binding
        binding = ChecklistModifyFragmentBinding.inflate(layoutInflater);
        period_frame = binding.calendarView;
        repeat_frame = binding.repeatView;
        // 0 매일, 1 매주, 2 정기
        daily_view = binding.repeatDailyView
        weekly_view = binding.repeatWeeklyView
        regular_view = binding.repeatRegularView

        repeat_radio_group = binding.repeatSelectGroup

        setFragmentResultListener("checklist") {key, bundle ->
            val type_val = bundle.getInt("checklist_type")
            when(type_val){
                0 -> {
                    period_type = true
                    now_list = bundle.getSerializable("checklist") as CheckList;
                }
                1 -> {
                    period_type = false
                    now_repeat_list = bundle.getSerializable("checklist") as CheckListRepeat;
                }
                else ->{
                    Log.d("checker_test", "modify data type hasnt");
                    period_type = true;
                }
            }
            init();
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        //return inflater.inflate(R.layout.checklist_modify_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager = GridLayoutManager(view.context, 2, GridLayoutManager.HORIZONTAL, false);

        recyclerView = binding.checkListRecyclerView;

        recyclerView.adapter = adapter;
        recyclerView.layoutManager = manager;

        navController = Navigation.findNavController(view);

        // 기간 설정
        val today_cal = Calendar.getInstance();
        val today_year = today_cal.get(Calendar.YEAR);
        val today_day_temp : Int = today_cal.get(Calendar.DAY_OF_MONTH);
        val today_day = if ( today_day_temp > 10 ) today_day_temp.toString() else "0"+today_day_temp.toString();
        val today_month : String = if (today_cal.get(Calendar.MONTH) + 1 < 10 ) "0"+(today_cal.get(Calendar.MONTH)+1).toString() else (today_cal.get(Calendar.MONTH)+1).toString()
        val today_text : String = "$today_year.$today_month.$today_day";
        // 기간의 시작날짜와 끝날짜 초기화
        binding.checklistStartDateText.text = today_text;
        binding.checklistEndDateText.text = today_text;

        // 두 날짜를  apply 형식으로 저장하기 위해 두개의 calendar 객체 선언
        val min_cal = Calendar.getInstance();
        val max_cal = Calendar.getInstance();
        min_day = min_cal.apply { set(today_year, today_month.toInt()-1,today_day_temp) }
        max_day = max_cal.apply { set(today_year, today_month.toInt()-1, today_day_temp) }

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
                min_day = min_cal.apply { set(year, month, day) };
                Log.d("checker", "start_datedialog");
            }
            val dialog = DatePickerDialog(this.requireContext(), start_date_text, min_cal.get(Calendar.YEAR), min_cal.get(Calendar.MONTH), min_cal.get(Calendar.DAY_OF_MONTH));
            dialog.setTitle("시작 날짜를 골라주세요.");
            dialog.datePicker.background = ContextCompat.getDrawable(this.requireContext(), R.drawable.calendar_cell_border)
            dialog.show();
//            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this.requireContext(), R.color.black));
//            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this.requireContext(), R.color.black));
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
                max_day = max_cal.apply { set(year, month, day) }
                Log.d("checker", min_day.time.toString());
            }

            val dialog2 = DatePickerDialog(this.requireContext(), end_date_text, max_cal.get(Calendar.YEAR), max_cal.get(Calendar.MONTH), max_cal.get(Calendar.DAY_OF_MONTH));
            dialog2.setTitle("끝나는 날짜를 골라주세요.");
            dialog2.datePicker.minDate = min_day.timeInMillis;
            dialog2.show();
        }

        // 뒤로가기
        binding.checklistAddpagePreBtn.setOnClickListener {
            navController.popBackStack();
        }

        // 속성 수정하기
        binding.checklistAttrAddBtn.setOnClickListener {
            navController.navigate(R.id.action_checklist_modify_fragment_to_attribute_list_fragment);
        }

        // 정기 영역
        repeat_radio_group.setOnCheckedChangeListener { radioGroup, i ->
            daily_view.isVisible = false;
            weekly_view.isVisible = false;
            regular_view.isVisible = false;
            when(i){
                R.id.radio_repeat_daliy -> {
                    date_repeat_type = 0;
                    daily_view.isVisible = true;
                }
                R.id.radio_repeat_weekly -> {
                    date_repeat_type = 1;
                    weekly_view.isVisible = true;
                }
                R.id.radio_repeat_regularly -> {
                    date_repeat_type = 2;
                    regular_view.isVisible = true;
                }
                else -> {
                    Log.d("checker_test", "radio err" + i.toString())
                }
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

        // 체크리스트 항목 수정하기
        // 현재 있는 값을 전달해 update 진행
        binding.checklistModifyBtn.setOnClickListener {
            val name : String = binding.checklistTitleText.text.toString();
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

            val context : String = binding.checklistContentText.text.toString()

            if ( !has_name || !has_attr ) {
                return@setOnClickListener
            }

            // 이곳이 실행된다 == has_name, attr이 충족되었다.
            // 기간 선택
            when(period_type){
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
                    // 현재 데이터에서 complete가 true 인 부분 찾아서 수정하기

                    val complete : List<Boolean> = temp_complete.toList();
                    var temp_day = min_day.get(Calendar.YEAR).toString() + "-" + (min_day.get(Calendar.MONTH)+1).toString() + "-" + min_day.get(Calendar.DAY_OF_MONTH)
                    val start_date : Date = SimpleDateFormat("yyyy-MM-dd").parse(temp_day);
                    temp_day = max_day.get(Calendar.YEAR).toString() + "-" + (max_day.get(Calendar.MONTH)+1).toString() + "-" + max_day.get(Calendar.DAY_OF_MONTH)
                    val end_date : Date = SimpleDateFormat("yyyy-MM-dd").parse(temp_day);

                    // checklist로 변환해서 VM으로 추가하기
                    vm.update_checklist(CheckList(now_list.id, attr_id, name, complete, start_date, end_date, context));

                }
                false ->{
                    // checklist_repeat 에 저장
                    // start_date 만 지정 -- 체크리스트를 만든 오늘
                    val temp_complete : ArrayList<Boolean> = ArrayList<Boolean>();
                    for(i in 1 .. 10000){
                        temp_complete.add(false);
                    }
                    // 현재 데이터에서 complete가 true 인 부분 찾아서 수정하기
                    // start_date가 차이 나는 날짜만큼의 complete val을 수정
                    val today = min_day.get(Calendar.YEAR).toString() + "-" + (min_day.get(Calendar.MONTH)+1).toString() + "-" + min_day.get(Calendar.DAY_OF_MONTH)
                    val today_date = SimpleDateFormat("yyyy-MM-dd").parse(today)

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
                    val data = CheckListRepeat(now_repeat_list.id, attr_id, name, complete, today_date, date_repeat_type, week_val, repeat_cycle_val, context)
                    vm.update_checklistrepeat(data)

                }
            }
            // 뒤로가기
            navController.navigate(R.id.action_checklist_modify_fragment_to_checklist_all_fragment)
        }

    }

    private fun init(){
        // spinner 설정
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

        val spinner_array = resources.getStringArray(R.array.checklist_regular_set);
        val spinner_adpater = ArrayAdapter(requireView().context, R.layout.checklist_regular_spinner_textview, spinner_array);
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
        
        when(period_type){
            true -> {
                // 이름
                binding.checklistTitleText.setText(now_list.name);

                // 현재 선택된 속성 --> adapter로 진행하는 것이기에 불가능
                // 기간 설정
                val start_date = Calendar.getInstance();
                // start_date
                start_date.time = now_list.start_date;
                var year = start_date.get(Calendar.YEAR);
                var day_temp : Int = start_date.get(Calendar.DAY_OF_MONTH);
                var day = if ( day_temp >= 10 ) day_temp.toString() else "0"+day_temp.toString();
                var month : String = if (start_date.get(Calendar.MONTH) + 1 < 10 ) "0"+(start_date.get(Calendar.MONTH)+1).toString() else (start_date.get(Calendar.MONTH)+1).toString()
                var text : String = "$year.$month.$day";
                binding.checklistStartDateText.text = text;
                min_day = start_date.apply { set(year, month.toInt()-1,day_temp) }

                // end_date
                val end_date = Calendar.getInstance();
                end_date.time = now_list.end_date;
                year = end_date.get(Calendar.YEAR);
                day_temp = end_date.get(Calendar.DAY_OF_MONTH);
                day = if ( day_temp >= 10 ) day_temp.toString() else "0"+day_temp.toString();
                month = if (end_date.get(Calendar.MONTH) + 1 < 10 ) "0"+(end_date.get(Calendar.MONTH)+1).toString() else (end_date.get(Calendar.MONTH)+1).toString()
                text = "$year.$month.$day";
                binding.checklistEndDateText.text = text;
                max_day = end_date.apply { set(year, month.toInt()-1, day_temp) }

                // 저장된 내용 작성
                binding.checklistContentText.setText(now_list.content);

                // 기간 설정의 프레임 뷰에서 period_view를 보이게하기
                period_frame.isVisible = true;
                repeat_frame.isVisible = false;
            }
            false -> {
                // 이름
                binding.checklistTitleText.setText(now_repeat_list.name)

                // 정기 설정
                binding.textView2.setText(R.string.checklist_attr_date_repeat)
                period_frame.isVisible = false;
                repeat_frame.isVisible = true;

                // 타입에 따라 보일 frame view 선정
                // 0 매일, 1 매주, 2 정기
                daily_view.isVisible = false;
                weekly_view.isVisible = false;
                regular_view.isVisible = false;
                when(now_repeat_list.repeat_type){
                    0 -> {
                        daily_view.isVisible = true
                        repeat_radio_group.check(R.id.radio_repeat_daliy)
                        date_repeat_type = 0;
                    }
                    1 -> {
                        weekly_view.isVisible = true
                        repeat_radio_group.check(R.id.radio_repeat_weekly)
                        date_repeat_type = 1
                        // 요일 오브젝트에 표기하기
                        var week_val = now_repeat_list.week_val
                        var has_val = week_val / 1000000
                        if ( has_val > 0){
                            binding.repeatMonday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                        }
                        week_val %= 1000000
                        has_val = week_val / 100000
                        if ( has_val > 0 ){
                            binding.repeatTuesday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                        }
                        week_val %= 100000
                        has_val = week_val / 10000
                        if ( has_val > 0 ){
                            binding.repeatWednesday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                        }
                        week_val %= 10000
                        has_val = week_val / 1000
                        if ( has_val > 0 ){
                            binding.repeatThursday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                        }
                        week_val %= 1000
                        has_val = week_val / 100
                        if ( has_val > 0 ){
                            binding.repeatFriday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                        }
                        week_val %= 100
                        has_val = week_val / 10
                        if ( has_val > 0 ){
                            binding.repeatSaturday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                        }
                        week_val %= 10
                        if ( week_val > 0 ){
                            binding.repeatSunday.setBackgroundResource(R.drawable.checklist_repeat_weekly_item_bg)
                        }
                    }
                    2 -> {
                        date_repeat_type = 2
                        regular_view.isVisible = true
                        repeat_radio_group.check(R.id.radio_repeat_regularly)
                        // 정기 일자 및 속성 지정
                        val repeat_regular_type = now_repeat_list.repeat_cycle / 100 - 1
                        binding.repeatRegularFeatureSpinner.setSelection(repeat_regular_type);

                        val repeat_regular_val = now_repeat_list.repeat_cycle % 100
                        binding.repeatRegularDayVal.text.clear()
                        binding.repeatRegularDayVal.text.append(repeat_regular_val.toString())

                    }
                }

                // 저장된 내용 작성
                binding.checklistContentText.setText(now_repeat_list.content)
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
         * @return A new instance of fragment checklist_modify_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            checklist_modify_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}