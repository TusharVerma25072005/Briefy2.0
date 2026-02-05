package com.example.breify20.ui.screens
import Topbar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breify20.ui.components.EmailCard

data class EmailItem(
    val id: String,
    val senderName: String,
    val senderEmail: String,
    val subject: String,
    val summary: String,
    val detailedSummary : String,
    val priority: EmailPriority,
    val time: String,
    val isRead: Boolean,
    val body: String
)
enum class EmailPriority {
    HIGH,
    MEDIUM,
    LOW
}
val emailList = List(20) { index ->
    EmailItem(
        id = index.toString(),
        senderName = "Tushar Verma",
        senderEmail = "Tushar$index@gmail.com",
        subject = "Adhar Card Verification",
        summary = "This is one liner AI-generated summary for email $index.",
        body = "This is the full email body content for email $index.",
        isRead = index % 2 == 0,
        priority = EmailPriority.entries.random(),
        time = "10:30 AM",
        detailedSummary = "This is a detailed AI-generated summary for email $index."

    )
}

@Composable
fun BlankAvatar() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary)
    )
}


@Composable
fun InboxScreen(modifier: Modifier = Modifier) {

    val listState = rememberLazyListState()
    var showTopBar by remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        var lastScrollOffset = 0
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect { offset ->
                showTopBar = offset < lastScrollOffset
                lastScrollOffset = offset
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
                Topbar()
            }
        }
    ) { padding ->

        LazyColumn(
            state = listState,
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(emailList) { email ->
                EmailCard(email = email)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InboxScreenPreview(){
    InboxScreen()
}


