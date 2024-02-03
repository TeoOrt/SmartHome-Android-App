package com.example.smart_home

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaRecorder
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.ExecutionException
import kotlin.math.abs

class CameraTaking : AppCompatActivity() {
    private lateinit var recordButton: Button
    private lateinit var returnHome: Button
    private  var previewView: PreviewView? =null
    private val frontCamera = CameraSelector.LENS_FACING_FRONT
    private lateinit var title: String
    private var recording : Recording? = null
    private var videoCapture : VideoCapture<Recorder>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_taking)
        requestPermissions()
        previewView = findViewById<PreviewView>(R.id.CameraID)
        returnHome = findViewById<Button>(R.id.ReturnHome)
        recordButton = findViewById(R.id.Record_button)
        returnHome.setOnClickListener {
                val intent  = Intent(this@CameraTaking,MainActivity::class.java)
                startActivity(intent)
        }
        title = intent.getStringExtra("title").toString()
        title= title.replace(' ','_')

    }

    private suspend fun startCamera() = withContext(Dispatchers.IO){
        val aspectRatio = previewView?.let { aspectRatio(it.width, previewView!!.height) }
        val listenableFut = ProcessCameraProvider.getInstance(this@CameraTaking)
        listenableFut.addListener({
            try {
                val cameraProvider = listenableFut.get()//lets get our listenable
                val preview = aspectRatio?.let { Preview.Builder().setTargetAspectRatio(it).build() }
                val recorder =  Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HD)).build()
                videoCapture = VideoCapture.withOutput(recorder)
                val cameraSelector = CameraSelector.Builder().requireLensFacing(frontCamera).build()
                cameraProvider.unbindAll()
                val camera = cameraProvider.
                bindToLifecycle(
                    this@CameraTaking,
                    cameraSelector,
                    preview,
                    videoCapture)

                takeVideo()
                recording?.pause()
                preview?.setSurfaceProvider(previewView?.surfaceProvider)
                recordButton.setOnClickListener {
                    if (recordButton.text == "Stop Recording"){
                        recording?.stop()
                    }else{
                        recording!!.resume()
                        recordButton.text="Stop Recording"

                    }

                }

            }catch (e: ExecutionException ){
                Log.e("Camera","Use case binding falied",e)
            }catch (e: InterruptedException){
                Log.e("Camera","Use case binding falied",e)
            }

        },ContextCompat.getMainExecutor(this@CameraTaking))

    }

    private fun takeVideo(){


        val recordingListener = Consumer<VideoRecordEvent>{event->
            when(event){
                is VideoRecordEvent.Start ->{
                    val msg= "Starting Recording"
                    Toast.makeText(this@CameraTaking,msg,Toast.LENGTH_SHORT).show()
                }
                is VideoRecordEvent.Finalize ->{
                    val msg = if(!event.hasError()){
                        val playback = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(event.outputResults.outputUri,"video/*")
                        }
                        startActivity(playback)
                        "Video capture succedded : ${event.outputResults.outputUri}"
                    }else{
                        recording?.close()
                        recording = null
                        "Video capture failed : ${event.error}"

                    }
                    Toast.makeText(this@CameraTaking,msg,Toast.LENGTH_SHORT).show()
                }
            }

        }
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,"CameraX-VideoCapture")
            put(MediaStore.MediaColumns.MIME_TYPE,"video/mp4")
        }
        val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
            contentResolver,MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues).build()

        recording= videoCapture?.output?.prepareRecording(this,mediaStoreOutputOptions)
            ?.start(ContextCompat.getMainExecutor(this@CameraTaking),recordingListener)

    }

    /*
    *  Calculate aspectRatio for recording
    *
    * */
    private fun aspectRatio(width:Int, height:Int):Int{

        if(width == height){ // in java if width/height are equal it returns a zero
            return AspectRatio.RATIO_4_3
        }
        val previewRatio = Math.max(width,height)/ Math.min(width,height)
        return if(abs(previewRatio-4.0/3.0) <= abs(previewRatio -16.0/9.0)) {
            AspectRatio.RATIO_4_3
        }else{
            AspectRatio.RATIO_16_9
        }
    }




    /*
    *
    * ------------------------------------------------------
    * Permissions stuff
    *
    *
    * */
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            }else{
                CoroutineScope(Dispatchers.Main).launch {
                startCamera()
                }
            }
        }



    /** A safe way to get an instance of the Camera object. */

}
