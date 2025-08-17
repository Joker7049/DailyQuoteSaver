// In file: composeApp/src/commonMain/kotlin/org/example/dailyquotesaver/network/GeminiQuoteService.kt

package org.example.dailyquotesaver.network


import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.first
import org.example.dailyquotesaver.data.ApiKeyRepository

// We no longer need Ktor, so we can remove those imports.
// We also no longer need the DTO imports.


class GeminiQuoteService(
    private val apiKeyRepository: ApiKeyRepository,
    // We can still allow the model name to be passed in, for flexibility.
    private val modelName: String = "gemini-1.5-flash" // Let's use a standard, reliable model name to start.
) : AiQuoteApi {

    // We no longer create the GenerativeModel instance here.
    // We will create it inside the generateQuote function, once we have the key.

    override suspend fun generateQuote(mood: String): String {
        val apiKey = apiKeyRepository.getApiKey()
            ?: throw ApiKeyNotFoundException("API Key not found. Please add it in settings.")

        val generativeModel = GenerativeModel(
            modelName = modelName,
            apiKey = apiKey
        )

        // 2. We build the prompt string, just like before.
        val prompt = "Generate a short, inspiring quote for someone who is feeling: $mood"

        try {
            // 3. We call the SDK's generateContent function with our prompt.
            val response = generativeModel.generateContent(prompt)

            // 4. We safely get the text from the response. If it's null or blank,
            //    we'll throw an exception.
            return response.text ?: throw Exception("Gemini returned a null or empty response.")
        } catch (e: Exception) {
            // 5. If anything goes wrong (network error, API key issue, etc.),
            //    we'll catch the exception and re-throw it with a more informative message.
            //    The original exception `e` will contain the detailed error from the SDK.
            throw Exception("Failed to generate quote from Gemini: ${e.message}", e)
        }
    }
}
