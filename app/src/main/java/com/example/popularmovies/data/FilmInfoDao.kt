package com.example.popularmovies.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.popularmovies.pojo.Film
import com.example.popularmovies.pojo.FilmDetailedInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface FilmInfoDao {
    @Query("SELECT * FROM film_list")
    fun getFavouriteMoviesList(): Single<List<Film>>

    @Query("SELECT * FROM film_detailed_info WHERE kinopoiskId == :id LIMIT 1")
    fun getDetailedInfoAboutFilm(id : Int): Single<FilmDetailedInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouriteMovie(film: Film) : Completable

    @Query("DELETE FROM film_list WHERE filmId == :id")
    fun deleteFavouriteMovie(id : Int) : Single<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilmDetailedInfo(filmDetailedInfo: FilmDetailedInfo) : Completable

    @Query("DELETE FROM film_detailed_info WHERE kinopoiskId == :id")
    fun deleteFilmDetailedInfo(id : Int) : Completable

    @Query("SELECT * FROM film_list WHERE filmId == :id")
    fun getFavouriteFilm(id : Int) : Single<Film>
}