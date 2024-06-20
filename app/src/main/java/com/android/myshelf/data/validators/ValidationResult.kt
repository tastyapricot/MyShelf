package com.android.myshelf.data.validators

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null,
)