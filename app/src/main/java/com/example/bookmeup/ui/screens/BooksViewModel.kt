package com.example.bookmeup.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookmeup.BooksApplication
import com.example.bookmeup.data.BooksRepository
import com.example.bookmeup.model.ApiResponse
import com.example.bookmeup.model.Item
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException


sealed interface BooksUiState {
    data class Success(val items: ApiResponse) : BooksUiState
    object Error : BooksUiState
    object Loading : BooksUiState

}

/** This enum represents the different search types that can be used to search for books.*/
enum class SearchType {
    intitle,
    inauthor,
    inpublisher,
    isbn
}

/** This class represents the ViewModel for the books screen. */
class BooksViewModel(private val booksRepository: BooksRepository) : ViewModel() {
    /** The mutable State that stores the status of the most recent query request */
    var userInput: String by mutableStateOf("")
        private set

    /** The mutable State that stores the status of the most recent books request */
    var booksUiState: BooksUiState by mutableStateOf(BooksUiState.Loading)
        private set

    /** The mutable State that stores the status of the most recent book request search type */
    var searchType: SearchType by mutableStateOf(SearchType.intitle)
        private set


    /**
     * Call getBooks() on init so we can display status immediately.
     */
    init {
        getBooks()
    }

    /**
     * Updates user input
     */
    fun updateUserInput(input: String) {
        userInput = input
    }

    /**
     * Clears user input
     */
    fun clearUserInput() {
        userInput = ""
    }

    fun updateSearchType(userSearchType: SearchType) {
        searchType = userSearchType

    }

    /**
     * Gets Books information from the Books API Retrofit service and updates the
     * [Item] [List] [MutableList].
     */
    fun getBooks() {
        viewModelScope.launch {
            booksUiState = BooksUiState.Loading
            booksUiState = try {
                BooksUiState.Success(booksRepository.getBooks(userInput, "$searchType"))

            } catch (e: IOException) {
                BooksUiState.Error

            } catch (e: HttpException) {
                BooksUiState.Error
            } catch (e: SerializationException) {
                BooksUiState.Error
            }
        }
    }

    /**
     * Factory for [BooksViewModel] that takes [BooksRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BooksApplication)
                val booksRepository = application.container.booksRepository
                BooksViewModel(booksRepository = booksRepository)
            }
        }
    }
}
