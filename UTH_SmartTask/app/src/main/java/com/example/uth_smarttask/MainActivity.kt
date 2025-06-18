package com.example.uth_smarttask

import android.os.Bundle
import android.app.Activity
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.rememberAsyncImagePainter
import com.example.uth_smarttask.ui.theme.UTH_SmartTaskTheme
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            UTH_SmartTaskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Check if user is already signed in
                    LaunchedEffect(Unit) {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            navController.navigate("profile") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") { backStackEntry ->
                            LoginScreen(navController)
                        }
                        composable("profile") { backStackEntry ->
                            ProfileScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            isLoading = true
            errorMessage = null
            
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    println("Google Sign In success: ${account.email}")
                    println("Google Sign In token: ${account.idToken?.take(20)}...")
                    
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    firebaseAuth.signInWithCredential(credential)
                        .addOnSuccessListener {
                            println("Firebase Auth success: ${it.user?.email}")
                            isLoading = false
                            navController.navigate("profile") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        .addOnFailureListener { e ->
                            println("Firebase Auth failed: ${e.message}")
                            println("Firebase Auth error details: ${e.cause}")
                            isLoading = false
                            errorMessage = when {
                                e.message?.contains("invalid-credential") == true -> "Invalid credentials. Please try again."
                                e.message?.contains("network") == true -> "Network error. Please check your connection."
                                else -> e.message ?: "Authentication failed"
                            }
                        }
                } catch (e: Exception) {
                    println("Google Sign In failed: ${e.message}")
                    when (e) {
                        is ApiException -> {
                            println("Google Sign In API error code: ${e.statusCode}")
                            println("Google Sign In API error message: ${e.status.statusMessage}")
                            errorMessage = when (e.statusCode) {
                                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Sign in was cancelled"
                                GoogleSignInStatusCodes.NETWORK_ERROR -> "Network error. Please check your connection"
                                else -> "Google Sign In failed: ${e.status.statusMessage}"
                            }
                        }
                        else -> errorMessage = e.message ?: "Sign in failed"
                    }
                    isLoading = false
                    e.printStackTrace()
                }
            } else {
                println("Sign in result: cancelled")
                isLoading = false
                errorMessage = "Sign in was cancelled"
            }
        }
    )

    val signInRequest: () -> Unit = {
        try {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            println("Web Client ID: ${context.getString(R.string.default_web_client_id)}")
            
            val client = GoogleSignIn.getClient(context, options)
            
            // Sign out first to show account picker every time
            client.signOut().addOnCompleteListener {
                println("Previous sign out completed")
                launcher.launch(client.signInIntent)
            }
        } catch (e: Exception) {
            println("Sign In Request failed: ${e.message}")
            println("Sign In Request error details: ${e.cause}")
            errorMessage = when {
                e.message?.contains("web_client_id") == true -> "Missing or invalid web client ID"
                else -> e.message ?: "Failed to initialize sign in"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.uth_logo),
                contentDescription = "UTH Logo",
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "SmartTasks",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = stringResource(R.string.app_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.welcome),
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = signInRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                enabled = !isLoading
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.sign_in_with_google))
                }
            }
            
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
            
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = when {
                        error.contains("network", ignoreCase = true) -> "Please check your internet connection"
                        error.contains("canceled", ignoreCase = true) -> "Sign in was canceled"
                        error.contains("credential", ignoreCase = true) -> "Invalid credentials"
                        else -> error
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = stringResource(R.string.copyright),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Custom Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Back Button
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Title
            Text(
                text = stringResource(R.string.profile),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Profile Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Picture with Camera Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    user?.photoUrl?.let { photoUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(photoUrl),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Camera Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_camera),
                        contentDescription = "Change photo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Profile Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Name Field
                Column {
                    Text(
                        text = stringResource(R.string.name),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    OutlinedTextField(
                        value = user?.displayName ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onBackground,
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            disabledContainerColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }

                // Email Field
                Column {
                    Text(
                        text = stringResource(R.string.email),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    OutlinedTextField(
                        value = user?.email ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onBackground,
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            disabledContainerColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }

                // Date of Birth Field
                Column {
                    Text(
                        text = stringResource(R.string.date_of_birth),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    OutlinedTextField(
                        value = "23/05/1995",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onBackground,
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            disabledContainerColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(android.R.drawable.arrow_down_float),
                                contentDescription = "Select date",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Back Button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.back),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
