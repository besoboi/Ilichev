package com.example.popularmovies.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.popularmovies.data.pojo.Film
import com.example.popularmovies.data.pojo.FilmDetailedInfo

@Database(entities = [Film::class, FilmDetailedInfo::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FilmDatabase : RoomDatabase() {
    companion object {
        private var db: FilmDatabase? = null
        private const val DB_NAME = "favourite_movies.db"
        private val monitor = Any()

        fun getInstance(context: Context): FilmDatabase {
            synchronized(monitor) {
                db?.let { return it }
                val instance = Room.databaseBuilder(
                    context,
                    FilmDatabase::class.java,
                    DB_NAME
                )
                    .build()
                db = instance
                return instance
            }
        }
    }

    abstract fun filmInfoDao() : FilmInfoDao
}
