package com.example.checker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.Adapter.CheckListViewAdapter
import com.example.checker.Adapter.SwipeHelper
import com.example.checker.DB.CheckListViewer
import com.example.checker.DB.ListViewer
import com.example.checker.DB.VM
import com.example.checker.databinding.ChecklistAllFragmentBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [checklist_all_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class checklist_all_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var navController: NavController
    lateinit var binding: ChecklistAllFragmentBinding;
    lateinit var recyclerView: RecyclerView;
    lateinit var manager: RecyclerView.LayoutManager;
    lateinit var vm: VM
    lateinit var adapter : CheckListViewAdapter;

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
        binding = ChecklistAllFragmentBinding.inflate(layoutInflater);
        // 수정하기 위해 이름을 클릭하기
        val click_listener = { id : Int, type : Int ->
            when(type) {
                // 기간 리스트
                0 -> {
                    vm.find_checklist(id).observe(viewLifecycleOwner, Observer { data ->
                        val bundle = Bundle();
                        //Log.d("checker_test", data.toString());
                        bundle.putInt("checklist_type", 0)
                        bundle.putSerializable("checklist", data)
                        setFragmentResult("checklist", bundle)
                        //Log.d("checker", data.start_date.toString());
                        navController.navigate(R.id.action_checklist_all_fragment_to_checklist_modify_fragment)
                    })
                }
                // 정기 반복 리스트
                1 -> {
                    vm.find_checklistrepeat(id).observe(viewLifecycleOwner) {
                        val bundle = Bundle();
                        //Log.d("checker_test", data.toString());
                        bundle.putInt("checklist_type", 1)
                        bundle.putSerializable("checklist", it)
                        setFragmentResult("checklist", bundle)
                        //Log.d("checker", data.start_date.toString())
                        navController.navigate(R.id.action_checklist_all_fragment_to_checklist_modify_fragment)
                    }
                }
            }

        }

        // 항목 자세히 보기 화면으로 이동
        val more_click_listener = { id : Int , type : Int ->
            val bundle = Bundle();
            bundle.putInt("checklist_id", id);
            bundle.putInt("checklist_type", type)
            setFragmentResult("checklist_detail", bundle);
            navController.navigate(R.id.action_checklist_all_fragment_to_checklist_detail_fragment)
        }


        // 삭제 버튼 이벤트
        val delete_click_listener = { id : Int , type : Int->
            when(type) {
                0 -> {
                    Log.d("checker_list_test", "delete " + id);
                    vm.delete_one_list(id);

                }
                1 -> {
                    Log.d("checker_list_test", "delete " + id);
                    vm.delete_one_checklistrepeat(id)
                }
            }
            adapter.change_List_complete();
            // 다시 그리기
            recyclerView.removeAllViews();
            recyclerView.layoutManager = manager
            recyclerView.adapter = adapter;
        }

        adapter = CheckListViewAdapter(click_listener, more_click_listener , delete_click_listener);

        vm = ViewModelProvider(this).get(VM::class.java);
        vm.checklist_view_data.observe(viewLifecycleOwner, Observer { data ->
            val list_data = ArrayList<ListViewer>()
            var size = data.size
            for(i in 0 until size){
                list_data.add(ListViewer(data[i], null))
            }

            vm.checklistrepeat_view_data.observe(viewLifecycleOwner) {
                size = it.size;
                for(i in 0 until size){
                    if ( !list_data.contains(ListViewer(null, it[i])))
                        list_data.add(ListViewer(null, it[i]))
                }

                adapter.set_List(list_data);
            }
        })


        return binding.root;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.checklist_all_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recycler view 생성
        manager = LinearLayoutManager(view.context);
        recyclerView = binding.checklistRecyclerView;
        recyclerView.adapter = adapter;
        recyclerView.layoutManager = manager;

        // recyclerview 스와이프 액션 추가
        val swipe_helper = SwipeHelper();
        val itemTouchHelper = ItemTouchHelper(swipe_helper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // 달력 페이지로 이동
        navController = Navigation.findNavController(view)
        binding.checklistBackCalendarBtn.setOnClickListener {
            navController.popBackStack()
        }
        binding.checklistAddBtn.setOnClickListener {
            navController.navigate(R.id.action_checklist_all_fragment_to_checklist_add_fragment)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment checklist_all_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            checklist_all_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}