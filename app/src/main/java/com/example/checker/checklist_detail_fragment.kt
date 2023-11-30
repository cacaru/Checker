package com.example.checker

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.Adapter.CheckListDetailAdapter
import com.example.checker.Adapter.DayListDetailAdapter
import com.example.checker.DB.VM
import com.example.checker.Item.ChecklistCompleteItem
import com.example.checker.Item.SimpleListItem
import com.example.checker.databinding.ChecklistDetailFragmentBinding
import com.example.checker.databinding.DaylistDetailsFragmentBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [checklist_detail_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class checklist_detail_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var navController: NavController
    private lateinit var binding: ChecklistDetailFragmentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var vm : VM

    private val TAG : String = "checker_detail_test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChecklistDetailFragmentBinding.inflate(layoutInflater);
        vm = ViewModelProvider(this).get(VM::class.java)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // navigation 설정
        navController = Navigation.findNavController(view);
        recyclerView = binding.checklistDetailRecyclerView;
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        setFragmentResultListener("checklist_detail") {key, bundle ->

            val type : Int = bundle.getInt("checklist_type")
            val id : Int = bundle.getInt("checklist_id");
            //Log.d("checker_test", id.toString());
            when(type){
                0 -> {
                    binding.checklistDetailTypeText.setText("기간")

                    vm.find_checklist_detail(id).observe(viewLifecycleOwner) {
                        // 이름
                        binding.checklistDetailTitleText.setText(it.checklist_name);

                        // 속성
                        binding.checklistDetailAttrText.setText(it.attr_name);
                        binding.checklistDetailAttrText.setTextColor(Color.BLACK);
                        binding.checklistDetailAttrText.setBackgroundResource(R.drawable.checklist_attr_border);
                        val color_val = Color.parseColor(it.attr_color);
                        binding.checklistDetailAttrText.backgroundTintList = ColorStateList.valueOf(color_val);

                        // 내용
                        binding.checklistDetailContentText.setText(it.content);

                        // 완료 보기 항목 설정
                        // adapter에 넘겨줄 변수 생성
                        val item : ArrayList<ChecklistCompleteItem> = ArrayList<ChecklistCompleteItem>();
                        // start_date 당일부터 end_Date 당일까지의 날짜 수 계산
                        val dif : Int = when {
                            it.end_date != it.start_date -> ((it.end_date.time - it.start_date.time) / (24 * 60 * 60 * 1000 )).toInt()
                            else -> 0;
                        }
                        // 날짜를 더하기 편하게 하기 위한 calendar 객체 생성
                        val day = Calendar.getInstance();
                        day.time = it.start_date;
                        for( i in 0 .. dif){
                            day.add(Calendar.DATE, i)
                            val time : Date = day.time
                            item.add(ChecklistCompleteItem(time, it.complete[i]));
                            day.add(Calendar.DATE, -i)
                        }

                        // 클릭 리스너
                        val complete_click_lisener = { pos : Int, complete_val : Boolean ->
                            val update_complete_val : ArrayList<Boolean> = ArrayList(it.complete);
                            update_complete_val[pos] = !complete_val;
                            vm.update_one_list(id, update_complete_val.toList());
                        }

                        val adapter = CheckListDetailAdapter(item, complete_click_lisener);
                        recyclerView.adapter = adapter;
                    }
                }
                1 -> {
                    vm.find_checklistrepeat_detail(id).observe(viewLifecycleOwner) {
                        // 이름
                        binding.checklistDetailTitleText.setText(it.checklistrepeat_name);

                        // 속성
                        binding.checklistDetailAttrText.setText(it.attr_name);
                        binding.checklistDetailAttrText.setTextColor(Color.BLACK);
                        binding.checklistDetailAttrText.setBackgroundResource(R.drawable.checklist_attr_border);
                        val color_val = Color.parseColor(it.attr_color);
                        binding.checklistDetailAttrText.backgroundTintList = ColorStateList.valueOf(color_val);

                        // 내용
                        binding.checklistDetailContentText.setText(it.content);


                        // item 생성하기
                        // 갯수는 올해 까지만 보여지게 하기
                        val item : ArrayList<ChecklistCompleteItem> = ArrayList<ChecklistCompleteItem>();
                        // 날짜를 더하기 편하게 하기 위한 calendar 객체 생성
                        val day = Calendar.getInstance();
                        day.time = it.start_date;
                        // 올해 마지막 날짜 변수
                        val last_day : Date = SimpleDateFormat("yyyy-MM-dd").parse(day.get(Calendar.YEAR).toString() + "-12-31")!!
                        // 올해 말까지 남은 갯수만큼 진행
                        var dif : Int = when {
                            last_day != it.start_date -> ((last_day.time - it.start_date.time) / (24 * 60 * 60 * 1000 )).toInt()
                            else -> 0;
                        }
                        if ( dif > 1000 ) dif = 1000;

                        // day를 start_date로 맞추기
                        day.time = it.start_date;

                        //유형 설정 및 유형별 아이템 생성
                        // 0 매일 1주간 2정기
                        when(it.repeat_type){
                            0 -> {
                                binding.checklistDetailTypeText.setText("매일")
                                for(i in 0 .. dif){
                                    day.add(Calendar.DATE, i)
                                    val time = day.time
                                    item.add(ChecklistCompleteItem(time, it.complete[i]))
                                    day.add(Calendar.DATE, -i);
                                }
                            }
                            1 -> {
                                binding.checklistDetailTypeText.setText("주간")
                                // 요일 정보를 담고 있을 변수
                                // 0~6 월 ~ 일
                                val selected_day_of_week : ArrayList<Boolean> = ArrayList()
                                var week_val = it.week_val
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

                                // item 채우기
                                var counter = 0;
                                for(i in 0 .. dif){
                                    day.add(Calendar.DATE, i)
                                    // day 의 요일이 주간의 요일에 맞는 요일이면 item에 추가
                                    // 1 ~ 7 일 월 화 수 목 금 토
                                    val today_of_week = day.get(Calendar.DAY_OF_WEEK)
                                    var that_day = false;
                                    when(today_of_week){
                                        1 -> that_day = selected_day_of_week[6]
                                        2 -> that_day = selected_day_of_week[0]
                                        3 -> that_day = selected_day_of_week[1]
                                        4 -> that_day = selected_day_of_week[2]
                                        5 -> that_day = selected_day_of_week[3]
                                        6 -> that_day = selected_day_of_week[4]
                                        7 -> that_day = selected_day_of_week[5]
                                    }
                                    if ( that_day ){
                                        val time = day.time
                                        item.add(ChecklistCompleteItem(time, it.complete[counter++]))
                                    }
                                    day.add(Calendar.DATE, -i);
                                }
                            }
                            2 -> {
                                binding.checklistDetailTypeText.setText("정기")

                                val repeat_cycle_type = it.repeat_cycle / 100
                                val repeat_cycle_val = it.repeat_cycle % 100

                                when(repeat_cycle_type){
                                    1 -> { // 매 n일 마다
                                        var counter = 0;
                                        for(i in 0 .. dif step(repeat_cycle_val)){
                                            day.add(Calendar.DATE, i)
                                            val time = day.time
                                            item.add(ChecklistCompleteItem(time, it.complete[counter++]))
                                            day.add(Calendar.DATE, -i);
                                        }
                                    }
                                    2 -> { // 매월 n일 마다
                                        var counter = 0;
                                        // 올해 연도 변수화해두기
                                        val this_day = Calendar.getInstance()
                                        val day_year = this_day.get(Calendar.YEAR);

                                        // day를 n일로 맞추기
                                        // repeat_cycle_val이 start_date보다 작으면 다음 달부터 진행해야함
                                        val temp_day = day.get(Calendar.DATE)
                                        //Log.d("checker_test", temp_day.toString())
                                        if ( temp_day > repeat_cycle_val) {
                                            day.add(Calendar.MONTH, 1)
                                        }
                                        day.set(Calendar.DATE, repeat_cycle_val)
                                        while(true){
                                            if ( day.get(Calendar.YEAR) > day_year ){
                                                break;
                                            }
                                            val time = day.time
                                            item.add(ChecklistCompleteItem(time, it.complete[counter]))
                                            day.add(Calendar.MONTH, 1)
                                            counter++
                                        }
                                    }
                                }

                            }
                        }

                        // 클릭 리스너
                        val complete_click_lisener = { pos : Int, complete_val : Boolean ->
                            val update_complete_val : ArrayList<Boolean> = ArrayList(it.complete);
                            update_complete_val[pos] = !complete_val;
                            vm.update_one_checklistrepeat(id, update_complete_val.toList());
                        }

                        val adapter = CheckListDetailAdapter(item, complete_click_lisener);
                        recyclerView.adapter = adapter;
                    }
                }
            }

        }

        // 뒤로가기 버튼
        binding.checklistDetailPreBtn.setOnClickListener {
            navController.popBackStack();
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment checklist_detail_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            checklist_detail_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}