package com.example.popularmovies.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ProgressBar
import android.widget.ToggleButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.popularmovies.R

class FavouriteFilmsListActivity : AppCompatActivity() {

    private lateinit var viewModel: FilmsViewModel
    private lateinit var rvFilms : RecyclerView
    private lateinit var pbLoading : ProgressBar
    private lateinit var tbPopular : ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_films_list)
        supportActionBar?.title = getString(R.string.favourite)
        supportActionBar?.setTitleColor(Color.BLACK)
        supportActionBar?.elevation = 0f
        initViews()
        val adapter = FilmInfoAdapter()
        rvFilms.adapter = adapter
        viewModel = ViewModelProvider(this)[FilmsViewModel::class.java]
        viewModel.getFilmListFromDB().observe(this) {
            adapter.filmInfoList = it
        }

        tbPopular.setOnClickListener{
            val intent = FilmListActivity.newIntent(this@FavouriteFilmsListActivity)
            startActivity(intent)
            finish()
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
        private const val TAG = "FavouriteFilmsListActivity"

        fun newIntent(context: Context): Intent {
            return Intent(context, FavouriteFilmsListActivity::class.java)
        }
    }
}