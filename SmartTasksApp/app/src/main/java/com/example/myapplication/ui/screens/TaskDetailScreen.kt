package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.TaskRed
import com.example.myapplication.viewmodel.TaskViewModel

@Composable
fun TaskDetailScreen(navController: NavController, viewModel: TaskViewModel, taskId: Int) {
    LaunchedEffect(taskId) {
        viewModel.fetchTaskById(taskId)
    }
    val task = viewModel.selectedTask
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Detail", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
        }
        if (task != null) {
            Text(task.title, style = MaterialTheme.typography.headlineMedium)
            Text(task.description, style = MaterialTheme.typography.bodyMedium)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = TaskRed)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column { Text("Category", style = MaterialTheme.typography.bodySmall); Text(task.category) }
                    Column { Text("Status", style = MaterialTheme.typography.bodySmall); Text(task.status) }
                    Column { Text("Priority", style = MaterialTheme.typography.bodySmall); Text(task.priority) }
                }
            }
            Text("Subtasks", style = MaterialTheme.typography.titleMedium)
            task.subtasks.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = false, onCheckedChange = null)
                    Text(it)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Attachments", style = MaterialTheme.typography.titleMedium)
            if (task.attachment != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Text(task.attachment)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}