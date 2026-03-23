package com.example.breify20.ui.screens
import androidx.compose.runtime.getValue
import android.util.Log
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.breify20.model.email.Category
import com.example.breify20.model.email.EmailItem
import com.example.breify20.ui.components.EmailBodyText
import com.example.breify20.ui.components.SenderAvatar
import com.example.breify20.ui.viewModel.EmailViewModel

var sample = EmailItem(
    id = "0",
    senderName = "Sender",
    senderEmail = "sender@gmail.com",
    subject = "Subject line ",
    summary = "This is one liner AI-generated summary for email .",
    body = "This is the full email body content for email . Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n" +  "\n" + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\n" +  "\n" + "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n",
    isRead = true,
    priority = EmailPriority.entries.random(),
    time = "10:30 AM",
    detailedSummary = "The email outlines final updates to the Q4 roadmap and budget allocation following the latest review cycle. Key feedback has been incorporated, with specific attention given to budget adjustments.\n",
    category = Category.WORK,
    bodyType = "text/plain",
    embedding = ""
)


@Composable
fun EmailScreen( modifier: Modifier = Modifier,
                emailId: String,
                navController: NavController,
                 viewModel: EmailViewModel
                 ) {
    val mail by viewModel.getMailById(emailId).collectAsState(initial = null)


    Column(
        modifier = modifier.fillMaxSize()
            .padding(vertical = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = modifier.height(20.dp))
        Row(
            modifier = modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }

            )

        }

        Spacer(modifier = Modifier.height(24.dp))
        mail?.let{email ->
            Text(
                text = email.subject,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SenderAvatar(email.senderName)
                Spacer(modifier = Modifier.width(12.dp))
                Row(
                    modifier = modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = modifier
                    ) {

                        Text(
                            text = email.senderName,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = email.senderEmail,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))


                    }
                    Column(
                        modifier = modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End


                    ) {
                        Text(text = email.time,)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI BRIEF",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = email.detailedSummary,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (email.bodyType == "text/html") {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    factory = { context ->
                        WebView(context).apply {
                            settings.apply {
                                javaScriptEnabled = false
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                builtInZoomControls = true
                                displayZoomControls = false
                                textZoom = 80
                            }
                            val gmailFixHtml = """
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                    <style>
                        html { width: 100% !important; }
                        body { width: 100% !important; margin:0; padding: 8px 3px; }
                        table, td, div, p, a, img { max-width: 100% !important; }
                        img { height: auto !important; }
                        * { box-sizing: border-box !important; }
                    </style>
                </head>
                <body>
                    ${email.body}
                </body>
                </html>
            """.trimIndent()
                            loadDataWithBaseURL(
                                null,
                                gmailFixHtml,
                                "text/html",
                                "utf-8",
                                null
                            )
                        }
                    }
                )

            } else {
                EmailBodyText(emailBody = email.body)
            }
        }
    }
}






