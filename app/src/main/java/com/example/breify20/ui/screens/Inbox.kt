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

enum class EmailPriority {
    URGENT,
    IMPORTANT,
    NORMAL,
    LOW
}

@Composable
fun InboxScreen(modifier: Modifier = Modifier ,
                navController: NavController ,
                viewModel: EmailViewModel? = null
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val emails = if(viewModel!=null){
        viewModel.emails.collectAsLazyPagingItems()
    }else{
        null
    }
    LaunchedEffect(Unit) {
        emails?.refresh()
    }
    LaunchedEffect(Unit) {
        if (viewModel != null) {
        val accessToken = SecurePrefs.getPrefs(context = context).getString("accessToken", "") ?: ""
//        if(accessToken != "") {
//            viewModel.loadEmails(accessToken)
//        }
    }
    }
    LaunchedEffect(searchQuery) {
        delay(300)
        if (searchQuery.isNotBlank()) {
            viewModel?.search(searchQuery, null)
        }
    }
    val listState = rememberLazyListState()
    var showTopBar by remember { mutableStateOf(true) }
    val searchResults by viewModel?.searchResults?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val isSearching = searchQuery.isNotBlank()
    Log.d("INBOX",isSearching.toString())
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
                    onSearchChange = { searchQuery = it }
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

        LazyColumn(
            state = listState,
            modifier = modifier
                .padding(padding)
                .padding(bottom = 16.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isSearching) {
                items(searchResults.size) { index ->
                    val email = searchResults[index]
                    EmailCard(email = email, navController = navController)
                }
            } else {
                if(emails!=null) {
                    items(emails.itemCount) { index ->
                        emails[index]?.let { email ->
                            EmailCard(
                                email = email,
                                navController = navController
                            )
                        }

                    }
                }

            }

        }
    }
}




