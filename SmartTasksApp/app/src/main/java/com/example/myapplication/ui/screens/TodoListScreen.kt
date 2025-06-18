package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Task
import com.example.myapplication.viewmodel.TaskViewModel
import com.example.myapplication.navigation.Screen
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.TaskRed
import com.example.myapplication.ui.theme.TaskYellow
import com.example.myapplication.ui.theme.TaskBlue
import com.example.myapplication.ui.theme.TaskGray
import com.example.myapplication.R

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text("SmartTasks", style = MaterialTheme.typography.titleLarge, color = Color(0xFF2196F3))
            Text("A simple and efficient to-do app", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.Star, contentDescription = "Star", tint = Color(0xFFFFD600))
    }
}

@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    val bgColor = when {
        task.status == "In Progress" -> TaskRed
        task.category == "Work" -> TaskYellow
        task.category == "Fitness" -> TaskBlue
        else -> TaskGray
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.status == "In Progress" || task.status == "Pending",
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(checkedColor = Color.Black)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.Black)
                )
            }
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status: ${task.status}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                )
                Text(
                    text = "${task.time} ${task.date}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                )
            }
        }
    }
}

@Composable
fun BottomBar(onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Home */ }) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
        IconButton(onClick = onAddClick) {
            Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = Color(0xFF2196F3), modifier = Modifier.size(48.dp))
        }
        IconButton(onClick = { /* Settings */ }) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    }
}

@Composable
fun TodoListScreen(navController: NavController, viewModel: TaskViewModel) {
    LaunchedEffect(Unit) { viewModel.loadTasks() }
    Column(modifier = Modifier.fillMaxSize()) {
        HomeHeader()
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(viewModel.taskList) { task ->
                TaskCard(task = task, onClick = {
                    navController.navigate(Screen.Detail.createRoute(task.id))
                })
            }
        }
        BottomBar(onAddClick = { /* TODO: Add task */ })
    }
}