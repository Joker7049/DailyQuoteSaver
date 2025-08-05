// In file: composeApp/src/commonMain/kotlin/org/example/dailyquotesaver/network/GeminiQuoteService.kt

package org.example.dailyquotesaver.network


import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import org.example.dailyquotesaver.BuildKonfig

// We no longer need Ktor, so we can remove those imports.
// We also no longer need the DTO imports.




class GeminiQuoteService(
    // We can still allow the model name to be passed in, for flexibility.
    private val modelName: String = "gemini-2.0-flash" // Let's use a standard, reliable model name to start.
) : AiQuoteApi {

    // 1. We create the GenerativeModel instance right here in the class.
    //    It's configured with the model name and the API key from BuildKonfig.
    private val generativeModel = GenerativeModel(
        modelName = modelName,
        apiKey = BuildKonfig.GEMINI_API_KEY
    )




    override suspend fun generateQuote(mood: String): String {
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
