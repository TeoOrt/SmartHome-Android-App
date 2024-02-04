package com.example.smart_home

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
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
import androidx.cardview.widget.CardView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.net.toFile

import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Field


class CameraTaking : AppCompatActivity() {
    private lateinit var recordButton: Button
    private lateinit var returnHome: Button
    private lateinit var uploadButton:Button
    private  var previewView: PreviewView? =null
    private val frontCamera = CameraSelector.LENS_FACING_FRONT
    private lateinit var title: String
    private var recording : Recording? = null
    private var videoCapture : VideoCapture<Recorder>? = null
    private val IoScope = CoroutineScope(Dispatchers.IO)
    private val MainScope = CoroutineScope(Dispatchers.Main)
    private var trialNumber: Number =0
    private var uploadUri: Uri? = null //uri to upload video

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_taking)
        requestPermissions()

        previewView = findViewById<PreviewView>(R.id.CameraID)
        returnHome = findViewById<Button>(R.id.ReturnHome)
        recordButton = findViewById(R.id.Record_button)
        uploadButton = findViewById(R.id.Upload_button)

        initCameraFragment()
        IoScope.launch {
            title = intent.getStringExtra("Title").toString()
            title = title.replace(' ', '_')
        }
        setButtons()
    }

    private fun initCameraFragment(){
        lifecycleScope.launch {
            bindCaptureUse()
        }
    }

    private fun setButtons(){
        returnHome.setOnClickListener {
            recording?.stop()
            val intent  = Intent(this@CameraTaking,MainActivity::class.java)
            startActivity(intent)
        }

        recordButton.setOnClickListener {
            if (recordButton.text == "Stop Recording"){
                recordButton.text = "Start Recording"
                recording?.stop()
            }else{
                IoScope.launch {
                    takeVideo()
                }
                recordButton.text="Stop Recording"
            }
        }
        uploadButton.setOnClickListener {
            IoScope.launch {
                uploadVideo()
            }
        }
    }

    private suspend fun uploadVideo(){
        val baseUrl ="http://192.168.0.203:8080/"
        val api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApi::class.java)
        val file = uploadUri?.toFile(this@CameraTaking)


        Log.e("Upload","This is the filee  ${file.toString()}")
        val titleRequestBody = RequestBody.create(MultipartBody.FORM,"Ortega")
        val nameRequestBody = RequestBody.create(MultipartBody.FORM,title)
        val fileRequestBody = file?.let { RequestBody.create(MultipartBody.FORM, it) }
        val filePart =
            fileRequestBody?.let { MultipartBody.Part.createFormData("file_field", file.name, it) }



        IoScope.launch(Dispatchers.IO){
                val res = filePart?.let { api.postVideos(titleRequestBody,nameRequestBody, it) }
                withContext(Dispatchers.Main){
                    if (res != null) {
                        if(res.isSuccessful){
                            Log.d("YES","Video uploaded succesfully")
                            Toast.makeText(this@CameraTaking, "Video Succesfully Uploaded going back to Home...", Toast.LENGTH_SHORT).show()
                            Thread.sleep(500)
                            val intent  = Intent(this@CameraTaking,MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this@CameraTaking,"Video failed to upload, check connection...",Toast.LENGTH_SHORT).show()
                            Log.e("Error Http","$res.message()")
                        }
                    }
            }

        }
    }

    private suspend fun bindCaptureUse() = withContext(Dispatchers.IO) {
        val aspectRatio = AspectRatio.RATIO_16_9

        val cameraProvider = ProcessCameraProvider.getInstance(this@CameraTaking).await()
        val cameraSelector = CameraSelector.Builder().requireLensFacing(frontCamera).build()
        val quality = Quality.LOWEST
        val recorder = Recorder.Builder().setQualitySelector(QualitySelector.from(quality))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)
        MainScope.launch {
            try {
                val preview = Preview.Builder().setTargetAspectRatio(aspectRatio).build().apply {
                    setSurfaceProvider(previewView?.surfaceProvider)
                }
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this@CameraTaking,
                    cameraSelector,
                    videoCapture,
                    preview
                )
            } catch (e: Exception) {
                Log.e("Camera", "Binding Failed", e)
            }
            }
        }


    private suspend fun takeVideo()= withContext(Dispatchers.IO){

        val name = "{$title}_${trialNumber}_ORTEGA.mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,name)
        }
        val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
            contentResolver,MediaStore.Video.Media.EXTERNAL_CONTENT_URI) //setting limit
            .setContentValues(contentValues).setDurationLimitMillis(6000).build()

        recording= videoCapture?.output?.prepareRecording(this@CameraTaking,mediaStoreOutputOptions)
            ?.start(ContextCompat.getMainExecutor(this@CameraTaking),recordingListener)

    }
    private val recordingListener = Consumer<VideoRecordEvent>{event->

        when(event){
            is VideoRecordEvent.Start ->{
                val msg= "Starting Recording"
                MainScope.launch {
                    Toast.makeText(this@CameraTaking,msg,Toast.LENGTH_SHORT).show()
                }
            }
            is VideoRecordEvent.Finalize    ->{
                //an error is send but it's because of the 5 second timer I added so it's fine
                val playback = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(event.outputResults.outputUri,"video/*")
                }
                uploadUri=event.outputResults.outputUri
                uploadButton.visibility = CardView.VISIBLE
                startActivity(playback)
            }
        }

    }


    //Helper function
    fun Uri.toFile(context: Context):File?{
        val contentResolver  = context.contentResolver
        return try{
            val inputStream = contentResolver.openInputStream(this)
            inputStream?.use {input->
                val file = File.createTempFile( "{$title}_${trialNumber}_ORTEGA",".mp4",context.cacheDir)
                FileOutputStream(file).use {
                    input.copyTo(it)
                }
                file
            }
        }catch (e:Exception){
            Log.e("UriToFile","Could not get file ${e.message}")
            null
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
            }
            }




    /** A safe way to get an instance of the Camera object. */

}
