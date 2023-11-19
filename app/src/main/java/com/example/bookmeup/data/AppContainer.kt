package com.example.bookmeup.data

import com.example.bookmeup.network.BookApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit


/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val booksRepository: BooksRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://www.googleapis.com/books/v1/"

    /**
     *  Value to ignore unknown json keys - Extremely important when dealing with json where you
     *  don't need all the data from it.
     */
    private val json = Json {
        ignoreUnknownKeys = true
    }
    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }

    /**
     * DI implementation for Books repository
     */
    override val booksRepository: BooksRepository by lazy {
        NetworkBooksRepository(retrofitService)
    }
}
