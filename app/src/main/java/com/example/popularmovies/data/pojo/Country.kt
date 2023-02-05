package com.example.popularmovies.data.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Country (
    @SerializedName("country")
    @Expose
    val country: String? = null
)