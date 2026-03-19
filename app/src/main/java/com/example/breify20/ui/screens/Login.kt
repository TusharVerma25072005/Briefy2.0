import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.breify20.R
import androidx.core.net.toUri
import com.example.breify20.ui.viewModel.AuthViewModel
import com.example.breify20.worker.WorkManagerHelper

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    authViewModel : AuthViewModel,
    mail :String? = "")
{

    var email by remember { mutableStateOf(mail ?: "") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val loginSuccess = authViewModel.loginSuccess.value
    val loginError = authViewModel.loginError.value

    LaunchedEffect(loginError) {
        if(loginError.isNotEmpty()){
            Toast.makeText(context, loginError, Toast.LENGTH_LONG).show()
        }
    }
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            WorkManagerHelper.restartSync(context)
            navController.navigate("inbox") {
                popUpTo("login") { inclusive = true }
            }
        }
    }


    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = modifier.fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.briefy),
                    contentDescription = "Logo",
                    modifier = modifier
                        .height(200.dp),
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopCenter
                )

                Text(
                    text = "Your Intelligent Email Assistant\nSummarize, prioritize, and focus",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )

                InputField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email"
                )
                Spacer(modifier = Modifier.height(12.dp))
                InputField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password"
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if(email.isEmpty() || password.isEmpty()){
                            Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            authViewModel.LoginUser(email, password, context)
                        }

                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Login",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton( onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://briefy2-0-backend.onrender.com/signup".toUri()
                    )
                    context.startActivity(intent)
                } ) {
                    Text(
                        "Don't have an account? Sign up",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


