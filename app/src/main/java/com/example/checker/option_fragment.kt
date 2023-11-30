package com.example.checker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.DB.VM
import com.example.checker.databinding.ChecklistAddFragmentBinding
import com.example.checker.databinding.DaylistDetailsFragmentBinding
import com.example.checker.databinding.OptionFragmentBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [option_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class option_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var navController: NavController
    private lateinit var binding: OptionFragmentBinding

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
        binding = OptionFragmentBinding.inflate(layoutInflater);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view);

        binding.settingPreBtn.setOnClickListener {
            navController.popBackStack()
        }

        // 테마 클릭 이벤트
        binding.theme0.setOnClickListener { normal_theme_click_event(0) }
        binding.theme1.setOnClickListener { normal_theme_click_event(1) }
        binding.theme2.setOnClickListener { normal_theme_click_event(2) }
        binding.theme3.setOnClickListener { normal_theme_click_event(3) }
        binding.theme4.setOnClickListener { normal_theme_click_event(4) }
        binding.theme5.setOnClickListener { normal_theme_click_event(5) }
        binding.theme6.setOnClickListener { normal_theme_click_event(6) }
        binding.theme7.setOnClickListener { normal_theme_click_event(7) }
        binding.theme8.setOnClickListener { normal_theme_click_event(8) }
        binding.theme9.setOnClickListener { normal_theme_click_event(9) }

    }

    private fun normal_theme_click_event(id : Int){
        val sf = this.requireActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor = sf.edit()
        editor.putInt("theme", id)
        Log.d("checker_test", id.toString())
        editor.apply()

        restart_app()
    }

    fun restart_app() {
        val intent = Intent(this.requireActivity(), MainActivity::class.java)
        startActivity(intent)
        this.requireActivity().finish()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment option_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            option_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}