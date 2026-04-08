package com.example.breify20.ui.screens
import Topbar
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.breify20.data.SecurePrefs
import com.example.breify20.ui.components.BottomBar
import com.example.breify20.ui.components.EmailCard
import com.example.breify20.ui.viewModel.EmailViewModel
import kotlinx.coroutines.delay

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import com.example.breify20.ui.components.EmptyState
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.rememberCoroutineScope
import com.example.breify20.worker.WorkManagerHelper
import kotlinx.coroutines.launch

enum class EmailPriority {
    URGENT,
    IMPORTANT,
    NORMAL,
    LOW
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(modifier: Modifier = Modifier ,
                navController: NavController ,
                viewModel: EmailViewModel? = null
) {

    val context = LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()
    var searchQuery by remember { mutableStateOf("") }
    val emails = if(viewModel!=null){
        viewModel.emails.collectAsLazyPagingItems()
    }else{
        null
    }
    LaunchedEffect(Unit) {
        emails?.refresh()
    }
    BackHandler(enabled = searchQuery.isNotEmpty()) {
        searchQuery = ""
        viewModel?.clearSearchResults()
    }
    LaunchedEffect(Unit) {
        if (viewModel != null) {
        val accessToken = SecurePrefs.getPrefs(context = context).getString("accessToken", "") ?: ""
    }
    }
    val listState = rememberLazyListState()
    var showTopBar by remember { mutableStateOf(true) }
    val searchResults by viewModel?.searchResults?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
val isSearchingLoading by viewModel?.isSearching?.collectAsState()
        ?: remember { mutableStateOf(false) }
    LaunchedEffect(listState) {
        var lastIndex = 0
        var lastOffset = 0

        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->

            val isScrollingUp =
                index < lastIndex ||
                        (index == lastIndex && offset < lastOffset)

            showTopBar = isScrollingUp || index == 0

            lastIndex = index
            lastOffset = offset
        }
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            AnimatedVisibility(
                visible = showTopBar,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Topbar(modifier = modifier , navController = navController ,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onSearchSubmit = {
                        viewModel?.search(it, null)
                    }
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showTopBar,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BottomBar(modifier = modifier, navController = navController)
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true

                WorkManagerHelper.restartSync(context)

                scope.launch {
                    kotlinx.coroutines.delay(1000)
                    isRefreshing = false
                }
            },
            state = pullRefreshState
        ) {
            LazyColumn(
                state = listState,
                modifier = modifier
                    .padding(padding)
                    .padding(bottom = 16.dp)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {

                if (emails != null && emails.loadState.refresh is androidx.paging.LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (searchQuery.isNotBlank()) {

                    if (isSearchingLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(top = 20.dp),
                                contentAlignment = Alignment.Center,

                                ) {

                                CircularProgressIndicator()
                            }
                        }
                    } else if (searchResults.isEmpty()) {
                        item {
                            EmptyState("No results found")
                        }
                    } else {
                        items(searchResults.size) { index ->
                            val email = searchResults[index]
                            EmailCard(email = email, navController = navController)
                        }
                    }
                } else {
                    if (emails != null && emails.itemCount == 0) {
                        item {
                            EmptyState("No emails available")
                        }
                    } else {
                        if (emails != null) {
                            items(emails.itemCount) { index ->
                                emails[index]?.let { email ->
                                    EmailCard(email = email, navController = navController)
                                }
                            }
                        }
                    }
                }
            }
        }

        }
    }





