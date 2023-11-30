package com.example.checker.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.Item.CalendarItem
import com.example.checker.Item.SimpleListItem
import com.example.checker.R

class CalendarAdapter(private val itemList : ArrayList<CalendarItem>,
                      private val day_click_listener : (Int, ArrayList<SimpleListItem>) -> Unit,
                      context : Context
    ) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val context = context
    private lateinit var view : View

    inner class CalendarViewHolder(item : View) : RecyclerView.ViewHolder(item){
        val calendar_main_day_text = item.findViewById<TextView>(R.id.calendar_main_day_text);
        val calendar_content_img = item.findViewById<ImageView>(R.id.has_content_day);
        val calendar_cell = item.findViewById<ConstraintLayout>(R.id.calendar_cell);
        val calendar_recyclerview = item.findViewById<RecyclerView>(R.id.daylist_recyclerview);
        val bg_img = item.findViewById<ImageView>(R.id.bg_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_day, parent, false)
        return CalendarViewHolder(view);
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        if ( itemList[position].day.length > 0 ) {
            // 클릭 이벤트 붙이기
            holder.bg_img.setOnClickListener { day_click_listener(Integer.parseInt(itemList[position].day), itemList[position].has_list) }
            holder.calendar_main_day_text.text = itemList[position].day;

            if (itemList[position].day_of_week == 6 ){
                holder.calendar_main_day_text.setTextColor(Color.parseColor("#0070C0"));
            }
            if (itemList[position].day_of_week == 7 ){
                holder.calendar_main_day_text.setTextColor(Color.parseColor("#FF0000"));
            }

            // 체크리스트가 있으면 이미지 표시하기
            // 새로운 recyclerview 생성
            val manager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, true);
            holder.calendar_recyclerview.layoutManager = manager
            holder.calendar_recyclerview.adapter = CalendarDataAdapter(itemList[position].attr_list);

            // content가 있으면 이미지 표시
            // 없으면 invisible
            holder.calendar_content_img.visibility = when {
                itemList[position].content.length > 0 -> View.VISIBLE
                else -> View.INVISIBLE;
            }
        }

        else{
            holder.calendar_cell.visibility = View.INVISIBLE;
        }

    }

    inner class ClickTriggerRunnable : Runnable {

        var isClickTriggered = false

        override fun run() {
            isClickTriggered = true
        }
    }

    override fun getItemCount(): Int {
        return itemList.count();
    }

}