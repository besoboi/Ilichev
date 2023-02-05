package com.example.popularmovies.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.popularmovies.pojo.Film
import com.example.popularmovies.pojo.FilmDetailedInfo
import io.reactivex.rxjava3.core.Completable

@Dao
interface FilmInfoDao {
    @Query("SELECT * FROM film_list")
    fun getFavouriteMoviesList(): LiveData<List<Film>>

    @Query("SELECT * FROM film_detailed_info WHERE kinopoiskId == :id LIMIT 1")
    fun getDetailedInfoAboutFilm(id : Int): FilmDetailedInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouriteMovie(film: Film) : Completable

    @Query("DELETE FROM film_list WHERE filmId == :id")
    fun deleteFavouriteMovie(id : Int) : Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilmDetailedInfo(filmDetailedInfo: FilmDetailedInfo) : Completable

    @Query("DELETE FROM film_detailed_info WHERE kinopoiskId == :id")
    fun deleteFilmDetailedInfo(id : Int) : Completable

    @Query("SELECT * FROM film_list WHERE filmId == :id")
    fun getFavouriteFilm(id : Int) : LiveData<Film>
}