@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bookmeup.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.bookmeup.R
import com.example.bookmeup.model.Item
import com.example.bookmeup.ui.theme.BooksTheme


@Composable
fun HomeScreen(
    booksUiState: BooksUiState,
    searchQuery: String,
    onSearchValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    retryAction: () -> Unit,
    searchType: SearchType,
    updateSearchType: (SearchType) -> Unit,
    modifier: Modifier = Modifier,
    userSearched: Boolean = false,
) {
    val interactionSource = MutableInteractionSource()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.toFloat()
    val translationY: Float by animateFloatAsState(
        if (!userSearched)
            screenHeight
        else 0f, label = "move search box after user searches"
    )
    Box(Modifier.fillMaxSize()) {
        Column(Modifier
            .semantics {
                isContainer = true
            }
            .zIndex(1f)
            .fillMaxSize()
            .graphicsLayer {
                this.translationY =
                    translationY
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomSearchBar(
                query = searchQuery,
                onSearchValueChange = onSearchValueChange,
                onSearch = onSearch,
                placeholder = stringResource(R.string.what_the_book),
                interactionSource = interactionSource,
                searchType = searchType,
                updateSearchType = updateSearchType
            )
            if (userSearched) {
                when (booksUiState) {
                    is BooksUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxWidth())
                    is BooksUiState.Success -> BookGridScreen(
                        booksUiState.items.items,
                        modifier
                    )

                    is BooksUiState.Error -> ErrorScreen(
                        retryAction,
                        modifier = modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * CustomSearchBar function that takes a [query] to search, a [onSearchValueChange] that connects to the view model to update
 * the text on user input, [onSearch] as a () -> Unit to call a function when the user searches either using the ime search
 * button on the keyboard or the leading icon in the search bar. [placeholder] text for within the search box with default of "".
 * [searchType] which is the currently selected searchType that should be fed from the view model. [updateSearchType] which is
 * the function that should be called from the view model to update the searchType to the correct filter. [interactionSource]
 * which should be a saved in a hoisted state as a [MutableInteractionSource].
 *
 * The search bar has a drop down menu to select a filter to apply with the search
 * */

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomSearchBar(
    query: String,
    onSearchValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    searchType: SearchType,
    updateSearchType: (SearchType) -> Unit,
    interactionSource: MutableInteractionSource,
) {
    val controller = LocalSoftwareKeyboardController.current
    var filtersVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = query,
        onValueChange = onSearchValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        leadingIcon = {
            Icon(
                painterResource(R.drawable.baseline_search_24),
                contentDescription = null,
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onSearch() }
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                contentDescription = stringResource(R.string.more_options),
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { filtersVisible = !filtersVisible })
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions {
            onSearch()
            controller?.hide()
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        ),
        modifier = Modifier
    )

    /**
     * Animated visibility below allows the filter radio button menu to be shown or not depending on whether the user has clicked
     * to show it.
     */
    AnimatedVisibility(
        visible = filtersVisible,
        enter = expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.2f
        ),
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            OutlinedCard(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectableGroup()
                        .padding(5.dp)
                ) {
                    Column {
                        RadioButtonRow(
                            searchType = searchType,
                            searchTypeTarget = SearchType.intitle,
                            updateSearchType = updateSearchType,
                            text = stringResource(R.string.in_title),
                            modifier = Modifier.padding(5.dp)
                        )
                        RadioButtonRow(
                            searchType = searchType,
                            searchTypeTarget = SearchType.inauthor,
                            updateSearchType = updateSearchType,
                            text = stringResource(R.string.in_author),
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                    Column {
                        RadioButtonRow(
                            searchType = searchType,
                            searchTypeTarget = SearchType.inpublisher,
                            updateSearchType = updateSearchType,
                            text = stringResource(R.string.in_publisher),
                            modifier = Modifier.padding(5.dp)
                        )
                        RadioButtonRow(
                            searchType = searchType,
                            searchTypeTarget = SearchType.isbn,
                            updateSearchType = updateSearchType,
                            text = stringResource(R.string.in_isbn),
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        }
    }
}

/** Radio Button with Text next to it. Avoids code repetition */
@Composable
fun RadioButtonRow(
    searchType: SearchType,
    searchTypeTarget: SearchType,
    updateSearchType: (SearchType) -> Unit,
    text: String,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .selectable(
                selected = (searchType == searchTypeTarget),
                onClick = { updateSearchType(searchTypeTarget) },
                role = Role.RadioButton
            )
    ) {
        RadioButton(
            selected = searchType == searchTypeTarget,
            onClick = null
        )
        Text(text = text)
    }
}


/**
 * The home screen displaying the loading message.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation of loading symbol")
    val rotationZ by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ), label = "Rotate loading symbol"
    )
    Image(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(rotationZ = rotationZ),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

/**
 * The home screen displaying error message with re-attempt button.
 */
@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

/**
 * The main layout for the books once searched for.
 * Displays a grid of [BookThumbnailCard]
 */
@Composable
fun BookGridScreen(books: List<Item>, modifier: Modifier = Modifier) {

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(
            items = books,
            key = { item -> item.id }) { item ->
            BookThumbnailCard(
                book = item,
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .size(150.dp, 240.dp)
            )
        }
    }
}

/**
 * a quick parse of the URL to change it to a secure HTTP connection
 */
fun parseUrl(httpUrl: String?): String? =
    httpUrl?.replace("http://", "https://")

/**
 * A reusable card that uses [SubcomposeAsyncImage] to display an image from the [book] item passed into the function.
 * The [SubcomposeAsyncImage] allows animation of loading screens
 *
 * Additionally the card allows users to click to reveal book data (or it auto shows when there is no cover to show)
 */
@Composable
fun BookThumbnailCard(book: Item, modifier: Modifier = Modifier) {
    var showBookData by remember { mutableStateOf(book.volumeInfo.imageLinks["thumbnail"] == null) }
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = { showBookData = !showBookData }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest
                    .Builder(context = LocalContext.current)
                    .data(parseUrl(book.volumeInfo.imageLinks["thumbnail"]))
                    .crossfade(true)
                    .build(),
                contentDescription = book.volumeInfo.title,
                error = {
                    Image(
                        painter = painterResource(id = R.drawable.defaultbook),
                        contentDescription = stringResource(
                            R.string.no_book_cover
                        )
                    )
                },
                loading = { LoadingScreen() },
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()

            )
            if (showBookData) {
                BookData(book = book)
            }
        }
    }
}

/**
 * Reusable composable to show data from the [book] item passed to it.
 */
@Composable
fun BookData(book: Item) {
    Card(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(
                text = book.volumeInfo.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = book.volumeInfo.authors.toString().removeSurrounding("[", "]"),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_small)),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    BooksTheme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    BooksTheme {
        ErrorScreen({})
    }
}