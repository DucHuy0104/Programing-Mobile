package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.api.RetrofitClient
import com.example.myapplication.data.model.Task
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    var taskList by mutableStateOf<List<Task>>(emptyList())
    var selectedTask by mutableStateOf<Task?>(null)

    fun loadTasks() {
        viewModelScope.launch {
            taskList = try {
                RetrofitClient.apiService.getTasks()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun fetchTaskById(id: Int) {
        viewModelScope.launch {
            selectedTask = try {
                RetrofitClient.apiService.getTaskById(id)
            } catch (e: Exception) {
                null
            }
        }
    }
}