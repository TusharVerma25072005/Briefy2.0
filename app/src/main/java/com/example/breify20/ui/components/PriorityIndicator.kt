import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.breify20.ui.screens.EmailPriority

@Composable
fun PriorityIndicator(priority: EmailPriority, modifier : Modifier = Modifier) {

    val color = when (priority) {
        EmailPriority.URGENT -> MaterialTheme.colorScheme.error
        EmailPriority.IMPORTANT -> MaterialTheme.colorScheme.primary
        EmailPriority.NORMAL -> MaterialTheme.colorScheme.surfaceVariant
        EmailPriority.LOW -> MaterialTheme.colorScheme.surface
    }
    Box(
        modifier = modifier
            .width(8.dp)
            .fillMaxHeight()
            .background(color)
    )
}