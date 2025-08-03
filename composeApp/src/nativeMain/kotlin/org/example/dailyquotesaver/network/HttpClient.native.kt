package org.example.dailyquotesaver.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createHttpClient(): HttpClient {
    return HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true       // Makes logs easier to read.
                isLenient = true         // Forgives minor formatting errors from the server.
                ignoreUnknownKeys = true // Prevents crashes if the API adds new fields.
                explicitNulls = false    // Saves bandwidth by not sending "key": null.
            })
        }
    }
}