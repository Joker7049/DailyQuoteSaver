package org.example.dailyquotesaver.network.dto




import kotlinx.serialization.Serializable

/**
 * A single “part” of content (Gemini usually treats
 * each text block as a part). We only use the `text` case.
 */
@Serializable
data class Part(val text: String)

/**
 * A message/message chunk to send to Gemini.
 * Follow John O’Reilly’s example and omit role;
 * Gemini defaults to treating it as user‑content.
 * GenerationConfig is optional; shown here for tuning.
 */
@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class GenerationConfig(
    val temperature: Double? = null,
    val topP: Double? = null,
    val candidateCount: Int? = null,
    val maxOutputTokens: Int? = null
)

/**
 * The request envelope. `contents` can also be a List<Content>,
 * but using an overloaded single is OK per existing samples.
 */
@Serializable
data class GenerateContentRequest(
    val contents: Content,
    val generationConfig: GenerationConfig? = null
)

/**
 * Response container, per Gemini REST API.
 */
@Serializable
data class Candidate(val content: Content)

/**
 * Error in the response (will have an `error.message`),
 * or list of successful candidates.
 */
@Serializable
data class ErrorModel(val message: String)

@Serializable
data class GenerateContentResponse(
    val error: ErrorModel? = null,
    val candidates: List<Candidate>? = null
)
