package com.example.popularmovies.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.popularmovies.R
import com.example.popularmovies.pojo.Film
import java.util.*

class FilmInfoAdapter : RecyclerView.Adapter<FilmInfoAdapter.FilmInfoViewHolder>() {

    var filmInfoList : List<Film> = listOf()
    set(value) {
        val callback = FilmListDiffCallback(filmInfoList, value)
        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
        field = value
    }

    var onClickListener : OnFilmClickListener? = null
    var onReachEndListener : OnReachEndListener? = null
    var onFilmLongClickListener : OnFilmLongClickListener? = null

    inner class FilmInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivMiniPoster = itemView.findViewById<ImageView>(R.id.ivMiniPoster)
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvGenre = itemView.findViewById<TextView>(R.id.tvGenre)
        val vStar = itemView.findViewById<View>(R.id.vStar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return FilmInfoViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FilmInfoViewHolder, position: Int) {
        val film = filmInfoList[position]
        holder.tvTitle.text = film.nameRu
        var genreString = film.genres
            ?.map { it.genre }
            ?.filter { it != "" }
            ?.joinToString(separator = ", ")
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        if (film.year != null && film.year != "") genreString += " (${film.year})"
        holder.tvGenre.text = genreString
        Glide.with(holder.itemView)
            .load(film.posterUrlPreview)
            .into(holder.ivMiniPoster)

        if (film.isFavourite) {
            holder.vStar.visibility = View.VISIBLE
        } else {
            holder.vStar.visibility = View.GONE
        }

        if (position >= filmInfoList.size - 5) {
            onReachEndListener?.onReachEnd()
        }
        holder.itemView.setOnClickListener{
            onClickListener?.onFilmClick(film)
        }
        holder.itemView.setOnLongClickListener {
            onFilmLongClickListener?.onFilmLongClick(film)
            film.isFavourite = !film.isFavourite
            if (film.isFavourite) {
                holder.vStar.visibility = View.VISIBLE
            } else {
                holder.vStar.visibility = View.GONE
            }
            true
        }
    }

    interface OnReachEndListener {
        fun onReachEnd()
    }

    interface OnFilmClickListener {
        fun onFilmClick(film: Film)
    }

    interface OnFilmLongClickListener {
        fun onFilmLongClick(film: Film)
    }

    override fun getItemCount() = filmInfoList.size
}