package com.example.checker

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
import com.example.checker.Adapter.AttributeAdapter
import com.example.checker.Adapter.DayListDataAdapter
import com.example.checker.DB.Memo
import com.example.checker.DB.VM
import com.example.checker.Item.SimpleListItem
import com.example.checker.databinding.AttributeListFragmentBinding
import com.example.checker.databinding.DaylistSummaryFragmentBinding
import java.text.SimpleDateFormat
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [daylist_summary_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class daylist_summary_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var navController: NavController
    lateinit var binding: DaylistSummaryFragmentBinding
    lateinit var recyclerView: RecyclerView
    lateinit var manager: RecyclerView.LayoutManager
    lateinit var adapter: DayListDataAdapter

    private var today_day = 0
    private lateinit var today_str : String
    private lateinit var item_list : ArrayList<SimpleListItem>
    private val TAG = "daylist_test"

    private lateinit var vm : VM
    private var todays_content : String = ""
    private lateinit var today_date : Date
    private var has_content : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        vm = ViewModelProvider(this).get(VM::class.java);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DaylistSummaryFragmentBinding.inflate(layoutInflater);
        return binding.root;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.daylist_summary_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // navigation 설정
        navController = Navigation.findNavController(view);
        recyclerView = binding.daylistDailyRecyclerview

        setFragmentResultListener("DayList"){ key , bundle ->
            today_day = bundle.getInt("Day");
            today_str = bundle.getString("Today").toString();
            today_date = SimpleDateFormat("yyyy-MM-dd").parse(today_str);
            item_list = bundle.getSerializable("SimpleList") as ArrayList<SimpleListItem>;
            // recyclerview 설정
            // adapter 설정
            adapter = DayListDataAdapter(item_list);
            manager = LinearLayoutManager(view.context)
            recyclerView.layoutManager = manager
            recyclerView.adapter = adapter
            // 제목 수정하기
            binding.daylistTitle.setText(today_day.toString() + "일의 할 일");

            //오늘의 메모
            vm.find_day_memo(today_date).observe(viewLifecycleOwner) {
                if ( it != null ){
                    has_content = true
                    todays_content = it.content;
                    binding.daylistContentText.setText(todays_content)
                }
                else {
                    has_content = false
                    //Log.d("checker_daylist", "no data in here");
                }
            }
        };

        // 뒤로가기
        binding.daylistPreBtn.setOnClickListener {
            navController.popBackStack()
        }

        // 디테일 보러가기
        binding.listMoreBtn.setOnClickListener {
            val bundle = Bundle();
            bundle.putInt("Day", today_day)
            bundle.putString("Today", today_str)
            bundle.putSerializable("SimpleList", item_list);
            setFragmentResult("DayListDetails", bundle);
            navController.navigate(R.id.action_daylist_summary_fragment_to_daylist_details_fragment);
        }

        // 오늘의 기록 저장하기
        binding.dayContentSaveBtn.setOnClickListener {
            val text = binding.daylistContentText.text.toString();
            Log.d("checker_daylist", text);
            Log.d("checker_daylist", today_date.toString());
            val memo_data : Memo = Memo(today_date, text);
            when {
                has_content -> {
                    vm.update_memo(memo_data)
                    Log.d("checker_daylist", "memo update")
                }
                else -> {
                    vm.insert_memo(memo_data)
                    Log.d("checker_daylist", "memo insert")
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
         * @return A new instance of fragment daylist_summary_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            daylist_summary_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}