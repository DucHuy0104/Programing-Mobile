package com.example.myapplication.data.api


import com.example.myapplication.data.model.Task
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/researchUTH/task")
    suspend fun getTasks(): List<Task>

    @GET("api/researchUTH/task/{id}")
    suspend fun getTaskById(@Path("id") id: Int): Task
}