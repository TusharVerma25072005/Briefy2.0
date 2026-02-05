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
        EmailPriority.HIGH -> MaterialTheme.colorScheme.error
        EmailPriority.MEDIUM -> MaterialTheme.colorScheme.primary
        EmailPriority.LOW -> MaterialTheme.colorScheme.surfaceVariant
    }
    Box(
        modifier = modifier
            .width(5.dp)
            .fillMaxHeight()
            .background(color)
    )
}