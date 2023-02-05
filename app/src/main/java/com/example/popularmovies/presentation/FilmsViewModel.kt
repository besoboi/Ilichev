package com.example.popularmovies.presentation

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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

class FilmsViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "FilmsViewModel"
    }

    private val db = FilmDatabase.getInstance(application)
    private var page = 1

    var filmList: MutableLiveData<List<Film>> = MutableLiveData<List<Film>>()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)


    fun getDetailInfoFromDB(id: Int): Single<FilmDetailedInfo> {
        return db.filmInfoDao().getDetailedInfoAboutFilm(id)
    }

    fun getFilmListFromDB(): Single<List<Film>> {
        return db.filmInfoDao().getFavouriteMoviesList()
    }

    fun getDetailInfoFromWeb(id: Int): Single<FilmDetailedInfo> {
        return ApiFactory.apiService.getFilmDetailedInfo(id)
    }

    fun addFilmToFavourite(film: Film) {
        db.filmInfoDao().insertFavouriteMovie(film).subscribeOn(Schedulers.io()).subscribe()
        film.filmId?.let {
            getDetailInfoFromWeb(it).subscribeOn(Schedulers.io()).subscribe({
                db.filmInfoDao().insertFilmDetailedInfo(it).subscribeOn(Schedulers.io()).subscribe()
            }, {
                it.message?.let { it1 -> Log.d(TAG, it1) }
            })
        }
    }

    fun removeFilmFromFavourite(id: Int) : Single<Int> {
        return db.filmInfoDao().deleteFavouriteMovie(id)
    }

    private fun getFavouriteFilm(id: Int): Single<Film> {
        return db.filmInfoDao().getFavouriteFilm(id)
    }

    private val compositeDisposable = CompositeDisposable()

    fun loadData() {
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
                if (filmList.value != null) {
                    for (film in filmList.value!!) {
                        film.filmId?.let { it1 ->
                            getFavouriteFilm(it1)
                                .subscribeOn(Schedulers.io()).subscribe({
                                    film.isFavourite = true
                                }, {})
                        }
                    }
                }
                page++
            }, {
                it.message?.let { it1 -> Log.d(TAG, it1) }
            })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkIfOnline(context: Context): LiveData<Boolean> {
        val isOnline = MutableLiveData<Boolean>()
        isOnline.value = true
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return isOnline
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return isOnline
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return isOnline
            }
        }
        isOnline.value = false
        return isOnline
    }
}