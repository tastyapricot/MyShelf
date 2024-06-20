package com.android.myshelf.di

import android.content.Context
import com.android.myshelf.data.local.db.BookDao
import com.android.myshelf.data.local.db.GenreDao
import com.android.myshelf.data.local.db.LibraryDatabase
import com.android.myshelf.data.utils.AppDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object {

        @[Singleton Provides]
        fun provideAppDispatchers(): AppDispatchers {
            return AppDispatchers()
        }

        @[Singleton Provides]
        fun provideBookDatabase(
            @ApplicationContext context: Context,
            appDispatchers: AppDispatchers,
        ): LibraryDatabase {
            return LibraryDatabase.getInstance(context, appDispatchers)
        }

        @[Singleton Provides]
        fun provideBookDao(libraryDatabase: LibraryDatabase): BookDao {
            return libraryDatabase.bookDao()
        }

        @[Singleton Provides]
        fun provideGenreDao(libraryDatabase: LibraryDatabase): GenreDao {
            return libraryDatabase.genreDao()
        }
    }
}