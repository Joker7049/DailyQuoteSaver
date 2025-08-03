package org.example.dailyquotesaver.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.dailyquotesaver.BuildKonfig
import org.example.dailyquotesaver.network.dto.Content
import org.example.dailyquotesaver.network.dto.GenerateContentRequest
import org.example.dailyquotesaver.network.dto.GenerateContentResponse
import org.example.dailyquotesaver.network.dto.GenerationConfig
import org.example.dailyquotesaver.network.dto.Part

/**
 * AIQuoteService: send Gemini prompts and fetch generated text.
 *
 * @param apiKey Your secret Gemini/Generative Language API key.
 * @param modelName Which Gemini model you want (e.g. "gemini-2.5-pro").
 * @param location Your Cloud project location (defaults to "us-central1").
 */
class GeminiQuoteService(
    private val client: HttpClient, // Primary dependency first
    private val modelName: String = "gemini-2.5-pro"
): AiQuoteApi{



    private val apiKey = BuildKonfig.GEMINI_API_KEY
    private val endpointBase =
        "https://generativelanguage.googleapis.com/v1beta1/models"



    /**
     * Send prompt to Gemini, return the first generated answer or throw an error.
     */
    override suspend fun generateQuote(mood: String): String {
        // Build request body
        val promptText = "Generate a short, inspiring quote for someone who is feeling: $mood"
        val part = Part(text = promptText)
        val content = Content(parts = listOf(part))
        val request = GenerateContentRequest(
            contents = content,
            generationConfig = GenerationConfig(
                temperature = 0.7,
                candidateCount = 1,
                maxOutputTokens = 150
            )
        )

        // URL is "models/{modelName}:generateContent"
        val url = "$endpointBase/models/$modelName:generateContent"


        val resp = client.post(url) {
            url {
                parameters.append("key", apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<GenerateContentResponse>()

        // Error handling
        resp.error?.let {
            throw Exception("Gemini API error: ${it.message}")
        }

        val candidates = resp.candidates
        if (candidates.isNullOrEmpty()) {
            throw Exception("Gemini returned no candidates")
        }

        // Take the first candidate's first text
        val text = candidates.first().content.parts.firstOrNull()?.text
        return text ?: throw Exception("Failed to extract Gemini answer")
    }

    fun close() {
        client.close()
    }
}
