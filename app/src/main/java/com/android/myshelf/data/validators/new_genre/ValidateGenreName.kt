package com.android.myshelf.data.validators.new_genre

import android.content.Context
import com.android.myshelf.R
import com.android.myshelf.data.validators.ValidationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ValidateGenreName
@Inject
constructor(
    @ApplicationContext private val context: Context,
) {

    operator fun invoke(genreName: String): ValidationResult {
        if (genreName.isBlank()) {
            return ValidationResult(false, context.getString(R.string.genre_name_empty_error))
        }

        return ValidationResult(true)
    }
}