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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.breify20.ui.components.BottomBar
import com.example.breify20.ui.components.CategorySelectBox
import com.example.breify20.ui.components.EmailCard
import com.example.breify20.ui.screens.EmailPriority
import com.example.breify20.ui.screens.emailList

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
fun CategoryScreen(
    navController: NavController ,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()
    var showTopBar by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf(EmailPriority.HIGH) }

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
                Column {
                    Topbar(modifier = modifier , navController = navController)
                    CategorySelectBox(selected = selectedCategory, onSelect = {
                        selectedCategory = it
                    })
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showTopBar,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BottomBar(modifier = modifier , selectedScreen = 1 , navController = navController)
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
fun CategoryPreview(){
    var navController = rememberNavController()
    CategoryScreen(navController = navController)
}


