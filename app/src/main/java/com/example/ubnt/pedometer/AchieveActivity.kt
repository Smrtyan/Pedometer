package com.example.ubnt.pedometer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import java.util.ArrayList

class AchieveActivity : AppCompatActivity() {
    internal var achievementList = ArrayList<Item>()
    internal lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achieve)

        val intent = intent
        val totalSteps = intent.getStringExtra("totalSteps").toInt()
        Toast.makeText(this,""+totalSteps,Toast.LENGTH_SHORT).show()

        val listView = findViewById<ListView>(R.id.my_listview)
        var levelOfSteps = 5000;
        for (honer in ACHIEVEMENT) {
            levelOfSteps+=5000
            val isUnlocked = totalSteps > levelOfSteps
            val description = "unlock when totally walk "+levelOfSteps+" steps"
            achievementList.add(Item(honer,description, isUnlocked ))
        }

        adapter = MyAdapter(this, R.layout.list_view_items, achievementList)
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

        }

    }
    companion object {

        private val ACHIEVEMENT = arrayOf("Freshman I", "Freshman II", "Freshman III", "Freshman IV", "Golden I", "Golden II", "Golden III", "Golden IV")
    }
}
