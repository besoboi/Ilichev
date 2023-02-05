package com.example.popularmovies.data.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Entity("film_detailed_info")
data class FilmDetailedInfo (
    @PrimaryKey
    @SerializedName("kinopoiskId")
    @Expose
    val kinopoiskId: Int? = null,

    @SerializedName("nameRu")
    @Expose
    val nameRu: String? = null,

    @SerializedName("posterUrl")
    @Expose
    val posterUrl: String? = null,

    @SerializedName("year")
    @Expose
    val year: Int? = null,

    @SerializedName("description")
    @Expose
    val description: String? = null,

    @SerializedName("countries")
    @Expose
    val countries: List<Country>? = null,

    @SerializedName("genres")
    @Expose
    val genres: List<GenresInfo>? = null,

    @SerializedName("lastSync")
    @Expose
    val lastSync: String? = null
)