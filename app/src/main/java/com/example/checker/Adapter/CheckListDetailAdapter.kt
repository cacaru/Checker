package com.example.checker.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.Item.ChecklistCompleteItem
import com.example.checker.R
import java.text.SimpleDateFormat

class CheckListDetailAdapter(
    private val list : ArrayList<ChecklistCompleteItem>,
    private val complete_click_listener : (Int, Boolean) -> Unit
) : RecyclerView.Adapter<CheckListDetailAdapter.CheckListDetailViewHolder>() {

    inner class CheckListDetailViewHolder(item : View) : RecyclerView.ViewHolder(item){
        val complete_val = item.findViewById<ImageView>(R.id.checklist_day_complete_val)
        val date = item.findViewById<TextView>(R.id.checklist_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListDetailViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.checklist_detail_shower, parent, false);
        return CheckListDetailViewHolder(view);
    }

    override fun getItemCount(): Int {
        return list.count();
    }

    override fun onBindViewHolder(holder: CheckListDetailViewHolder, position: Int) {
        val show_text = SimpleDateFormat("yyyy-MM-dd").format(list[position].date);
        // 날짜 보여주기
        holder.date.setText(show_text);

        // 완료 버튼 이미지 보이기
        when {
            list[position].complete ->{
                holder.complete_val.setImageResource(R.drawable.checker_complete)
            }
            else ->{
                holder.complete_val.setImageResource(R.drawable.checker_enable)
            }
        }
        
        // 완료 버튼 이벤트
        holder.complete_val.setOnClickListener { complete_click_listener(position, list[position].complete) }
    }
}