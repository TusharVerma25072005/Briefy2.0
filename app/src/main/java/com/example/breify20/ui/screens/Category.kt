import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.breify20.model.email.Category
import com.example.breify20.ui.components.BottomBar
import com.example.breify20.ui.components.CategorySelectBox
import com.example.breify20.ui.components.EmailCard
import com.example.breify20.ui.components.EmptyState
import com.example.breify20.ui.viewModel.EmailViewModel

@Composable
fun CategoryScreen(
    navController: NavController ,
    modifier: Modifier = Modifier,
    viewModel: EmailViewModel? = null
) {
    val emails = if (viewModel != null) {
        viewModel.emails.collectAsLazyPagingItems()
    } else {
        null
    }
    val isSearchingLoading by viewModel?.isSearching?.collectAsState()
        ?: remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    var showBars by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf(Category.WORK) }
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel?.searchResults?.collectAsState()
        ?: remember { mutableStateOf(emptyList()) }

    val allEmails = emails?.itemSnapshotList?.items ?: emptyList()

    val baseList = if (searchQuery.isBlank()) {
        allEmails
    } else {
        searchResults
    }

    val finalEmails = if (searchQuery.isBlank()) {
        baseList.filter { it.category == selectedCategory }
    } else {
        baseList
    }



    BackHandler(enabled = searchQuery.isNotEmpty()) {
        searchQuery = ""
        viewModel?.clearSearchResults()
    }

    LaunchedEffect(listState) {
        var lastIndex = 0
        var lastOffset = 0
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            val isScrollingUp =
                index < lastIndex ||
                        (index == lastIndex && offset < lastOffset)

            showBars = isScrollingUp || index == 0
            lastIndex = index
            lastOffset = offset
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            AnimatedVisibility(
                visible = showBars,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Topbar(
                        modifier = modifier,
                        navController = navController,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onSearchSubmit = {
                            viewModel?.search(it, selectedCategory.name)
                        }
                    )

                    CategorySelectBox(
                        selected = selectedCategory,
                        onSelect = {
                            selectedCategory = it
                        }
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBars,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BottomBar(
                    modifier = modifier,
                    selectedScreen = 1,
                    navController = navController
                )
            }
        }
    ) { padding ->

        LazyColumn(
            state = listState,
            modifier = modifier
                .padding(padding)
                .padding(bottom = 16.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            if (searchQuery.isNotBlank() && isSearchingLoading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            else if (finalEmails.isEmpty()) {
                item {
                    EmptyState(
                        if (searchQuery.isBlank())
                            "No emails in this category"
                        else
                            "No results found"
                    )
                }
            }

            else {
                items(finalEmails) { email ->
                    EmailCard(email = email, navController = navController)
                }
            }

        }
    }
}
@Preview(showBackground = true)
@Composable
fun CategoryPreview(){
    var navController = rememberNavController()
    CategoryScreen(navController = navController)
}


