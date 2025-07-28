package org.example.dailyquotesaver

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform