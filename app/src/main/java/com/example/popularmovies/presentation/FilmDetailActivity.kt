package com.example.popularmovies.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.popularmovies.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class FilmDetailActivity : AppCompatActivity() {
    lateinit var ivPoster : ImageView
    lateinit var tvTitle : TextView
    lateinit var tvDescription : TextView
    lateinit var tvGenres : TextView
    lateinit var tvCountries : TextView
    lateinit var filmsViewModel: FilmsViewModel
    lateinit var pbLoading : ProgressBar
    lateinit var buttonArrow : ImageButton

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_detail)
        initView()
        if (!intent.hasExtra(EXTRA_ID)) {
            finish()
            return
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val decorView = this.window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decorView.windowInsetsController?.setSystemBarsAppearance(
                0,
                APPEARANCE_LIGHT_STATUS_BARS
            )
       }


        supportActionBar?.hide()
        val id = intent.getIntExtra(EXTRA_ID, 1)
        filmsViewModel = ViewModelProvider(this)[FilmsViewModel::class.java]
        filmsViewModel.getDetailInfoFromWeb(id).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                tvTitle.text = it.nameRu
                tvDescription.text = it.description
                tvGenres.text = it.genres
                    ?.map { it.genre }
                    ?.filter { it != "" }
                    ?.joinToString(separator = ", ")
                    ?.lowercase()
                    ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                tvCountries.text = it.countries
                    ?.map { it.country }
                    ?.filter { it != "" }
                    ?.joinToString(separator = ", ")
                Glide.with(this)
                    .load(it.posterUrl)
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

            },{
                it.message?.let { it1 -> Log.d(TAG, it1) }
            })

        buttonArrow.setOnClickListener {
            finish()
        }

    }

    private fun initView(){
        ivPoster = findViewById(R.id.ivPoster)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        tvGenres = findViewById(R.id.tvGenres)
        tvCountries = findViewById(R.id.tvCountries)
        pbLoading = findViewById(R.id.pbLoadingDetails)
        buttonArrow = findViewById<ImageButton>(R.id.buttonArrow)
    }

    companion object {
        private const val EXTRA_ID = "id"
        private const val TAG = "FilmDetailActivity"

        fun newIntent(context: Context ,id : Int) : Intent{
            val intent = Intent(context, FilmDetailActivity::class.java)
            intent.putExtra(EXTRA_ID, id)
            return intent
        }
    }


}