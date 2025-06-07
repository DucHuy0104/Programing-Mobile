package com.example.smarttasks

// Import các thư viện cần thiết
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ViewModel để quản lý state
class PasswordResetViewModel : ViewModel() {
    var email by mutableStateOf("")
    var verificationCode by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    
    var emailError by mutableStateOf("")
    var codeError by mutableStateOf("")
    var passwordError by mutableStateOf("")
    
    fun validateEmail(): Boolean {
        return if (email.isEmpty()) {
            emailError = "Yêu cầu nhập thông tin"
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Yêu cầu nhập thông tin chính xác"
            false
        } else {
            emailError = ""
            true
        }
    }
    
    fun validateCode(): Boolean {
        return if (verificationCode.isEmpty()) {
            codeError = "Yêu cầu nhập thông tin"
            false
        } else if (verificationCode.length != 6) {
            codeError = "Yêu cầu nhập thông tin chính xác"
            false
        } else {
            codeError = ""
            true
        }
    }
    
    fun validatePasswords(): Boolean {
        return if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            passwordError = "Yêu cầu nhập thông tin"
            false
        } else if (newPassword != confirmPassword) {
            passwordError = "Yêu cầu nhập thông tin chính xác"
            false
        } else {
            passwordError = ""
            true
        }
    }
    
    fun validateAll(): Boolean {
        val emailValid = validateEmail()
        val codeValid = validateCode()
        val passwordsValid = validatePasswords()
        return emailValid && codeValid && passwordsValid
    }
}

// MainActivity - Điểm khởi đầu của ứng dụng
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTasksTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    
                    // Xử lý back press
                    BackHandler {
                        if (navController.currentBackStackEntry?.destination?.route != "forget_password") {
                            navController.popBackStack()
                        }
                    }
                    
                    NavHost(
                        navController = navController,
                        startDestination = "forget_password"
                    ) {
                        composable("forget_password") {
                            ForgetPasswordScreen(
                                navController = navController,
                                onNextClick = {
                                    navController.navigate("verify_code") {
                                        popUpTo("forget_password") { saveState = true }
                                    }
                                }
                            )
                        }
                        composable("verify_code") {
                            VerifyCodeScreen(
                                navController = navController,
                                onNextClick = {
                                    navController.navigate("create_new_password") {
                                        popUpTo("verify_code") { saveState = true }
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("create_new_password") {
                            CreateNewPasswordScreen(
                                navController = navController,
                                onNextClick = {
                                    navController.navigate("confirm") {
                                        popUpTo("create_new_password") { saveState = true }
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("confirm") {
                            ConfirmScreen(
                                navController = navController,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Màn hình Quên mật khẩu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    navController: NavController,
    viewModel: PasswordResetViewModel = viewModel(),
    onNextClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            Image(
                painter = painterResource(id = R.drawable.forget_password_image),
                contentDescription = "Forget Password Image",
                modifier = Modifier.size(120.dp)
            )
            
            Text(
                text = "**Forget Password?**",
                fontWeight = FontWeight.Bold
            )
            
            Text(text = "Enter your Email, we will send you a verification code.")
            
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Your Email") },
                isError = viewModel.emailError.isNotEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            
            if (viewModel.emailError.isNotEmpty()) {
                Text(
                    text = viewModel.emailError,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Button(
                onClick = { 
                    focusManager.clearFocus()
                    if (viewModel.validateEmail()) {
                        onNextClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = "Next")
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// Màn hình Xác thực mã
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeScreen(
    navController: NavController,
    viewModel: PasswordResetViewModel = viewModel(),
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Image(
                painter = painterResource(id = R.drawable.forget_password_image),
                contentDescription = "Verify Code Image",
                modifier = Modifier.size(120.dp)
            )
            
            Text(
                text = "**Verify Code**",
                fontWeight = FontWeight.Bold
            )
            
            Text(text = "We just sent the code to your registered Email.")
            
            OutlinedTextField(
                value = viewModel.verificationCode,
                onValueChange = { viewModel.verificationCode = it },
                label = { Text("Code") },
                isError = viewModel.codeError.isNotEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            if (viewModel.codeError.isNotEmpty()) {
                Text(
                    text = viewModel.codeError,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Button(
                onClick = { 
                    focusManager.clearFocus()
                    if (viewModel.validateCode()) {
                        onNextClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = "Next")
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// Màn hình Tạo mật khẩu mới
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewPasswordScreen(
    navController: NavController,
    viewModel: PasswordResetViewModel = viewModel(),
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Image(
                painter = painterResource(id = R.drawable.forget_password_image),
                contentDescription = "Create New Password Image",
                modifier = Modifier.size(120.dp)
            )
            
            Text(
                text = "**Create New Password**",
                fontWeight = FontWeight.Bold
            )
            
            Text(text = "Your new password must be different from previously used password.")
            
            OutlinedTextField(
                value = viewModel.newPassword,
                onValueChange = { viewModel.newPassword = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = viewModel.passwordError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = viewModel.passwordError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
            
            if (viewModel.passwordError.isNotEmpty()) {
                Text(
                    text = viewModel.passwordError,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Button(
                onClick = { 
                    focusManager.clearFocus()
                    if (viewModel.validatePasswords()) {
                        onNextClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = "Next")
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// Màn hình Xác nhận
@Composable
fun ConfirmScreen(
    navController: NavController,
    viewModel: PasswordResetViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Image(
                painter = painterResource(id = R.drawable.forget_password_image),
                contentDescription = "Confirm Image",
                modifier = Modifier.size(120.dp)
            )
            
            Text(
                text = "Confirm Your Information",
                fontWeight = FontWeight.Bold
            )
            Text(text = "Please verify your email and password before submitting")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Email field - Có thể chỉnh sửa
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Email") },
                isError = viewModel.emailError.isNotEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            if (viewModel.emailError.isNotEmpty()) {
                Text(
                    text = viewModel.emailError,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Password fields - Có thể chỉnh sửa
            OutlinedTextField(
                value = viewModel.newPassword,
                onValueChange = { viewModel.newPassword = it },
                label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = viewModel.passwordError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = viewModel.passwordError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
            if (viewModel.passwordError.isNotEmpty()) {
                Text(
                    text = viewModel.passwordError,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Button(
                onClick = { 
                    focusManager.clearFocus()
                    if (viewModel.validateEmail() && viewModel.validatePasswords()) {
                        navController.navigate("forget_password") {
                            popUpTo("forget_password") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "**Submit**",
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// Theme của ứng dụng
@Composable
fun SmartTasksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFFBB86FC),
            secondary = androidx.compose.ui.graphics.Color(0xFF03DAC6),
            tertiary = androidx.compose.ui.graphics.Color(0xFFCF6679)
        )
    } else {
        lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF6200EE),
            secondary = androidx.compose.ui.graphics.Color(0xFF03DAC6),
            tertiary = androidx.compose.ui.graphics.Color(0xFFCF6679)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}