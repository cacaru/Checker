package com.example.checker

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.checker.Adapter.DayListDetailAdapter
import com.example.checker.DB.DayListViewer
import com.example.checker.DB.ListViewer
import com.example.checker.DB.VM
import com.example.checker.Item.SimpleListItem
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
 * Use the [daylist_details_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class daylist_details_fragment : Fragment(){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var navController: NavController
    private lateinit var binding: DaylistDetailsFragmentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var vm : VM

    private lateinit var callback: OnBackPressedCallback

    private var today_day = 0
    private var today_str = ""
    private lateinit var item_list : ArrayList<SimpleListItem>
    private val TAG = "daylist_test"

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
        binding = DaylistDetailsFragmentBinding.inflate(layoutInflater);
        vm = ViewModelProvider(this).get(VM::class.java)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // navigation 설정
        navController = Navigation.findNavController(view);
        recyclerView = binding.daylistDetailsRecyclerview;
        manager = LinearLayoutManager(view.context)



        setFragmentResultListener("DayListDetails"){ key , bundle ->
            today_day = bundle.getInt("Day");
            today_str = bundle.getString("Today").toString();
            item_list = bundle.getSerializable("SimpleList") as ArrayList<SimpleListItem>;

            // 날짜를 기반으로 표시할 데이터 받아오기
            val today : Date = SimpleDateFormat("yyyy-MM-dd").parse(today_str);

            val complete_click_listener = { id : Int, complete_list : List<Boolean>, pos : Int , type : Int->
                // pos를 넘겨받는 것으로 수정
                val update_complete_list : ArrayList<Boolean> = ArrayList<Boolean>(complete_list);
                update_complete_list[pos] = !complete_list[pos];
                // complete 업데이트
                when(type) {
                    0 -> {
                        vm.update_one_list(id, update_complete_list.toList());
                    }
                    1 -> {
                        vm.update_one_checklistrepeat(id, update_complete_list.toList())
                    }
                }
            }

            val adapter = DayListDetailAdapter(today, complete_click_listener);
            recyclerView.layoutManager = manager;
            recyclerView.adapter = adapter;

            vm.get_daylistdata(today).observe(viewLifecycleOwner, Observer { data ->
                val list_data = ArrayList<DayListViewer>()
                var size = data.size
                for(i in 0 until size){
                    list_data.add(DayListViewer(data[i], null))
                }
                vm.get_daylist_repeat_data(today).observe(viewLifecycleOwner) {
                    size = it.size;
                    // 무작정 넣지 말고 오늘 보여질 데이터라면 넣기
                    for(i in 0 until size){
                        var is_add_data = false;
                        // 타입별 분류
                        when(it[i].repeat_type){
                            0 -> { // 매일
                                // 검색되는 데이터가 today 이전부터 반복되고 있던 값들만을 불러오므로 매일은 항상 추가
                                is_add_data = true
                            }
                            1 -> { // 주간
                                // 오늘이 해당하는 주간 요일인지 검사해야함
                                // 선택한 요일 확인
                                // 0~6 월 ~ 일
                                val selected_day_of_week : ArrayList<Boolean> = ArrayList()
                                var week_val = it[i].week_val
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

                                // 오늘 요일의 값이 true 라면 데이터 추가
                                // 오늘 요일값
                                val calendar = Calendar.getInstance()
                                calendar.time = today
                                // 일 ~ 토 1 ~ 7
                                val today_of_week : Int = calendar.get(Calendar.DAY_OF_WEEK)
                                // 내 데이터 :: 0 ~6 월 ~일 이므로 -2 값이 -1 이 되면 6으로만 바꿔주면 대입가능
                                val search_day_of_weej_val : Int = when {
                                    today_of_week >= 2 -> today_of_week - 2
                                    else -> 6
                                }

                                if ( selected_day_of_week[search_day_of_weej_val] ){
                                    is_add_data = true
                                }
                            }
                            2 -> { // 정기
                                val repeat_cycle_type = it[i].repeat_cycle / 100
                                val repeat_cycle_val = it[i].repeat_cycle % 100
                                val date = Calendar.getInstance()
                                date.time = it[i].start_date
                                when(repeat_cycle_type) {
                                    1 -> { // 매 n일 == 시작일부터 n일째 되는 날임을 검사
                                        while( true ){
                                            // 증가된 날이 오늘인지 검사
                                            // 오늘이면 데이터 추가하고 종료
                                            if( date.time == today ){
                                                is_add_data = true
                                                break;
                                            }
                                            // 계산된 date가 오늘을 지나갔을 경우 종료
                                            else if ( date.time > today ) break;

                                            // date를 n만큼 증가
                                            date.add(Calendar.DATE, repeat_cycle_val)
                                        }
                                    }
                                    2 -> {  // 매월 n 일 == 오늘의 날짜가 n 인지 검사
                                        date.time = today;
                                        Log.d("checker_test", repeat_cycle_val.toString())
                                        Log.d("checker_test", date.time.toString())
                                        if ( date.get(Calendar.DATE) == repeat_cycle_val ){
                                            is_add_data = true
                                        }
                                    }
                                }

                            }
                        }

                        // update할 때 기존에 데이터가 있으면 해당 데이터를 제거하고 다시 등록해야함
                        // 기존의 모든 데이터와 비교한다
                        if ( is_add_data ) {
                            val compare_size = list_data.size
                            var update_list = false;
                            for(j in 0 until compare_size){
                                if ( list_data[j].repeat_list != null ){
                                    val already_has_repeat_id = list_data[j].repeat_list!!.id
                                    if ( it[i].id == already_has_repeat_id ){
                                        list_data.removeAt(j)
                                        list_data.add(DayListViewer(null, it[i]))
                                        update_list = true;
                                        break;
                                    }
                                }
                            }

                            if ( !update_list ){
                                list_data.add(DayListViewer(null, it[i]))
                            }
                        }

                    }

                    adapter.set_List(list_data);
                }

            });
        };

        // 뒤로 가기
        binding.daylistDetailsPreBtn.setOnClickListener {
            val bundle = Bundle();
            bundle.putInt("Day", today_day);
            bundle.putString("Today", today_str);
            bundle.putSerializable("SimpleList", item_list);
            setFragmentResult("DayList", bundle);
            navController.popBackStack();
        }

        // 새항목 생성하러 가기
        binding.daylistListAddBtn.setOnClickListener {
            val bundle = Bundle();
            bundle.putString("Dday", today_str);
            setFragmentResult("DayListAdd", bundle);
            navController.navigate(R.id.action_daylist_details_fragment_to_checklist_add_fragment)
        }
    }

    // 뒤로가기 버튼 이벤트 override
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val bundle = Bundle();
                bundle.putInt("Day", today_day);
                bundle.putString("Today", today_str);
                bundle.putSerializable("SimpleList", item_list);
                setFragmentResult("DayList", bundle);
                navController.popBackStack();
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment daylist_details_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            daylist_details_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}