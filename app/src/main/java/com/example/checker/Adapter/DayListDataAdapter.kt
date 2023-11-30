package com.example.checker.Adapter

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checker.Item.SimpleListItem
import com.example.checker.R


class DayListDataAdapter(private val list : ArrayList<SimpleListItem>) : RecyclerView.Adapter<DayListDataAdapter.DayListDataViewHolder>()
{

    inner class DayListDataViewHolder(item : View) : RecyclerView.ViewHolder(item){
        val attr_img = item.findViewById<ImageView>(R.id.daylist_attr_img)
        val list_name = item.findViewById<TextView>(R.id.daylist_list_name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayListDataAdapter.DayListDataViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.daylist_checklist_shower, parent, false);
        return DayListDataViewHolder(view);
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(
        holder: DayListDataAdapter.DayListDataViewHolder,
        position: Int
    ) {
        val text = when {
            list[position].name.length >= 18 ->
                list[position].name.substring(0,16) + "..."
            else ->
                list[position].name
        }
        holder.list_name.setText(text);

        holder.attr_img.setColorFilter( Color.parseColor(list[position].attr_color));
    }

    fun ConvertSPtoPX(context: Context, sp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}