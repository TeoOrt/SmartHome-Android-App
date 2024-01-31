package com.example.smart_home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.smart_home.R.layout.activity_main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    //private variables
    private lateinit var listIds: MutableList<ListItemStruct>
    private val linkMap = HashMap<Uri, ListItemStruct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        setInitialScreen()
        for (ids in listIds){
            setNextActivity(ids.itemView)
        }
        CoroutineScope(Dispatchers.Main).launch {
            fetchVideoData()
        }
    }


    /// Sets the text for the first screen
    /// This is useful for keeping track of all the id's
    private fun setInitialScreen(){
        listIds = mutableListOf<ListItemStruct>(
            ListItemStruct("Show One time","Record a video to show how you would do a one time", R.id.CountOne),
            ListItemStruct("Show Zero times","Record a video to show how you would do a zero times",R.id.CountZero),
            ListItemStruct("Show Two times","Record a video to show how you would do a two times", R.id.CountTwo),
            ListItemStruct("Show Three time","Record a video to show how you would do a three times", R.id.CountThree),
            ListItemStruct("Show Four time","Record a video to show how you would do a four times", R.id.CountFour),
            ListItemStruct("Show Five time","Record a video to show how you would do a five times", R.id.CountFive),
            ListItemStruct("Show Six time","Record a video to show how you would do a six times", R.id.CountSix),
            ListItemStruct("Show Seven time","Record a video to show how you would do a seven times", R.id.CountSeven),
            ListItemStruct("Show Eight time","Record a video to show how you would do a eight times", R.id.CountEight),
            ListItemStruct("Show Nine time","Record a video to show how you would do a nine times", R.id.CountNine),
            ListItemStruct("Show Decrease Fan Speed","Record a video to show how you would decrease fan speed", R.id.DecFan),
            ListItemStruct("Show Increase Fan Speed","Record a video to show how you would increase fan speed", R.id.IncFan),
            ListItemStruct("Show Fan On","Record a video to show how you would turn a fan on", R.id.FanOn),
            ListItemStruct("Show Fan Off","Record a video to show how you would turn a fan off", R.id.FanOff),
            ListItemStruct("Show Light On","Record a video to show how you would turn a light on", R.id.LightOn),
            ListItemStruct("Show Light Off","Record a video to show how you would turn a light off", R.id.LightOff),
            ListItemStruct("Set Temperature","Record a video to show how you would set the tempeurate", R.id.Temperature),

            )


    }
    private suspend fun fetchVideoData() = withContext(Dispatchers.IO){
        val uri_path = "http://192.168.0.203:8080/get_expert/video/" //my home server address

        val mp4Path_list= arrayOf<String>("H-0","H-1","H-2","H-3","H-4","H-5","H-6",
                                          "H-7","H-8","H-9","H-DecreaseFanSpeed",
                                          "H-IncreaseFanSpeed","H-FanOn","H-FanOff",
                                          "H-LightOn","H-LightOff",
                                          "H-SetThermo")
        var index = 0

        mp4Path_list.forEach {
            val uri = Uri.parse("$uri_path$it.mp4")
            linkMap[uri] = listIds[index]
            index+=1
        }

        withContext(Dispatchers.Main){
            Toast.makeText(this@MainActivity,"Size of mp4 ${mp4Path_list.size} and size of listIds is ${listIds.size}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setNextActivity(listItem:ListItem){
        val button = listItem.getNextActivityButton()
        button.setOnClickListener {
            val intent = Intent(this@MainActivity,VideoPlayer::class.java)
            intent.putExtra("URI",linkMap[])

            startActivity(Intent(this@MainActivity,VideoPlayer::class.java))
        }

    }




    inner class ListItemStruct(titleText: CharSequence, inputText: CharSequence,itemId:Int) {
        private var titleText : CharSequence = titleText
        private var inputText: CharSequence = inputText
        var itemId : Int? = null
        val itemView:ListItem = findViewById<ListItem>(itemId)

        init {

            itemView.inputText = inputText
            itemView.titleText = titleText
        }

    }




}