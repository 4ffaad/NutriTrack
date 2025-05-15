package com.daffa_34076492.nutritrack
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme


class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutriTrack_Daffa_34076492Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.onPrimary
                ) {
                    WelcomeScreen()
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen() {
    val context = LocalContext.current

    Scaffold(

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                // App name
                Text(
                    text = "NutriTrack",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 50.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // App logo
                Image(
                    painter = painterResource(id = R.drawable.nutritrack_logo),
                    contentDescription = "NutriTrack Logo",
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Disclaimer Text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making changes to your diet or exercise.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Use this app at your own risk.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Link to Monash Nutrition Clinic
                Text(
                    text = "If you'd like to consult an Accredited Practicing Dietitian (APD), please visit:",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground
                )

                TextButton(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW,
                            "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition".toUri())
                        context.startActivity(browserIntent)
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Monash Nutrition Clinic",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Login button
                Button(
                    onClick = {
                        // Navigate to LoginActivity
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Footer
                Text(
                    text = "Designed with ❤️ by Daffa (34076492)",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    )
}