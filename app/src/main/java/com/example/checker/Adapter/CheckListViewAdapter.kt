package com.example.checker.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.DB.CheckList
import com.example.checker.DB.CheckListRepeatViewer
import com.example.checker.DB.CheckListViewer
import com.example.checker.DB.ListViewer
import com.example.checker.R
import org.w3c.dom.Text

class CheckListViewAdapter(private val click_listener:( Int, Int)-> Unit,
                           private val more_click_listener:(Int, Int) -> Unit,
                           private val delete_click_listener:(Int, Int) -> Unit
    ) : RecyclerView.Adapter<CheckListViewAdapter.ChecklistViewHolder>() {

    private var data = emptyList<ListViewer>();

    inner class ChecklistViewHolder(item : View) : RecyclerView.ViewHolder(item) {
        // 표기할 정보를 전부 받아와야 함
        val list_view = item.findViewById<LinearLayout>(R.id.list_item);
        val attr_name = item.findViewById<TextView>(R.id.attr_name);
        val checklist_name = item.findViewById<TextView>(R.id.checklist_name);
        val delete_btn = item.findViewById<ImageView>(R.id.delete_btn);
        val more_btn = item.findViewById<LinearLayout>(R.id.checklist_details_icon);
        val type_name = item.findViewById<TextView>(R.id.checklist_type)

        private var fixed = false;

        fun change_fixed(change_val : Boolean){
            this.fixed = change_val;
        }

        fun get_fixed() : Boolean {
            return fixed;
        }
    }

    // viewholder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.checklist_shower, parent, false);
        return ChecklistViewHolder(view);
    }

    override fun getItemCount(): Int {
        return data.count();
    }

    // 어떻게 보여지게 될지 작성
    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        // normal_list 가 있을 경우
        if ( data[position].normal_list != null ){
            // 목표 제목
            val text = when {
                data[position].normal_list!!.checklist_name.length > 15 -> {
                    data[position].normal_list!!.checklist_name.substring(0, 15) + "…";
                }
                else -> {
                    data[position].normal_list!!.checklist_name
                }
            }
            holder.checklist_name.setText(text);

            // 속성
            holder.attr_name.setText(data[position].normal_list!!.attr_name)
            val color_val = Color.parseColor(data[position].normal_list!!.attr_color);
            holder.attr_name.backgroundTintList = ColorStateList.valueOf(color_val);

            // 형식 설정
            holder.type_name.setText("기간")

            // 더보기 버튼 추가
            holder.more_btn.setOnClickListener { more_click_listener(data[position].normal_list!!.id, 0) }

            // 이름 클릭으로 수정하기
            holder.checklist_name.setOnClickListener { click_listener(data[position].normal_list!!.id, 0); }

            // 삭제 버튼 설정
            holder.delete_btn.setOnClickListener { delete_click_listener(data[position].normal_list!!.id, 0) }

        }
        // repeat list가 있을 경우
        else {
            // 이름 설정
            val text = when {
                data[position].repeat_list!!.checklistrepeat_name.length > 15 -> {
                    data[position].repeat_list!!.checklistrepeat_name.substring(0, 15) + "…";
                }
                else -> {
                    data[position].repeat_list!!.checklistrepeat_name
                }
            }
            holder.checklist_name.setText(text);

            // 속성 설정
            holder.attr_name.setText(data[position].repeat_list!!.attr_name);
            val color_val = Color.parseColor(data[position].repeat_list!!.attr_color);
            holder.attr_name.backgroundTintList = ColorStateList.valueOf(color_val);

            // 형식 설정
            when(data[position].repeat_list!!.repeat_type) {
                0 -> {
                    holder.type_name.setText("매일")
                }
                1 -> {
                    holder.type_name.setText("주간")
                }
                2 -> {
                    holder.type_name.setText("정기")
                }
            }
            // 더보기 버튼 추가
            holder.more_btn.setOnClickListener { more_click_listener(data[position].repeat_list!!.id, 1) }

            // 이름 클릭으로 수정하기
            holder.checklist_name.setOnClickListener { click_listener(data[position].repeat_list!!.id, 1); }

            // 삭제 버튼 설정
            holder.delete_btn.setOnClickListener { delete_click_listener(data[position].repeat_list!!.id, 1) }
        }
        // 공용
        holder.checklist_name.setBackgroundResource(R.drawable.checklist_cell_border);
        holder.attr_name.setTextColor(Color.BLACK);
        holder.attr_name.setBackgroundResource(R.drawable.checklist_attr_border);
        holder.type_name.setBackgroundResource(R.drawable.checklist_cell_border)
    }

    public fun set_List(item : List<ListViewer>){
        this.data = item;
        notifyDataSetChanged();
    }

    public fun change_List_complete(){
        notifyDataSetChanged();
    }
    public fun remove_list(pos : Int){
        notifyItemRemoved(pos);
    }

}