package com.example.myapplication.data.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val status: String,
    val priority: String,
    val time: String,
    val date: String,
    val subtasks: List<String>,
    val attachment: String?
)
