package com.example.smart_home

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException
import kotlin.math.abs

class CameraTaking : AppCompatActivity() {
    private var recordButton: Button? = null
    private  var previewView: PreviewView? =null
    private val frontCamera = CameraSelector.LENS_FACING_FRONT;
    private lateinit var cameraProvider: ProcessCameraProvider


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_taking)
        requestPermissions()
        previewView = findViewById<PreviewView>(R.id.CameraID)
        recordButton = findViewById<Button>(R.id.Record_button)

    }

    override fun onDestroy() {
        super.onDestroy()
        unbindCamera()
    }

    private fun unbindCamera() {
        if (this::cameraProvider.isInitialized) {
            cameraProvider.unbindAll()
        }
    }

    private fun startCamera(camera : Int){
        val aspectRatio = previewView?.let { aspectRatio(it.width, previewView!!.height) }
        val listenableFut = ProcessCameraProvider.getInstance(this@CameraTaking)

        listenableFut.addListener({
            try {
                cameraProvider = listenableFut.get()//lets get our listenable
                val preview = aspectRatio?.let { Preview.Builder().setTargetAspectRatio(it).build() }
                val imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).setTargetRotation(windowManager.defaultDisplay.rotation).build()
                val cameraSelector = CameraSelector.Builder().requireLensFacing(camera).build()
                cameraProvider.unbindAll()
                val cam = cameraProvider.bindToLifecycle(this@CameraTaking,cameraSelector,preview,imageCapture)

                preview?.setSurfaceProvider(previewView?.surfaceProvider)
            }catch (e: ExecutionException ){
                e.printStackTrace()
            }catch (e: InterruptedException){
                e.printStackTrace()
            }

        },ContextCompat.getMainExecutor(this@CameraTaking))

    }


    private fun aspectRatio(width:Int, height:Int):Int{
        val previewRatio = Math.max(width,height)/ Math.min(width,height)
        return if(abs(previewRatio-4.0/3.0) <= abs(previewRatio -16.0/9.0)) {
            AspectRatio.RATIO_4_3
        }else{
            AspectRatio.RATIO_16_9
        }
    }


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
                startCamera(frontCamera)
            }
        }


    /** A safe way to get an instance of the Camera object. */

}
