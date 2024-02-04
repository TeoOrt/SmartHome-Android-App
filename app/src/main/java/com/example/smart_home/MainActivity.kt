package com.example.smart_home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_home.R.layout.activity_main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    //private variables
    private lateinit var listIds: MutableList<ListItemStruct>
    private val indexMap = HashMap<String,Int>()


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

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch{
            getTrialNumbers()
        }
    }

    private suspend fun getTrialNumbers() = withContext(Dispatchers.IO){
        val baseUrl ="http://192.168.0.203:8080/"
        val api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApi::class.java)
        GlobalScope.launch(Dispatchers.IO){
            val result = api.getAll()
            if (result.isSuccessful) {
                val body = result.body()
                if (body != null) {
                    val temp_map_converter = getMap()
                    for (values in body){
                        val title  = temp_map_converter[values.title]
                        val id = indexMap[title]
                        withContext(Dispatchers.Main){
                            listIds[id!!].addRecord(values.number)
                        }
                    }
                }

            }
        }
    }



    /// Sets the text for the first screen
    /// This is useful for keeping track of all the id's
    private fun setInitialScreen(){
        listIds = mutableListOf(
            ListItemStruct("Show Zero times","Record a video to show how you would do a zero times",R.id.CountZero),
            ListItemStruct("Show One times","Record a video to show how you would do a one time", R.id.CountOne),
            ListItemStruct("Show Two times","Record a video to show how you would do a two times", R.id.CountTwo),
            ListItemStruct("Show Three times","Record a video to show how you would do a three times", R.id.CountThree),
            ListItemStruct("Show Four times","Record a video to show how you would do a four times", R.id.CountFour),
            ListItemStruct("Show Five times","Record a video to show how you would do a five times", R.id.CountFive),
            ListItemStruct("Show Six times","Record a video to show how you would do a six times", R.id.CountSix),
            ListItemStruct("Show Seven times","Record a video to show how you would do a seven times", R.id.CountSeven),
            ListItemStruct("Show Eight times","Record a video to show how you would do a eight times", R.id.CountEight),
            ListItemStruct("Show Nine times","Record a video to show how you would do a nine times", R.id.CountNine),
            ListItemStruct("Show Decrease Fan Speed","Record a video to show how you would decrease fan speed", R.id.DecFan),
            ListItemStruct("Show Increase Fan Speed","Record a video to show how you would increase fan speed", R.id.IncFan),
            ListItemStruct("Show Fan On","Record a video to show how you would turn a fan on", R.id.FanOn),
            ListItemStruct("Show Fan Off","Record a video to show how you would turn a fan off", R.id.FanOff),
            ListItemStruct("Show Light On","Record a video to show how you would turn a light on", R.id.LightOn),
            ListItemStruct("Show Light Off","Record a video to show how you would turn a light off", R.id.LightOff),
            ListItemStruct("Set Temperature","Record a video to show how you would set the temperature", R.id.Temperature),

            )

        for ((idx, ids) in listIds.withIndex()){
               indexMap[ids.titleText.toString()] = idx
        }

    }
    private  fun fetchVideoData() {
        val uriPath = "http://192.168.0.203:8080/get_expert/video/" //my home server address

        val mp4PathList= arrayOf("H-0","H-1","H-2","H-3","H-4","H-5","H-6",
                                          "H-7","H-8","H-9","H-DecreaseFanSpeed",
                                          "H-IncreaseFanSpeed","H-FanOn","H-FanOff",
                                          "H-LightOn","H-LightOff",
                                          "H-SetThermo")
        var index = 0

        mp4PathList.forEach {
            val uri = "$uriPath$it.mp4"
            val uriLocal = "$it.mp4" //tried to access local files but it was eaiser to use http
            listIds[index].uri= arrayOf(uri,uriLocal)
            index +=1
        }

    }

    private suspend fun setNextActivity(listItem:ListItemStruct)= withContext(Dispatchers.Main){
        val button = listItem.itemView.getNextActivityButton()
        button.setOnClickListener {
            val intent = Intent(this@MainActivity,VideoPlayer::class.java)
            intent.putExtra("Uris",listItem.uri)
            intent.putExtra("Title",listItem.titleText)
            Toast.makeText(this@MainActivity, intent.getStringExtra("Title"), Toast.LENGTH_SHORT).show()

            startActivity(intent)
        }
      }


    private fun getMap():HashMap<String,String>{

        val map =  HashMap<String,String>()
        map.put( "H-0.mp4","Show Zero times")
        map.put( "H-1.mp4","Show One times")
        map.put( "H-2.mp4","Show Two times")
        map.put( "H-3.mp4","Show Three times")
        map.put( "H-4.mp4","Show Four times")
        map.put( "H-5.mp4","Show Five times")
        map.put( "H-6.mp4","Show Six times")
        map.put( "H-7.mp4","Show Seven times")
        map.put( "H-8.mp4","Show Eight times")
        map.put( "H-9.mp4","Show Nine times")
        map.put( "H-DecreaseFanSpeed.mp4","Show Decrease Fan Speed")
        map.put( "H-IncreaseFanSpeed.mp4","Show Increase Fan Speed")
        map.put( "H-FanOn.mp4","Show Fan On")
        map.put( "H-FanOff.mp4","Show Fan Off")
        map.put( "H-LightOn.mp4","Show Light On")
        map.put( "H-LightOff.mp4","Show Light Off")
        map.put( "H-SetThermo.mp4","Set Temperature")
        return map
    }



    inner class ListItemStruct(titleText: CharSequence, inputText: CharSequence,itemId:Int) {
        val itemView:ListItem = findViewById(itemId)
        lateinit var uri: Array<String>
        var titleText: CharSequence? = titleText


        init {
            itemView.inputText = inputText
            itemView.titleText = titleText
        }

        fun addRecord(trialNumber:Int){
            val trialString = trialNumber.toString()
            itemView.recorded = trialString
        }

    }


}