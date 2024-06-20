package com.android.myshelf.data.validators.book

import android.content.Context
import com.android.myshelf.R
import com.android.myshelf.data.validators.ValidationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ValidateTitle
@Inject
constructor(
    @ApplicationContext private val context: Context,
) {

    operator fun invoke(title: String): ValidationResult {
        if (title.isBlank()) {
            return ValidationResult(false, context.getString(R.string.book_title_empty_error))
        }

        return ValidationResult(true)
    }
}