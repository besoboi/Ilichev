package com.example.popularmovies.data.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FilmsList (
    @SerializedName("pagesCount")
    @Expose
    val pagesCount: Int? = null,

    @SerializedName("films")
    @Expose
    val films: List<Film>? = null
)