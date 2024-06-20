package com.android.myshelf.presentation.shared.models

import javax.annotation.concurrent.Immutable

@Immutable
data class TextFieldState(
    val text: String = "",
    val error: String? = null,
)