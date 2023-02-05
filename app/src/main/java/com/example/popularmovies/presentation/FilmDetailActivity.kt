package com.example.popularmovies.presentation

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.popularmovies.R
import com.example.popularmovies.pojo.FilmDetailedInfo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*


class FilmDetailActivity : AppCompatActivity() {
    private lateinit var ivPoster: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvGenres: TextView
    private lateinit var tvCountries: TextView
    private lateinit var filmsViewModel: FilmsViewModel
    private lateinit var pbLoading: ProgressBar
    private lateinit var buttonArrow: ImageButton
    private lateinit var ivCloud: ImageView
    private lateinit var tbRepeat: ToggleButton
    private lateinit var tvNoNet: TextView
    private lateinit var tvGenresTitle: TextView
    private lateinit var tvCountriesTitle: TextView

    private var id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_detail)
        initViews()
        setupActionBar()

        if (!intent.hasExtra(EXTRA_ID) || !intent.hasExtra(EXTRA_MODE)) {
            finish()
            return
        }

        id = intent.getIntExtra(EXTRA_ID, 1)
        if (id < 0) {
            finish()
            return
        }
        val mode = intent.getBooleanExtra(EXTRA_MODE, false)

        filmsViewModel = ViewModelProvider(this)[FilmsViewModel::class.java]
        getData(mode, id)
        setupViewListeners()

    }

    private fun setupActionBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val decorView = this.window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decorView.windowInsetsController?.setSystemBarsAppearance(
                0,
                APPEARANCE_LIGHT_STATUS_BARS
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewListeners() {
        buttonArrow.setOnClickListener {
            finish()
        }

        tbRepeat.setOnClickListener() {
            getData(true, id)
        }
    }

    private fun getData(mode: Boolean, id: Int) {
        if (mode) {
            filmsViewModel.getDetailInfoFromWeb(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showOnLineViews()
                    showData(it)
                }, {
                    showOfflineViews()
                })
        } else {
            filmsViewModel.getDetailInfoFromDB(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showData(it)
                }, {
                    it.message?.let { it1 -> Log.d(TAG, it1) }
                })
        }
    }

    private fun showData(filmDetailedInfo: FilmDetailedInfo) {
        tvTitle.text = filmDetailedInfo.nameRu
        tvDescription.text = filmDetailedInfo.description
        tvGenres.text = filmDetailedInfo.genres
            ?.map { it.genre }
            ?.filter { it != "" }
            ?.joinToString(separator = ", ")
            ?.lowercase()
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        tvCountries.text = filmDetailedInfo.countries
            ?.map { it.country }
            ?.filter { it != "" }
            ?.joinToString(separator = ", ")
        Glide.with(this)
            .load(filmDetailedInfo.posterUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    pbLoading.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    pbLoading.visibility = View.GONE
                    return false
                }
            })
            .into(ivPoster)
    }

    private fun showOfflineViews() {
        ivCloud.visibility = View.VISIBLE
        tvNoNet.visibility = View.VISIBLE
        tbRepeat.visibility = View.VISIBLE
        ivPoster.visibility = View.GONE
        tvTitle.visibility = View.GONE
        tvDescription.visibility = View.GONE
        tvGenres.visibility = View.GONE
        tvCountries.visibility = View.GONE
        pbLoading.visibility = View.GONE
        tvGenresTitle.visibility = View.GONE
        tvCountriesTitle.visibility = View.GONE
    }

    private fun showOnLineViews() {
        ivCloud.visibility = View.GONE
        tvNoNet.visibility = View.GONE
        tbRepeat.visibility = View.GONE
        ivPoster.visibility = View.VISIBLE
        tvTitle.visibility = View.VISIBLE
        tvDescription.visibility = View.VISIBLE
        tvGenres.visibility = View.VISIBLE
        tvCountries.visibility = View.VISIBLE
        pbLoading.visibility = View.VISIBLE
        tvGenresTitle.visibility = View.VISIBLE
        tvCountriesTitle.visibility = View.VISIBLE
    }

    private fun initViews() {
        ivPoster = findViewById(R.id.ivPoster)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        tvGenres = findViewById(R.id.tvGenres)
        tvCountries = findViewById(R.id.tvCountries)
        pbLoading = findViewById(R.id.pbLoadingDetails)
        buttonArrow = findViewById<ImageButton>(R.id.buttonArrow)
        ivCloud = findViewById<ImageView>(R.id.ivCloud)
        tvNoNet = findViewById<TextView>(R.id.tvNoNet)
        tbRepeat = findViewById<ToggleButton>(R.id.tbRepeat)
        tvGenresTitle = findViewById(R.id.tvGenresTitle)
        tvCountriesTitle = findViewById(R.id.tvCountriesTitle)
    }

    companion object {
        private const val EXTRA_MODE = "mode"
        private const val EXTRA_ID = "id"
        private const val TAG = "FilmDetailActivity"

        fun newIntent(context: Context, id: Int, modeFromWeb: Boolean): Intent {
            val intent = Intent(context, FilmDetailActivity::class.java)
            intent.putExtra(EXTRA_ID, id)
            intent.putExtra(EXTRA_MODE, modeFromWeb)
            return intent
        }
    }


}