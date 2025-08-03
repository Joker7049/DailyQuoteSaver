// in org/example.dailyquotesaver/network/AIQuoteService.kt

package org.example.dailyquotesaver.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import org.example.dailyquotesaver.BuildKonfig
import org.example.dailyquotesaver.network.dto.ChatMessage
import org.example.dailyquotesaver.network.dto.OpenAiRequest
import org.example.dailyquotesaver.network.dto.OpenAiResponse

class AIQuoteService {

    // Create an HttpClient instance using the expect/actual function we defined.
    private val client: HttpClient = createHttpClient()

    private val apiKey = BuildKonfig.OPENAI_API_KEY
    private val apiUrl = "https://api.openai.com/v1/chat/completions"

    /*
     * TASK: Implement the `generateQuote` function.
     *
     * This function will take a user's prompt (like "anxiety" or "motivation"),
     * construct the request, send it, and then parse the response to return
     * just the quote string.
     *
     * It's a `suspend` function because network calls are asynchronous.
     */
    suspend fun generateQuote(prompt: String): String {
        try {
            val httpResponse = client.post(apiUrl) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")

                setBody(
                    OpenAiRequest(
                        model = "o4-mini",
                        messages = listOf(
                            ChatMessage(
                                role = "user",
                                content = "Generate a short, unique, and inspiring quote about: $prompt"
                            )
                        ),
                        maxTokens = 40,
                        temperature = 0.8
                    )
                )
            }
            val responseBodyAsString = httpResponse.bodyAsText()
            println("AIQuoteService RAW RESPONSE BODY: $responseBodyAsString")
            // Now, try to decode the string we just received.
            val openAiResponse: OpenAiResponse = Json.decodeFromString(responseBodyAsString)



            //val openAiResponse = httpResponse.body<OpenAiResponse>()

            val quote = openAiResponse.choices.firstOrNull()?.message?.content
                ?: return "Sorry, I couldn't generate a quote right now."
            return quote

        } catch (e: ClientRequestException) {
            // THIS is the specific exception for 4xx/5xx errors.
            val errorBody = e.response.bodyAsText()
            println("AIQuoteService Client Error: ${e.message}")
            println("AIQuoteService Error Body: $errorBody")
            return "Sorry, there was an API error. Please check the logs."
        } catch (e: Exception) {
            // This is a general catch-all for other errors (network down, etc.)
            println("AIQuoteService Generic Error Type: ${e::class.simpleName}")
            println("AIQuoteService Generic Error: ${e.message}")
            return "Sorry, there was a network error. Please try again."
        }
    }
}