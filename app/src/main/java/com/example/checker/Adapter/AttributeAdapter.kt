package com.example.checker.Adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.DB.AttributeList
import com.example.checker.R

class AttributeAdapter(private val click_listener: (TextView, AttributeList) -> Unit) :
    RecyclerView.Adapter<AttributeAdapter.AttributeViewHolder>() {

    private var itemList = emptyList<AttributeList>();

    inner class AttributeViewHolder(item : View) : RecyclerView.ViewHolder(item){
        val attr_name = item.findViewById<TextView>(R.id.attr_name);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributeViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.attribute_layout, parent, false);
        return AttributeViewHolder(view);
    }

    override fun getItemCount(): Int {
        return itemList.count();
    }

    // 어떻게 보여질 것인지 작성
    override fun onBindViewHolder(holder: AttributeViewHolder, position: Int) {
        // 이름 설정
        holder.attr_name.text = itemList[position].name;

        // 색 설정
        //Log.d("checker color", itemList[position].name);
        //Log.d("checker color", itemList[position].color);
        val colorint = Color.parseColor(itemList[position].color);
        holder.attr_name.backgroundTintList = ColorStateList.valueOf(colorint);
        holder.attr_name.setOnClickListener { click_listener(holder.attr_name, itemList[position]) }
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setItem(item : List<AttributeList> ){
        this.itemList = item;
        notifyDataSetChanged();
    }
}