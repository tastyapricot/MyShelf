package com.android.myshelf.data.mappers

import android.content.Context
import com.android.myshelf.data.local.dbos.DefaultGenre
import com.android.myshelf.data.local.dbos.Genre

fun DefaultGenre.toGenre(context: Context): Genre {
    return Genre(
        id = id,
        name = context.getString(nameRes),
    )
}