package com.example.smart_home

import com.google.gson.annotations.SerializedName
import java.io.File

data class UploadVideo(
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("vide_type") val videoType: String?,
    @SerializedName("file_field") val file: File?,
)
