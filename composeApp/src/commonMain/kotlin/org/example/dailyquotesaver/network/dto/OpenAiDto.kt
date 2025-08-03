package org.example.dailyquotesaver.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAiRequest(
    val model: String,
    val messages: List<ChatMessage>,
    @SerialName("max_tokens") val maxTokens: Int,
    val temperature: Double
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)


@Serializable
data class OpenAiResponse(
    val choices: List<Choice>,
)

@Serializable
data class Choice(
    val message: ChatMessage,
)


