package com.example.bookmeup.data

import com.example.bookmeup.model.ApiResponse
import com.example.bookmeup.network.BookApiService

interface BooksRepository {
    /** Fetches list of Book from bookApi */
    suspend fun getBooks(query: String, searchType: String): ApiResponse
}

/**
 * Network Implementation of Repository that fetch books list from bookApi using q=SearchType:query
 */
class NetworkBooksRepository(
    private val bookApiService: BookApiService
) : BooksRepository {
    override suspend fun getBooks(query: String, searchType: String): ApiResponse = bookApiService.getBooks("$searchType:$query")
}

