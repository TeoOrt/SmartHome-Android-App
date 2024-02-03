package com.example.smart_home
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException


class VideoPlayer : AppCompatActivity() {

    private lateinit var videoViewPlayer:VideoView
    private lateinit var playButton:Button
    private lateinit var title:TextView
    private lateinit var recordVideo:Button
    private var uri: Uri? = null
    private var vidTitle: String? = null
    private var playedVid = false
    private val scope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        uri =  intent.data
        videoViewPlayer = findViewById(R.id.VidView)
        playButton = findViewById(R.id.play_button)
        title = findViewById(R.id.TitleVid)
        recordVideo = findViewById(R.id.NextRecordVid)

        title.text = intent.getStringExtra("Title")
        vidTitle=intent.getStringExtra("Title")

            playButton.setOnClickListener {
                scope.launch {
                    onStartVid() }

            }
            recordVideo.setOnClickListener {
                scope.launch {  onNextScreen()}
            }

        videoViewPlayer.setOnCompletionListener {
            playedVid = true
            playButton.text = "Replay"
        }



    }

    override fun onResume() {
        super.onResume()
        playButton.text = "Start"
        playedVid = false
    }



    private fun checkCameraUnbind(){
        val listenableFut = ProcessCameraProvider.getInstance(this@VideoPlayer)

        listenableFut.addListener({
            try {
                val cameraProvider = listenableFut.get()
                cameraProvider.unbindAll()
            }catch (e:ExecutionException){
                e.printStackTrace()
            }catch (e:InterruptedException){
                e.printStackTrace()
            }
        },ContextCompat.getMainExecutor(this@VideoPlayer))
    }


    private fun onNextScreen(){
        if(playedVid){
            checkCameraUnbind()
            videoViewPlayer.stopPlayback()
            val intent = Intent(this@VideoPlayer,CameraTaking::class.java)
//            intent.putExtra("Title",vidTitle)
            startActivity(intent)
            }
        else {
            Toast.makeText(this@VideoPlayer, "Must watch video first", Toast.LENGTH_SHORT).show()
        }
        }

    private suspend fun onStartVid() = withContext(Dispatchers.Main) {

        if (playButton.text == "Replay") {
            videoViewPlayer.start()
            return@withContext
        }

        videoViewPlayer.setVideoURI(uri) // http golang server this wont work if server is not running
        videoViewPlayer.start()
        playButton.text = buildString {
            append("Replay")
        }
        playedVid = true

    }
}