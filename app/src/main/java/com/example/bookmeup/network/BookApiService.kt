package com.example.bookmeup.network


import com.example.bookmeup.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface BookApiService {
    /**
     * The @GET annotation indicates that the "volumes" endpoint will be requested with the GET
     * HTTP method
     */

    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String
    ): ApiResponse
}