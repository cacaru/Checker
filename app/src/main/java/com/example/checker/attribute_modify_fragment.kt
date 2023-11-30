package com.example.checker

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.checker.DB.AttributeList
import com.example.checker.DB.VM
import com.example.checker.databinding.AttributeModifyFragmentBinding
import com.larswerkman.holocolorpicker.ColorPicker

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [attribute_modify_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class attribute_modify_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: AttributeModifyFragmentBinding
    lateinit var navController: NavController
    lateinit var colorPicker : ColorPicker
    lateinit var vm : VM

    lateinit var attr : AttributeList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        setFragmentResultListener("attr") {key, bundle ->
            attr = bundle.getSerializable("attr") as AttributeList;
//            Log.d("checker_attr_move_2nd", attr.name);
            attr_init();
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AttributeModifyFragmentBinding.inflate(layoutInflater);
        colorPicker = binding.colorPicker;

        vm = ViewModelProvider(this).get(VM::class.java);

        return binding.root;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.attribute_add_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view);

        color_init();

        // 이전화면으로 돌아가기
        binding.attributeModifypagePreBtn.setOnClickListener {
            navController.popBackStack();
        }

        // 이름 변경 감지
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.attrEx.text = s;
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

        // 예시 설정
        binding.attrNameText.addTextChangedListener(textWatcher);

        // 기존 hex code 적용 버튼
        binding.hexColorResetBtn.setOnClickListener{
            var red = Integer.parseInt(attr.color.substring(1,3),16);
            var green = Integer.parseInt(attr.color.substring(3,5),16);
            var blue = Integer.parseInt(attr.color.substring(5,7),16);
            val before_set_color = Color.rgb(red, green, blue);
            colorPicker.color = before_set_color;
        }

        // 임의 HEX값 변경 버튼
        binding.hexColorSetBtn.setOnClickListener{
            val hex_text : String = binding.hexColorText.text.toString();
            if( hex_text.length == 6 ){
                var red = Integer.parseInt(hex_text.substring(0,2), 16);
                var green = Integer.parseInt(hex_text.substring(2,4), 16);
                var blue = Integer.parseInt(hex_text.substring(4,6), 16);
                val set_color = Color.rgb(red, green, blue);
                colorPicker.color = set_color;
            }
            else{
                //Log.d("checker_color_set_error", "modify > hex_text 길이 부족");
                val toast = Toast.makeText(view.context, "HEX값을 정확히 입력해주세요", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 400)
                toast.show();
            }
        }

        // 값 수정
        binding.modifyAttrBtn.setOnClickListener {
            val name : String = binding.attrNameText.text.toString();
            val color : String = "#" + binding.hexColorText.text.toString();
            //Log.d("checker_modify", name);
            //Log.d("checker_modify", color);
            var attr : AttributeList = AttributeList(attr.attr_id, name, color);
            vm.update_attr(attr);
            // 화면을 이전 화면으로 이동
            navController.popBackStack();
        }
    }

    private fun color_init(){
        // colorpicker init
        val saturation_bar = binding.colorSaturationBar;
        val value_bar = binding.colorValueBar;

        colorPicker.addSaturationBar(saturation_bar);
        colorPicker.addValueBar(value_bar);

        colorPicker.setOnColorChangedListener {
            var hex_code_text = Integer.toHexString(it);
            binding.hexColorText.setText(hex_code_text.substring(2))

            binding.attrEx.backgroundTintList = ColorStateList.valueOf(it)
        }
    }

    private fun attr_init(){
        binding.attrNameText.setText(attr.name);
        //Log.d("checker_modify", attr.color);
        colorPicker.color = Color.parseColor(attr.color);
        colorPicker.oldCenterColor = Color.parseColor(attr.color);

        binding.hexColorBeforeText.setText(attr.color.substring(1));
        binding.hexColorText.setText(attr.color.substring(1));

        // 기존 보기 설정
        binding.attrBefore.text = attr.name;
        var red = Integer.parseInt(attr.color.substring(1,3),16);
        var green = Integer.parseInt(attr.color.substring(3,5),16);
        var blue = Integer.parseInt(attr.color.substring(5,7),16);
        binding.attrBefore.backgroundTintList = ColorStateList.valueOf(Color.rgb(red, green, blue));

        // 예시 기본 설정
        binding.attrEx.text = attr.name;
        binding.attrEx.backgroundTintList = ColorStateList.valueOf(Color.rgb(red, green, blue));
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment attribute_modify_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            attribute_modify_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}