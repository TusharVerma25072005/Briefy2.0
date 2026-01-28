package com.example.breify20.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


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
        senderName = "Sender $index",
        senderEmail = "sender$index@gmail.com",
        subject = "Subject line $index",
        summary = "This is one liner AI-generated summary for email $index.",
        body = "This is the full email body content for email $index.",
        isRead = index % 2 == 0,
        priority = EmailPriority.entries.random(),
        time = "10:30 AM",
        detailedSummary = "This is a detailed AI-generated summary for email $index."

    )
}


@Composable
fun PriorityIndicator(priority: EmailPriority) {
    val color = when (priority) {
        EmailPriority.HIGH -> MaterialTheme.colorScheme.error
        EmailPriority.MEDIUM -> MaterialTheme.colorScheme.primary
        EmailPriority.LOW -> MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = Modifier
            .width(5.dp)
            .fillMaxHeight()
            .background(color)
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
fun EmailCard(email: EmailItem) {

    Card(
        modifier = Modifier
            .padding(horizontal = 2.dp, vertical = 0.dp)
            .fillMaxWidth()
        ,
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface)
                .height(IntrinsicSize.Min)
        ) {
            PriorityIndicator(email.priority)
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // This is a blank avatar later replace with image
                    BlankAvatar()
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = email.subject,
                            fontWeight = if (email.isRead) FontWeight.Medium else FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = email.senderName,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = email.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(4.dp)
                            )
                        .padding(10.dp),
                ) {
                    Text(
                        text = email.summary,
                        style = MaterialTheme.typography.bodySmall,

                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            thickness = 1.dp
        )
    }
}



@Composable
fun Topbar() {
    var searchQuery by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()

            .padding(12.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BlankAvatar()
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (searchQuery.isEmpty()) {
                Text(
                    text = "Search emails",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        BlankAvatar()
    }
}


@Composable
fun InboxScreen(modifier: Modifier = Modifier) {



    Scaffold { padding ->
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Topbar()
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                items(emailList) { email ->
                    EmailCard(email = email)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InboxScreenPreview(){
    InboxScreen()
}


