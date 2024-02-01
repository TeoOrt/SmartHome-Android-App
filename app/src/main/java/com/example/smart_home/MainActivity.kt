package com.example.smart_home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.smart_home.R.layout.activity_main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import android.Manifest


class MainActivity : AppCompatActivity() {

    //private variables
    private lateinit var listIds: MutableList<ListItemStruct>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(activity_main)
        setInitialScreen()
        fetchVideoData()


        CoroutineScope(Dispatchers.Main).launch {
            for (ids in listIds){
                setNextActivity(ids)
            }
        }

    }


    /// Sets the text for the first screen
    /// This is useful for keeping track of all the id's
    private fun setInitialScreen(){
        listIds = mutableListOf<ListItemStruct>(
            ListItemStruct("Show Zero times","Record a video to show how you would do a zero times",R.id.CountZero),
            ListItemStruct("Show One time","Record a video to show how you would do a one time", R.id.CountOne),
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
    private  fun fetchVideoData() {
        val uriPath = "http://192.168.0.203:8080/get_expert/video/" //my home server address

        val mp4PathList= arrayOf<String>("H-0","H-1","H-2","H-3","H-4","H-5","H-6",
                                          "H-7","H-8","H-9","H-DecreaseFanSpeed",
                                          "H-IncreaseFanSpeed","H-FanOn","H-FanOff",
                                          "H-LightOn","H-LightOff",
                                          "H-SetThermo")
        var index = 0

        mp4PathList.forEach {
            val uri = "$uriPath$it.mp4"
            listIds[index].uri= uri
            index +=1
        }

    }

    private suspend fun setNextActivity(listItem:ListItemStruct)= withContext(Dispatchers.Main){
        val button = listItem.itemView.getNextActivityButton()
        button.setOnClickListener {
            val intent = Intent(this@MainActivity,VideoPlayer::class.java)
            intent.data = Uri.parse(listItem.uri)
            intent.putExtra("Title",listItem.titleText)
            Toast.makeText(this@MainActivity, intent.getStringExtra("Title"), Toast.LENGTH_SHORT).show()

            startActivity(intent)
        }
      }




    inner class ListItemStruct(titleText: CharSequence, inputText: CharSequence,itemId:Int) {
        var itemId : Int? = null
        val itemView:ListItem = findViewById<ListItem>(itemId)
        var uri: String? = null
        var titleText: CharSequence? = titleText

        init {

            itemView.inputText = inputText
            itemView.titleText = titleText
        }

    }


}