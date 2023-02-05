package com.example.popularmovies.pojo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GenresInfo (
    @SerializedName("genre")
    @Expose
    val genre: String? = null
)