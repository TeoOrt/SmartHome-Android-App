package com.example.smart_home

import android.content.Intent
import android.content.res.AssetManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView

class VideoPlayer : AppCompatActivity() {

    private lateinit var assetManager:AssetManager
    private lateinit var videoViewPlayer:VideoView
    private lateinit var playButton:Button
    private lateinit var title:TextView
    private lateinit var recordVideo:Button
    private var uri: Uri? = null
    private var vid_title: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        uri =  intent.data
        videoViewPlayer = findViewById<VideoView>(R.id.VidView)
        playButton = findViewById<Button>(R.id.play_button)
        title = findViewById<TextView>(R.id.TitleVid)
        recordVideo = findViewById<Button>(R.id.NextRecordVid)

        title.text = intent.getStringExtra("Title")
        playButton.setOnClickListener {
            onButtonClick(it)
        }

        recordVideo.setOnClickListener {
            onNextScreen(it)
        }

    }

    private fun onNextScreen(v: View?){
        val intent = Intent(this@VideoPlayer,CameraTaking::class.java)
        intent.putExtra("Title",vid_title)
        startActivity(intent)
    }

    private fun onButtonClick(v: View?){
        if (playButton.text == "Replay"){
            videoViewPlayer.start()
            return
        }
//        val videoPath = Uri.parse("http://192.168.0.203:8080/get_expert/video/H-0.mp4")

        videoViewPlayer.setVideoURI(uri)
        videoViewPlayer.start()
        playButton.text = buildString {
        append("Replay")
    }
    }


}