package com.example.smart_home

import android.content.res.AssetManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.VideoView

class VideoPlayer : AppCompatActivity() {

    private lateinit var assetManager:AssetManager
    private lateinit var videoViewPlayer:VideoView
    private lateinit var playButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        assetManager = assets
        videoViewPlayer = findViewById<VideoView>(R.id.VidView)
        playButton = findViewById<Button>(R.id.play_button)
        playButton.setOnClickListener {
            onButtonClick(it)
        }
    }

    private fun onButtonClick(v: View?){
        val videoPath = Uri.parse("http://192.168.0.203:8080/get_expert/video/H-0.mp4")
        videoViewPlayer.setVideoURI(videoPath)
        videoViewPlayer.start()
        playButton.text = "Replay"
    }


}