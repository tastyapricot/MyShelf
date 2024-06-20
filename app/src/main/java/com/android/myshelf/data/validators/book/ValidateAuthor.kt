package com.android.myshelf.data.validators.book

import android.content.Context
import com.android.myshelf.data.validators.ValidationResult
import com.android.myshelf.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ValidateAuthor
@Inject
constructor(
    @ApplicationContext private val context: Context,
) {

    operator fun invoke(author: String): ValidationResult {
        if (author.isBlank()) {
            return ValidationResult(false, context.getString(R.string.book_author_empty_error))
        }

        return ValidationResult(true)
    }
}