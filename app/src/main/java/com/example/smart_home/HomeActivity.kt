package com.example.smart_home

import android.os.Bundle
import android.view.View
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    private var expandableListView: ExpandableListView? = null
    private val expandableListAdapter: ExpandableListAdapter? = null
    var expandableTitlesArray: List<String>? = null
    private val expandableMapDetail: HashMap<String, List<String>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        expandableListView = findViewById<View>(R.id.expandableListView) as ExpandableListView
        //        expandableMapDetail = ExpandableListDataPump.getData()
    }
}
