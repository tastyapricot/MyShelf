package com.android.myshelf.data.validators.new_book

import com.android.myshelf.data.local.dbos.Genre
import com.android.myshelf.data.validators.ValidationResult
import javax.inject.Inject

class ValidateGenre
@Inject
constructor() {

    operator fun invoke(genre: Genre?): ValidationResult {
        return ValidationResult(true)
    }
}