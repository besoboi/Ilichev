package com.example.popularmovies.domain

import com.example.popularmovies.pojo.FilmDetailedInfo
import com.example.popularmovies.pojo.FilmsList
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers(
        "$QUERY_HEADER_API_KEY: $API_KEY_VALUE",
        "Content-Type: application/json"
    )
    @GET("api/v2.2/films/top")
    fun getPopularFilms(
        @Query(QUERY_PARAM_TYPE) type: String = TYPE_VALUE,
        @Query(QUERY_PARAM_PAGE) page: Int = 1
    ) : Single<FilmsList>

    @Headers(
        "$QUERY_HEADER_API_KEY: $API_KEY_VALUE",
        "Content-Type: application/json"
    )
    @GET("api/v2.2/films/{id}")
    fun getFilmDetailedInfo(
        @Path("id") filmId: Int
    ): Single<FilmDetailedInfo>

    companion object {
        private const val QUERY_PARAM_TYPE = "type"
        private const val TYPE_VALUE = "TOP_100_POPULAR_FILMS"
        private const val QUERY_PARAM_PAGE = "page"
        private const val QUERY_HEADER_API_KEY = "X-API-KEY"
        private const val API_KEY_VALUE = "e30ffed0-76ab-4dd6-b41f-4c9da2b2735b"
    }
}