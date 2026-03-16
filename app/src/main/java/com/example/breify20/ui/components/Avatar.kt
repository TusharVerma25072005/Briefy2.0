package com.example.breify20.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.breify20.data.SecurePrefs

@Composable
fun Avatar(size : Boolean = false) {
    val initial = SecurePrefs.getPrefs(context = LocalContext.current).getString("name", "").toString().first().uppercase()
    val image = SecurePrefs.getPrefs(context = LocalContext.current).getString("photo", null)
    val provider = SecurePrefs.getPrefs(context = LocalContext.current).getString("provider", null)
    val dims = if(size) 120.dp else 48.dp
    Box(
        modifier = Modifier
            .size(dims)
            .clip(CircleShape)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ){
        when {
            provider == "gmail" && image != null -> {
                AsyncImage(
                    model = image,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(dims)
                )
            }
            provider == "outlook" && image != null -> {

                val imageBytes = Base64.decode(image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(dims)
                )
            }
            else -> {
                    Text(
                        text = initial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

            }

        }
    }
}