package com.example.popularmovies.presentation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.popularmovies.R
import com.example.popularmovies.data.pojo.Film
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class FilmListActivity : AppCompatActivity() {

    private lateinit var viewModel: FilmsViewModel
    private lateinit var rvFilms: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var tbFavourite: ToggleButton
    private lateinit var adapter: FilmInfoAdapter
    private lateinit var ivCloud : ImageView
    private lateinit var tbRepeat : ToggleButton
    private lateinit var tvNoNet : TextView
    private lateinit var tvDescription : TextView
    private var orientation : Int = -1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_list)
        orientation = resources.configuration.orientation
        setupActionBar()

        initViews()
        setupAdapter()
        viewModel = ViewModelProvider(this)[FilmsViewModel::class.java]

        setupSubscriptions()
        setupViewListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }


    private fun ActionBar.setTitleColor(color: Int) {
        val text = SpannableString(title ?: "")
        text.setSpan(ForegroundColorSpan(color), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        title = text
    }

    private fun setupAdapterSubscriptions() {
        adapter.onReachEndListener = object : FilmInfoAdapter.OnReachEndListener {
            override fun onReachEnd() {
                viewModel.loadData()
            }
        }
        adapter.onClickListener = object : FilmInfoAdapter.OnFilmClickListener {
            override fun onFilmClick(film: Film) {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    val intent = film.filmId?.let {
                        FilmDetailActivity.newIntent(
                            this@FilmListActivity,
                            it,
                            modeFromWeb = true
                        )
                    }
                    startActivity(intent)
                } else {
                    film.filmId?.let {
                        viewModel.getDetailInfoFromWeb(it).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                tvDescription = findViewById<TextView>(R.id.tvDescription)
                                tvDescription.text = it.description
                            },{})
                    }
                }
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
    }

    private fun setupViewModelSubscriptions() {
        viewModel.loadData()
        viewModel.filmList.observe(this) {
            adapter.filmInfoList = it
        }
        viewModel.isLoading.observe(this) {
            if (it) {
                pbLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.GONE
            }
        }
    }

    private fun initViews() {
        rvFilms = findViewById<RecyclerView>(R.id.rvFilms)
        pbLoading = findViewById<ProgressBar>(R.id.pbLoading)
        tbFavourite = findViewById<ToggleButton>(R.id.tbFavourite)
        ivCloud = findViewById<ImageView>(R.id.ivCloud)
        tvNoNet = findViewById<TextView>(R.id.tvNoNet)
        tbRepeat = findViewById<ToggleButton>(R.id.tbRepeat)

    }

    private fun setupAdapter() {
        adapter = FilmInfoAdapter()
        rvFilms.adapter = adapter
    }

    private fun setupActionBar() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.title = getString(R.string.popular)
        supportActionBar?.setTitleColor(Color.BLACK)
        supportActionBar?.elevation = 0f
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportActionBar?.hide()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupViewListeners() {
        tbFavourite.setOnClickListener {
            val intent = FavouriteFilmsListActivity.newIntent(this@FilmListActivity)
            startActivity(intent)
            finish()
        }

        tbRepeat.setOnClickListener() {
            setupSubscriptions()
        }
    }

    private fun showOfflineViews(){
        rvFilms.visibility = View.GONE
        ivCloud.visibility = View.VISIBLE
        tvNoNet.visibility = View.VISIBLE
        tbRepeat.visibility = View.VISIBLE
    }

    private fun showOnLineViews(){
        rvFilms.visibility = View.VISIBLE
        ivCloud.visibility = View.GONE
        tvNoNet.visibility = View.GONE
        tbRepeat.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupSubscriptions(){
        viewModel.checkIfOnline(this)
            .observe(this){
                if (it) {
                    setupViewModelSubscriptions()
                    setupAdapterSubscriptions()
                    showOnLineViews()
                } else {
                    showOfflineViews()
                }
            }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, FilmListActivity::class.java)
        }
    }

}