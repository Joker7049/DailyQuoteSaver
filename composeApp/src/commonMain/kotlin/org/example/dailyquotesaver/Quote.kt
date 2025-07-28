package org.example.dailyquotesaver

import kotlinx.serialization.Serializable


@Serializable
data class Quote(
    val text: String,
    val author: String? = null
)
