package com.example.popularmovies.data.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "film_list")
data class Film (
    @PrimaryKey
    @SerializedName("filmId")
    @Expose
    val filmId: Int? = null,

    @SerializedName("nameRu")
    @Expose
    val nameRu: String? = null,

    @SerializedName("year")
    @Expose
    val year: String? = null,

    @SerializedName("genres")
    @Expose
    val genres: List<GenresInfo>? = null,

    @SerializedName("posterUrl")
    @Expose
    val posterUrl: String? = null,

    @SerializedName("posterUrlPreview")
    @Expose
    val posterUrlPreview: String? = null,

    var isFavourite: Boolean = false
)