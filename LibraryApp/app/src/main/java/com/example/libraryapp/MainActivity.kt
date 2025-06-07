package com.example.libraryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Lớp quản lý thư viện (OOP)
class LibraryManager {
    private val students = mutableListOf<Student>()
    private val books = mutableListOf<Book>()

    fun addStudent(student: Student) {
        if (!students.contains(student)) {
            students.add(student)
        }
    }

    fun addBook(book: Book) {
        books.add(book)
    }

    fun borrowBook(student: Student, book: Book) {
        if (!book.isBorrowed) {
            book.isBorrowed = true
            book.borrowedBy = student
            addStudent(student)
        }
    }

    fun getBooks(): List<Book> = books
    fun getStudents(): List<Student> = students
}

// Data classes cho OOP
data class Student(val name: String)
data class Book(val title: String, var isBorrowed: Boolean = false, var borrowedBy: Student? = null)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibraryApp()
        }
    }
}

@Composable
fun LibraryApp() {
    val navController = rememberNavController()
    val libraryManager = remember { LibraryManager() }
    // Thêm sách mặc định
    LaunchedEffect(Unit) {
        libraryManager.addBook(Book("Sach 01"))
        libraryManager.addBook(Book("Sach 02"))
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Quản lý") },
                    label = { Text("Quản lý") },
                    selected = navController.currentDestination?.route == "management",
                    onClick = { navController.navigate("management") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Danh sách sách") },
                    label = { Text("Danh sách") },
                    selected = navController.currentDestination?.route == "book_list",
                    onClick = { navController.navigate("book_list") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Sinh viên") },
                    label = { Text("Sinh viên") },
                    selected = navController.currentDestination?.route == "students",
                    onClick = { navController.navigate("students") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Thêm sách") },
                    label = { Text("Thêm sách") },
                    selected = navController.currentDestination?.route == "add_book",
                    onClick = { navController.navigate("add_book") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "management",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("management") {
                ManagementScreen(libraryManager, navController)
            }
            composable("book_list") {
                BookListScreen(libraryManager)
            }
            composable("students") {
                StudentScreen(libraryManager)
            }
            composable("add_book") {
                AddBookScreen(libraryManager, navController)
            }
        }
    }
}

@Composable
fun ManagementScreen(libraryManager: LibraryManager, navController: NavHostController) {
    var studentName by remember { mutableStateOf("") }
    var selectedBooks by remember { mutableStateOf(listOf<Book>()) }
    var borrowMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hệ thống Quản lý Thư viện",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Phần nhập tên sinh viên
        Text(
            text = "Sinh viên",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = studentName,
                onValueChange = { studentName = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, shape = RoundedCornerShape(4.dp)),
                placeholder = { Text("Nhập tên sinh viên") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { /* Không cần thay đổi vì đã nhập trực tiếp */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Thay đổi")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phần danh sách sách
        Text(
            text = "Danh sách sách",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start)
        )
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(libraryManager.getBooks()) { book ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedBooks.contains(book),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedBooks = selectedBooks + book
                                } else {
                                    selectedBooks = selectedBooks - book
                                }
                            },
                            enabled = !book.isBorrowed,
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF8B0000),
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = book.title,
                            modifier = Modifier.padding(start = 8.dp),
                            color = if (book.isBorrowed) Color.Gray else Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Thông báo mượn sách
        if (borrowMessage.isNotEmpty()) {
            Text(
                text = borrowMessage,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Nút mượn sách
        Button(
            onClick = {
                if (studentName.isBlank()) {
                    borrowMessage = "Bạn chưa nhập tên sinh viên!"
                } else if (selectedBooks.isEmpty()) {
                    borrowMessage = "Bạn chưa chọn sách nào! Vui lòng chọn ít nhất một cuốn sách."
                } else {
                    val student = Student(studentName)
                    selectedBooks.forEach { book ->
                        libraryManager.borrowBook(student, book)
                    }
                    borrowMessage = "Nhận: Thêm để bắt đầu trình đọc sách."
                    selectedBooks = emptyList()
                    studentName = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
        ) {
            Text("Thêm")
        }
    }
}

@Composable
fun BookListScreen(libraryManager: LibraryManager) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Danh sách sách",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(libraryManager.getBooks()) { book ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${book.title} - ${if (book.isBorrowed) "Đã mượn" else "Có sẵn"}",
                            modifier = Modifier.padding(start = 8.dp),
                            color = if (book.isBorrowed) Color.Gray else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentScreen(libraryManager: LibraryManager) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Danh sách sinh viên mượn sách",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(libraryManager.getStudents()) { student ->
                    val borrowedBooks = libraryManager.getBooks().filter { it.borrowedBy == student }
                    if (borrowedBooks.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Sinh viên: ${student.name}",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            borrowedBooks.forEach { book ->
                                Text(
                                    text = "- ${book.title}",
                                    modifier = Modifier.padding(start = 16.dp, bottom = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddBookScreen(libraryManager: LibraryManager, navController: NavHostController) {
    var bookTitle by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Thêm sách mới",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Tiêu đề sách",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start)
        )
        TextField(
            value = bookTitle,
            onValueChange = { bookTitle = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(Color.White, shape = RoundedCornerShape(4.dp)),
            placeholder = { Text("Nhập tiêu đề sách") },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (message.contains("thành công")) Color.Green else Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (bookTitle.isBlank()) {
                    message = "Vui lòng nhập tiêu đề sách!"
                } else {
                    libraryManager.addBook(Book(bookTitle))
                    message = "Thêm sách thành công!"
                    bookTitle = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
        ) {
            Text("Thêm sách")
        }
    }
}