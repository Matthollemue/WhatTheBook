package com.example.bookmeup.model

import kotlinx.serialization.Serializable

// This class represents the response from the Google Books API.
@Serializable
data class ApiResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<Item>
)

// This class represents an item (Book) in the response from the Google Books API.
@Serializable
data class Item(
    val id: String,
    val volumeInfo: VolumeInfo
)

// This class represents the volume information for an item in the response from the Google Books API.
@Serializable
data class VolumeInfo(
    val title: String = "Title not available",
    val authors: List<String> = listOf("Author not available"),
    val publishedDate: String = "Publication date not available",
    val description: String = "Description not available",
    val imageLinks: Map<String,String> = emptyMap()
)




