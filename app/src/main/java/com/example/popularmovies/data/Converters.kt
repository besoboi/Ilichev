package com.example.popularmovies.data

import androidx.room.TypeConverter
import com.example.popularmovies.data.pojo.Country
import com.example.popularmovies.data.pojo.GenresInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {

    @TypeConverter
    fun genresListToString(value: List<GenresInfo>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<GenresInfo>>() {}.type
        return gson.toJson(value, type)
    }
    @TypeConverter
    fun stringToGenresList(value: String): List<GenresInfo> {
        val gson = Gson()
        val type = object : TypeToken<List<GenresInfo>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun countryListToString(value: List<Country>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Country>>() {}.type
        return gson.toJson(value, type)
    }
    @TypeConverter
    fun stringToCountriesList(value: String): List<Country> {
        val gson = Gson()
        val type = object : TypeToken<List<Country>>() {}.type
        return gson.fromJson(value, type)
    }
}