package com.example.smart_home

import com.google.gson.annotations.SerializedName
import java.io.File

data class ListItemCounter(
    @SerializedName("title") val title: String?,
    @SerializedName("counter") val number: Int,
)
