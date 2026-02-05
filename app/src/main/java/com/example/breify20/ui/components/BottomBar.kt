package com.example.breify20.ui.components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun BottomBar(modifier : Modifier = Modifier , selectedScreen :Int = 0){
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        NavigationBarItem(
            selected = selectedScreen == 0,
            onClick = {  },
            icon = {
                Icon(Icons.Default.Email, contentDescription = "Inbox")
            },
            label = { Text("Inbox") }
        )

        NavigationBarItem(
            selected = selectedScreen == 1,
            onClick = {  },
            icon = {
                Icon(Icons.Default.Menu, contentDescription = "Category")
            },
            label = { Text("Category") }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun BottomBarPreview(){
    BottomBar()
}
