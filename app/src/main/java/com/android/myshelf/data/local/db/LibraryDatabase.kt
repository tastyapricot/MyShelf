package com.android.myshelf.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.myshelf.data.local.dbos.Book
import com.android.myshelf.data.local.dbos.DefaultGenre
import com.android.myshelf.data.local.dbos.Genre
import com.android.myshelf.data.mappers.toGenre
import com.android.myshelf.data.utils.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Book::class, Genre::class], version = 1, exportSchema = false)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun genreDao(): GenreDao

    companion object {
        const val BOOKS_TABLE_NAME = "books"
        const val GENRES_TABLE_NAME = "genres"
        private const val DB_NAME = "library.db"
        private var INSTANCE: LibraryDatabase? = null
        private val LOCK = Any()

        fun getInstance(
            context: Context,
            appDispatchers: AppDispatchers,
        ): LibraryDatabase {
            INSTANCE?.let { return it }

            synchronized(LOCK) {
                INSTANCE?.let { return it }

                val callback = object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        CoroutineScope(appDispatchers.io).launch {
                            insertDefaultGenres(context, db)
                        }
                    }

                }

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibraryDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        private suspend fun insertDefaultGenres(
            context: Context,
            db: SupportSQLiteDatabase,
        ) {
            DefaultGenre.DEFAULT_GENRES
                .map { it.toGenre(context) }
                .forEach {
                    db.insert(
                        GENRES_TABLE_NAME,
                        SQLiteDatabase.CONFLICT_REPLACE,
                        it.getContentValues()
                    )
                }
        }
    }
}