package org.example.dailyquotesaver

import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable // This is crusial for saving the object to DataStore
data class Quote(
    val id : Long = Random.nextLong(), // a simple unique ID. A UUID is also a great option
    val text: String,
    val author: String? = null,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList<String>()
)
