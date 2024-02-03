package com.example.smart_home

import android.Manifest
import android.content.Intent
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoOutput
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutionException
import kotlin.math.abs

class CameraTaking : AppCompatActivity() {
    private lateinit var recordButton: Button
    private  var previewView: PreviewView? =null
    private val frontCamera = CameraSelector.LENS_FACING_FRONT
    private lateinit var camera : Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_taking)
        requestPermissions()
        previewView = findViewById<PreviewView>(R.id.CameraID)
        recordButton = findViewById<Button>(R.id.Record_button)
        recordButton.setOnClickListener {
                val intent  = Intent(this@CameraTaking,MainActivity::class.java)
                startActivity(intent)
        }

    }

    private suspend fun startCamera() = withContext(Dispatchers.IO){
        val aspectRatio = previewView?.let { aspectRatio(it.width, previewView!!.height) }
        val listenableFut = ProcessCameraProvider.getInstance(this@CameraTaking)
        Thread.sleep(500)
        listenableFut.addListener({
            try {
                val cameraProvider = listenableFut.get()//lets get our listenable
                val preview = aspectRatio?.let { Preview.Builder().setTargetAspectRatio(it).build() }
                val imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).setTargetRotation(windowManager.defaultDisplay.rotation).build()
                val cameraSelector = CameraSelector.Builder().requireLensFacing(frontCamera).build()
                cameraProvider.unbindAll()
                camera = cameraProvider.
                bindToLifecycle(
                    this@CameraTaking,
                    cameraSelector,
                    preview,
                    imageCapture)

                preview?.setSurfaceProvider(previewView?.surfaceProvider)
            }catch (e: ExecutionException ){
                e.printStackTrace()
            }catch (e: InterruptedException){
                e.printStackTrace()
            }

        },ContextCompat.getMainExecutor(this@CameraTaking))

    }

//    private fun takeVideo(mediaRecorder: MediaRecorder){
//        val videoCapture:VideoCapture<Recorder> = VideoCapture.withOutput(Recorder.Builder().build())
//    }

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
