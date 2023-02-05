package com.example.popularmovies.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.popularmovies.data.FilmDatabase
import com.example.popularmovies.domain.ApiFactory
import com.example.popularmovies.pojo.Film
import com.example.popularmovies.pojo.FilmDetailedInfo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.InternalCoroutinesApi

class FilmsViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
       private const val TAG = "FilmsViewModel"
    }

    private val db = FilmDatabase.getInstance(application)
    private var page = 1

    var filmList : MutableLiveData<List<Film>> = MutableLiveData<List<Film>>()
    var isLoading : MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)


    fun getDetailInfoFromDB(id : Int) : FilmDetailedInfo {
        return db.filmInfoDao().getDetailedInfoAboutFilm(id)
    }

    fun getFilmListFromDB() : LiveData<List<Film>> {
        return db.filmInfoDao().getFavouriteMoviesList()
    }

    fun getDetailInfoFromWeb(id : Int) : Single<FilmDetailedInfo> {
        return ApiFactory.apiService.getFilmDetailedInfo(id)
    }

    fun addFilmToFavourite(film : Film) {
        db.filmInfoDao().insertFavouriteMovie(film).subscribeOn(Schedulers.io()).subscribe()
    }

    fun removeFilmFromFavourite(id : Int) {
        db.filmInfoDao().deleteFavouriteMovie(id).subscribeOn(Schedulers.io()).subscribe()
    }

    fun getFavouriteFilm(id : Int) : LiveData<Film>{
        return db.filmInfoDao().getFavouriteFilm(id)
    }

    private val compositeDisposable = CompositeDisposable()

    @OptIn(InternalCoroutinesApi::class)
    fun loadData(){
        val loading = isLoading.value
        if (loading != null && loading) {
            return
        }
        val disposable = ApiFactory.apiService.getPopularFilms(page = page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                isLoading.value = true
            }
            .doAfterTerminate {
                isLoading.value = false
            }
            .subscribe({
                val loadedFilms = filmList.value?.toMutableList()
                if (loadedFilms != null) {
                    loadedFilms += it.films as List<Film>
                    filmList.value = loadedFilms
                } else {
                    filmList.value = it.films
                }
                page++
            },{
                it.message?.let { it1 -> Log.d(TAG, it1) }
            })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}