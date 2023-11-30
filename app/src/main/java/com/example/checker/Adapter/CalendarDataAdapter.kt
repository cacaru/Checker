package com.example.checker.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.R

class CalendarDataAdapter(private val list : ArrayList<String>) : RecyclerView.Adapter<CalendarDataAdapter.CalendarDataViewHolder>() {

    inner class CalendarDataViewHolder(item : View) : RecyclerView.ViewHolder(item){
        val image_data = item.findViewById<ImageView>(R.id.has_list_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDataViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.calendar_has_list, parent, false);
        return CalendarDataViewHolder(view);
    }

    override fun getItemCount(): Int {
        return list.count();
    }

    override fun onBindViewHolder(holder: CalendarDataViewHolder, position: Int) {
        holder.image_data.setColorFilter( Color.parseColor(list[position]));
    }
}