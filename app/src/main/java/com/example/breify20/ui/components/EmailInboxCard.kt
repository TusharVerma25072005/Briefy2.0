package com.example.breify20.ui.components

import PriorityIndicator
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.breify20.ui.screens.BlankAvatar
import com.example.breify20.ui.screens.EmailItem
import com.example.breify20.ui.screens.sample

@Composable
fun EmailCard(email: EmailItem , navController : NavController) {

    Card(
        modifier = Modifier
            .padding(horizontal = 2.dp, vertical = 0.dp)
            .fillMaxWidth()
            .clickable(
                onClick = {
                    navController.navigate("email")
                }
            )

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
@Preview(showBackground = true)
fun EmailCardPreview(){
    var navController = rememberNavController()
    EmailCard(email = sample , navController = navController)
}