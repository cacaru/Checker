package com.example.checker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.Adapter.AttributeAdapter
import com.example.checker.DB.VM
import com.example.checker.databinding.AttributeListFragmentBinding


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [attribute_list_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class attribute_list_fragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var navController: NavController
    lateinit var binding: AttributeListFragmentBinding
    lateinit var recyclerView: RecyclerView;
    lateinit var manager: RecyclerView.LayoutManager;
    lateinit var vm: VM
    lateinit var adapter : AttributeAdapter;

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
        //binding
        binding = AttributeListFragmentBinding.inflate(layoutInflater);

        adapter = AttributeAdapter { item , attr ->
            val bundle = Bundle();
            bundle.putSerializable("attr", attr);
            setFragmentResult("attr", bundle);
            //Log.d("checker", attr.name);
            navController.navigate(R.id.action_attribute_list_fragment_to_attribute_modify_fragment);
        }

        vm = ViewModelProvider(this).get(VM::class.java)
        vm.attr_all_data.observe(viewLifecycleOwner, Observer {
                data -> adapter.setItem(data);
        })
        return binding.root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.checklist_add_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        manager = GridLayoutManager(view.context, 2, GridLayoutManager.VERTICAL, false);

        recyclerView = binding.attrListRecyclerview;

        recyclerView.adapter = adapter;
        recyclerView.layoutManager = manager;

        navController = Navigation.findNavController(view);

        binding.attrAddBtn.setOnClickListener {
            navController.navigate(R.id.action_attribute_list_fragment_to_attribute_add_fragment);
        }

        binding.attributeListpagePreBtn.setOnClickListener {
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
         * @return A new instance of fragment attribute_list_fragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            attribute_list_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}