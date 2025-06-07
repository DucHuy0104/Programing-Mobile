package com.example.uthsmarttasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uthsmarttasks.ui.theme.UTHSmartTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UTHSmartTasksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

// Data class for onboarding screen content
data class OnboardingScreenData(
    val title: String,
    val description: String,
    val imageRes: Int
)

// List of onboarding screens
val onboardingScreens = listOf(
    OnboardingScreenData(
        title = "Easy Time Management",
        description = "With more management based on priority and daily tasks, it will give you convenience in managing the tasks that must be done first.",
        imageRes = R.drawable.onboarding1
    ),
    OnboardingScreenData(
        title = "Increase Work Effectiveness",
        description = "Time management and the determination of more important tasks will give your job better statistics to improve.",
        imageRes = R.drawable.onboarding2
    ),
    OnboardingScreenData(
        title = "Reminder Notification",
        description = "The advantage of this app is that it also provides reminders for you so you don’t forget to keep doing the assignments you have set.",
        imageRes = R.drawable.onboarding3
    )
)

// Navigation setup
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("onboarding") {
            OnboardingScreen(navController)
        }
    }
}

// Splash Screen
@Composable
fun SplashScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.uth_logo),
                contentDescription = "UTH Logo",
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = "UTH SmartTasks",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E88E5),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
    // Auto-navigate to onboarding after a delay (simulated here)
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000) // 2-second delay
        navController.navigate("onboarding") {
            popUpTo("splash") { inclusive = true }
        }
    }
}

// Onboarding Screen with state
@Composable
fun OnboardingScreen(navController: NavHostController) {
    val currentPage = remember { androidx.compose.runtime.mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { /* Skip to main app screen */ }) {
                Text(text = "skip", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Image
        Image(
            painter = painterResource(id = onboardingScreens[currentPage.value].imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = onboardingScreens[currentPage.value].title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = onboardingScreens[currentPage.value].description,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Dots Indicator
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            repeat(onboardingScreens.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (currentPage.value == index) Color(0xFF1E88E5) else Color.LightGray
                        )
                )
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentPage.value > 0) {
                Button(
                    onClick = { currentPage.value-- },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text(text = "←", color = Color.White)
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            Button(
                onClick = {
                    if (currentPage.value < onboardingScreens.size - 1) {
                        currentPage.value++
                    } else {
                        // Navigate to main app screen
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                modifier = Modifier
                    .height(48.dp)
                    .width(120.dp)
            ) {
                Text(
                    text = if (currentPage.value == onboardingScreens.size - 1) "Get Started" else "Next",
                    color = Color.White
                )
            }
        }
    }
}