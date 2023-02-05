package com.example.popularmovies.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.ToggleButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.popularmovies.R
import com.example.popularmovies.pojo.Film

class FilmListActivity : AppCompatActivity() {

    private lateinit var viewModel: FilmsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_list)
        supportActionBar?.title = getString(R.string.popular)
        supportActionBar?.setTitleColor(Color.BLACK)
        supportActionBar?.elevation = 0f
        val adapter = FilmInfoAdapter()
        val rvFilms = findViewById<RecyclerView>(R.id.rvFilms)
        val pbLoading = findViewById<ProgressBar>(R.id.pbLoading)
        val tbFavourite = findViewById<ToggleButton>(R.id.tbFavourite)
        rvFilms.adapter = adapter
        viewModel = ViewModelProvider(this)[FilmsViewModel::class.java]
        viewModel.loadData()
        viewModel.filmList.observe(this) {
            adapter.filmInfoList = it
        }
        adapter.onReachEndListener = object : FilmInfoAdapter.OnReachEndListener {
            override fun onReachEnd() {
                viewModel.loadData()
            }
        }
        adapter.onClickListener = object : FilmInfoAdapter.OnFilmClickListener {
            override fun onFilmClick(film: Film) {
                val intent = film.filmId?.let {
                    FilmDetailActivity.newIntent(
                        this@FilmListActivity,
                        it
                    )
                }
                startActivity(intent)
            }
        }
        adapter.onFilmLongClickListener = object : FilmInfoAdapter.OnFilmLongClickListener {
            override fun onFilmLongClick(film: Film) {
                if (!film.isFavourite) {
                    viewModel.addFilmToFavourite(film)
                } else {
                    film.filmId?.let { viewModel.removeFilmFromFavourite(it) }
                }
            }

        }
        viewModel.isLoading.observe(this) {
            if (it) {
                pbLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.GONE
            }
        }

        tbFavourite.setOnClickListener {
            val intent = FavouriteFilmsListActivity.newIntent(this@FilmListActivity)
            startActivity(intent)
            finish()
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }


    private fun ActionBar.setTitleColor(color: Int) {
        val text = SpannableString(title ?: "")
        text.setSpan(ForegroundColorSpan(color),0,text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        title = text
    }

    companion object {
        const val TAG = "FilmListActivity"

        fun newIntent(context: Context): Intent {
            return Intent(context, FilmListActivity::class.java)
        }
    }

}