package com.example.checker

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.checker.DB.AttributeList
import com.example.checker.DB.VM
import com.example.checker.databinding.AttributeAddFragmentBinding
import com.larswerkman.holocolorpicker.ColorPicker
import com.larswerkman.holocolorpicker.SaturationBar
import com.larswerkman.holocolorpicker.ValueBar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Attribute_Create_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Attribute_Create_Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: AttributeAddFragmentBinding
    lateinit var navController: NavController
    lateinit var colorPicker : ColorPicker
    lateinit var vm : VM

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
        binding = AttributeAddFragmentBinding.inflate(layoutInflater);
        colorPicker = binding.colorPicker;
        vm = ViewModelProvider(this).get(VM::class.java);
        return binding.root;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.attribute_add_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view);

        binding.attributeAddpagePreBtn.setOnClickListener {
            navController.popBackStack();
        }

        init_Picker();

        // hex code 값으로 색 변경
        binding.hexColorSetBtn.setOnClickListener{
            var hex_code = binding.hexColorText.text.toString();
            Log.d("checker_hex_color_val", hex_code);
            if ( hex_code.length == 6 ){
                var red = Integer.parseInt(hex_code.substring(0,2),16);
                var green = Integer.parseInt(hex_code.substring(2,4),16);
                var blue = Integer.parseInt(hex_code.substring(4,6),16);

                var change_color = Color.rgb(red, green, blue);
                colorPicker.setNewCenterColor(change_color);
                colorPicker.color = change_color;

                // 예시 설정
                binding.attrEx.backgroundTintList = ColorStateList.valueOf(change_color)
            }
            else{
                val toast = Toast.makeText(view.context, "HEX값을 정확히 입력해주세요", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 400)
                toast.show();
            }
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.attrEx.text = s;
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

        // 예시 실시간 변경
        binding.attrNameText.addTextChangedListener(textWatcher);

        // 추가하기
        binding.attrAddBtn.setOnClickListener {
            val name : String = binding.attrNameText.text.toString();
            val color : String = "#" + binding.hexColorText.text.toString();
            var attr : AttributeList = AttributeList(0, name, color);
            Log.d("checker_attr_add", attr.name);
            Log.d("chekcer_attr_add", attr.color);
            vm.insert_attr(attr);
            // 이전 화면으로 돌아가기
            navController.popBackStack();
        }
    }

    private fun init_Picker(){
        val saturation_bar : SaturationBar = binding.colorSaturationBar;
        val value_bar : ValueBar = binding.colorValueBar;

        colorPicker.addSaturationBar(saturation_bar);
        colorPicker.addValueBar(value_bar);

        var red : Int = Color.red(colorPicker.color);
        var blue : Int = Color.blue(colorPicker.color);
        var green : Int = Color.green(colorPicker.color);

        var text = Integer.toHexString(Color.rgb(red, green, blue)).substring(2);
/*
        Log.d("check_color_hex", red.toString());
        Log.d("check_color_hex", green.toString());
        Log.d("check_color_hex", blue.toString());
        Log.d("check_color_hex", text);
*/
        binding.hexColorText.setText(text);
        binding.attrEx.backgroundTintList = ColorStateList.valueOf(colorPicker.color);
        // 색 변경 적용
        colorPicker.setOnColorChangedListener {
            var hex_color_code = Integer.toHexString(it);
            binding.hexColorText.setText(hex_color_code.substring(2));

            // 예시 설정
            binding.attrEx.backgroundTintList = ColorStateList.valueOf(it)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment attribute_add_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Attribute_Create_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}