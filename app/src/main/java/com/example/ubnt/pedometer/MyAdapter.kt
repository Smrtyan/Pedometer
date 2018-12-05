package com.example.ubnt.pedometer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class MyAdapter(context: Context, textViewResourceId: Int, objects: ArrayList<Item>) : ArrayAdapter<Item>(context, textViewResourceId, objects) {

    internal var achievementList = ArrayList<Item>()
    init {
        achievementList = objects
    }

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var v = convertView
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = inflater.inflate(R.layout.list_view_items, null)

        val tv_title = v!!.findViewById<TextView>(R.id.textView_title)
        tv_title.text = achievementList[position].name

        val tv_description = v!!.findViewById<View>(R.id.textView_description) as TextView
        tv_description.text = achievementList[position].description
        val iv_lock = v!!.findViewById<ImageView>(R.id.imageView_lock)
        if (achievementList[position].isUnlocked)
            iv_lock.setImageResource(R.drawable.unlock)


        return v

    }

}
