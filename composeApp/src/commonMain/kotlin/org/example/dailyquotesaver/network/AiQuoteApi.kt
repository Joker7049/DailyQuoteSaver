package org.example.dailyquotesaver.network

// In file: network/AiQuoteApi.kt


/**
 * Defines the contract for an API that can generate a quote based on a user's mood.
 * Using an interface allows us to easily swap out the implementation later
 * (e.g., from Gemini to OpenAI, or to a fake version for testing).
 */
interface AiQuoteApi {
    /**
     * Generates an inspiring quote for a given mood.
     * @param mood A string describing the user's current mood.
     * @return A string containing the generated quote.
     * @throws Exception if the API call fails or a quote cannot be generated.
     */
    suspend fun generateQuote(mood: String): String
}