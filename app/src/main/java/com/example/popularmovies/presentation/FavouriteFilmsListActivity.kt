package com.example.popularmovies.presentation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.popularmovies.R
import com.example.popularmovies.data.pojo.Film
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class FavouriteFilmsListActivity : AppCompatActivity() {

    private lateinit var viewModel: FilmsViewModel
    private lateinit var rvFilms : RecyclerView
    private lateinit var pbLoading : ProgressBar
    private lateinit var tbPopular : ToggleButton
    private lateinit var adapter: FilmInfoAdapter
    private lateinit var tvDescription : TextView
    private var orientation : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_films_list)
        orientation = resources.configuration.orientation
        setupActionBar()
        initViews()
        adapter = setupAdapter()
        setupViewModel()

        setupViewListeners()

        setupAdapterSubscriptions()

    }

    private fun setupAdapterSubscriptions() {
        adapter.onClickListener = object : FilmInfoAdapter.OnFilmClickListener {
            override fun onFilmClick(film: Film) {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    val intent = film.filmId?.let {
                        FilmDetailActivity.newIntent(
                            this@FavouriteFilmsListActivity,
                            it,
                            modeFromWeb = false
                        )
                    }
                    startActivity(intent)
                } else {
                    film.filmId?.let {
                        viewModel.getDetailInfoFromDB(it).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                tvDescription = findViewById<TextView>(R.id.tvDescription)
                                tvDescription.text = it.description
                            }, {})
                    }
                }
            }
        }
        adapter.onFilmLongClickListener = object : FilmInfoAdapter.OnFilmLongClickListener {
            override fun onFilmLongClick(film: Film) {
                film.filmId?.let {
                    viewModel.removeFilmFromFavourite(it).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it > 0) {
                                viewModel.getFilmListFromDB()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        adapter.filmInfoList = it
                                    }, {})
                            }
                        }, {})
                }


            }

        }
    }

    private fun setupViewListeners() {
        tbPopular.setOnClickListener {
            val intent = FilmListActivity.newIntent(this@FavouriteFilmsListActivity)
            startActivity(intent)
            finish()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[FilmsViewModel::class.java]
        viewModel.getFilmListFromDB()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                adapter.filmInfoList = it
            }, {})
    }

    private fun setupAdapter(): FilmInfoAdapter {
        val adapter = FilmInfoAdapter()
        rvFilms.adapter = adapter
        return adapter
    }

    private fun setupActionBar() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.title = getString(R.string.favourite)
        supportActionBar?.setTitleColor(Color.BLACK)
        supportActionBar?.elevation = 0f
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportActionBar?.hide()
        }
    }

    private fun initViews(){
        rvFilms = findViewById<RecyclerView>(R.id.rvFilms)
        pbLoading = findViewById<ProgressBar>(R.id.pbLoading)
        tbPopular = findViewById<ToggleButton>(R.id.tbPopular)
    }

    private fun ActionBar.setTitleColor(color: Int) {
        val text = SpannableString(title ?: "")
        text.setSpan(ForegroundColorSpan(color),0,text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        title = text
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, FavouriteFilmsListActivity::class.java)
        }
    }
}