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
import com.example.checker.DB.CheckListDetailViewer
import com.example.checker.DB.CheckListViewer
import com.example.checker.DB.DayListViewer
import com.example.checker.Item.SimpleListItem
import com.example.checker.R
import java.util.Calendar
import java.util.Date

class DayListDetailAdapter(
    private val today : Date,
    private val complete_click_listener:(Int, List<Boolean>, Int, Int) -> Unit
) : RecyclerView.Adapter<DayListDetailAdapter.DayListDetailViewHolder>()
{
    private var list  = emptyList<DayListViewer>();

    inner class DayListDetailViewHolder(item : View) : RecyclerView.ViewHolder(item) {
        val attr_name = item.findViewById<TextView>(R.id.daylist_checklist_attr_name)
        val checklist_name = item.findViewById<TextView>(R.id.daylist_checklist_name)
        val content_val = item.findViewById<TextView>(R.id.daylist_checklist_content)
        val complete_val = item.findViewById<ImageView>(R.id.daylist_complete_checkbox)
        val checklist_type = item.findViewById<TextView>(R.id.daylist_checklist_type_name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayListDetailAdapter.DayListDetailViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.daylist_shower, parent, false);
        return DayListDetailViewHolder(view);
    }

    override fun onBindViewHolder(
        holder: DayListDetailAdapter.DayListDetailViewHolder,
        position: Int
    ) {
        when(list[position].normal_list != null ){
            true -> {
                // 이름 넣기
                holder.checklist_name.text = list[position].normal_list!!.checklist_name;

                // 내용 넣기
                holder.content_val.text = list[position].normal_list!!.content;

                // 속성 설정하기
                holder.attr_name.setText(list[position].normal_list!!.attr_name);
                val color_val = Color.parseColor(list[position].normal_list!!.attr_color);
                holder.attr_name.backgroundTintList = ColorStateList.valueOf(color_val);

                // 유형 넣기
                holder.checklist_type.setText("기간")

                // 완료 상태에 따라 이미지 결정
                // 오늘의 complete 변수 값 찾기
                val dif : Int = when {
                    list[position].normal_list!!.start_date != list[position].normal_list!!.end_date -> {
                        ((today.time - list[position].normal_list!!.start_date.time) / (24 * 60 * 60 * 1000)).toInt();
                    }
                    else -> 0
                }
                when {
                    list[position].normal_list!!.complete[dif] -> {
                        holder.complete_val.setImageResource(R.drawable.checker_complete)
                    }
                    else -> {
                        holder.complete_val.setImageResource(R.drawable.checker_enable)
                    }
                }

                // 완료 버튼 눌렀을 때 이벤트 설정하기
                holder.complete_val.setOnClickListener { complete_click_listener(list[position].normal_list!!.id, list[position].normal_list!!.complete, dif, 0); }
            }
            false -> {
                // 이름 넣기
                holder.checklist_name.text = list[position].repeat_list!!.checklistrepeat_name;

                // 내용 넣기
                holder.content_val.text = list[position].repeat_list!!.content;

                // 속성 설정하기
                holder.attr_name.setText(list[position].repeat_list!!.attr_name);
                val color_val = Color.parseColor(list[position].repeat_list!!.attr_color);
                holder.attr_name.backgroundTintList = ColorStateList.valueOf(color_val);
                // 타입별 분류
                var dif : Int = 0;
                when(list[position].repeat_list!!.repeat_type){
                    0 -> { // 매일
                        // 유형 넣기
                        holder.checklist_type.setText("매일")
                        // 오늘의 complete 변수 찾기
                        dif = when {
                            today != list[position].repeat_list!!.start_date -> {
                                ((today.time - list[position].repeat_list!!.start_date.time) / (24 * 60 * 60 * 1000)).toInt();
                            }
                            else -> 0
                        }
                    }
                    1 -> {
                        // 유형 넣기
                        holder.checklist_type.setText("주간")
                        // 오늘의 complete 변수 찾기
                        // 여기에 오는 값은 반드시 today에 있는 값 이므로
                        // start_date로 부터 몇 번째 참인 날인지 알아야함
                        dif = when {
                            today != list[position].repeat_list!!.start_date -> {
                                // 오늘이 complete의 몇번째 일까
                                // 주간이므로 현재 주간 갯수를 알아야 함
                                // 0~6 월 ~ 일
                                val selected_day_of_week : ArrayList<Boolean> = ArrayList()
                                var week_val = list[position].repeat_list!!.week_val
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

                                // 몇주 다음 인지도 알아야함
                                var counter = -1;
                                val max : Int = list[position].repeat_list!!.complete.size
                                val date = Calendar.getInstance()
                                date.time = list[position].repeat_list!!.start_date
                                // start_date의 요일이 selected_day_of_week에 있으면 counter == 0부터 시작
                                // 없으면 -1부터 시작
                                val start_date_val : Int = date.get(Calendar.DAY_OF_WEEK)
                                val counter_select_val = when {
                                    start_date_val >= 2 -> start_date_val - 2
                                    else -> 6
                                }
                                if ( selected_day_of_week[counter_select_val] ) counter = 0

                                while(true){
                                    date.add(Calendar.DATE, 1)
                                    // 일 ~ 토 1 ~ 7
                                    val temp_val : Int = date.get(Calendar.DAY_OF_WEEK)
                                    val check_day_of_week_val = when {
                                        temp_val >= 2 -> temp_val - 2
                                        else -> 6
                                    }
                                    if ( selected_day_of_week[check_day_of_week_val] ) {
                                        counter+=1
                                    }

                                    if( today == date.time ) break;

                                    if(counter > max ) {
                                        counter = max-1
                                        break
                                    }
                                }
                                counter
                            }
                            else -> 0

                        }
                    }
                    2 -> {
                        // 유형 넣기
                        holder.checklist_type.setText("정기")
                        // 유형 및 변수 분류
                        val repeat_cycle_type = list[position].repeat_list!!.repeat_cycle / 100
                        val repeat_cycle_val = list[position].repeat_list!!.repeat_cycle % 100
                        val date = Calendar.getInstance()
                        date.time = list[position].repeat_list!!.start_date
                        var counter = 0;

                        when(repeat_cycle_type) {
                            1 -> {
                                while (true) {
                                    if (date.time > today) {
                                        break
                                    }
                                    if (date.time == today) {
                                        dif = counter
                                        break
                                    }
                                    date.add(Calendar.DATE, repeat_cycle_val)
                                    counter++
                                }
                            }
                            2 -> {
                                date.set(Calendar.DATE, repeat_cycle_val)
                                while (true) {
                                    if (date.time > today) {
                                        break
                                    }
                                    if (date.time == today) {
                                        dif = counter
                                        break
                                    }
                                    date.add(Calendar.MONTH, 1)
                                    counter++
                                }
                            }
                        }

                    }
                }
                // complete에 따라 이미지 넣기
                when {
                    list[position].repeat_list!!.complete[dif] -> {
                        holder.complete_val.setImageResource(R.drawable.checker_complete)
                    }
                    else -> {
                        holder.complete_val.setImageResource(R.drawable.checker_enable)
                    }
                }

                // 완료 버튼 눌렀을 때 이벤트 설정하기
                holder.complete_val.setOnClickListener { complete_click_listener(list[position].repeat_list!!.id, list[position].repeat_list!!.complete, dif, 1); }
            }

        }
        // 공통 적용
        holder.attr_name.setTextColor(Color.BLACK);
        holder.attr_name.setBackgroundResource(R.drawable.checklist_attr_border);
    }

    override fun getItemCount(): Int {
        return list.count();
    }

    public fun set_List(item : List<DayListViewer>){
        this.list = item;
        notifyDataSetChanged();
    }
}