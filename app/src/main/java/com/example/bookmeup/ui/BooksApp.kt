package com.example.bookmeup.ui

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookmeup.R
import com.example.bookmeup.ui.screens.BooksViewModel
import com.example.bookmeup.ui.screens.HomeScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksApp(modifier: Modifier = Modifier) {
    /** scroll Behavior for the top app bar */
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    /** App View Model via factory that takes BooksRepository as a dependency */
    val booksViewModel: BooksViewModel = viewModel(factory = BooksViewModel.Factory)
    /** Variable for whether user has searched */
    var userSearched by rememberSaveable { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { BooksTopAppBar(scrollBehavior = scrollBehavior, userSearched = userSearched) }
    ) { it ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HomeScreen(
                booksUiState = booksViewModel.booksUiState,
                searchQuery = booksViewModel.userInput,
                onSearchValueChange = { booksViewModel.updateUserInput(it) },
                /** Searches for the current userInput if there is one, toggles userSearched to true */
                onSearch = {
                    if (booksViewModel.userInput != "") {
                        booksViewModel.getBooks()
                        userSearched = true
                        Log.i(
                            "Search",
                            "user searched for ${booksViewModel.searchType}:${booksViewModel.userInput}"
                        )
                    } else {
                        Log.i("Search", "no user input before search")
                    }
                },
                /** Passes the [booksViewMode.getBooks()] function, toggle user searched and log the retry */
                retryAction = {
                    booksViewModel.getBooks()
                    userSearched = true
                    Log.i("Search", "User retried for ${booksViewModel.searchType}:${booksViewModel.userInput}")
                },
                searchType = booksViewModel.searchType,
                updateSearchType = {
                    booksViewModel.updateSearchType(userSearchType = it)
                    Log.i("Search", "user updated search type to ${booksViewModel.searchType}")
                },
                userSearched = userSearched

            )
        }

    }
}


// Top app bar composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    userSearched: Boolean,
    modifier: Modifier = Modifier
) {
    val topAppBarTextScale: Float by animateFloatAsState(
        targetValue = if (userSearched) 1f else 1.5f,
        label = "text scale of top app bar when user searches",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = modifier
                    .graphicsLayer(
                        scaleX = topAppBarTextScale,
                        scaleY = topAppBarTextScale
                    )
                    .padding(10.dp)
            )
        },
        scrollBehavior = scrollBehavior
        )
}