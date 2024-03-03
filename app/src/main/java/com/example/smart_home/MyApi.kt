package com.example.smart_home

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MyApi {
    @Multipart
    @POST("/upload_video")
    suspend fun postVideos(
        @Part("last_name") title: RequestBody,
        @Part("video_type") name: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @GET("/get_counter")
    suspend fun getAll() : Response<List<ListItemCounter>>
}

