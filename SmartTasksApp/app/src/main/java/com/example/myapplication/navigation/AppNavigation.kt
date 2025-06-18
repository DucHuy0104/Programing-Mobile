package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.EmptyListScreen
import com.example.myapplication.ui.screens.TaskDetailScreen
import com.example.myapplication.ui.screens.TodoListScreen
import com.example.myapplication.viewmodel.TaskViewModel

sealed class Screen(val route: String) {
    object TodoList : Screen("todo_list")
    object EmptyList : Screen("empty_list")
    object Detail : Screen("task_detail/{id}") {
        fun createRoute(id: Int) = "task_detail/$id"
    }
}

@Composable
fun AppNavigation(viewModel: TaskViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.TodoList.route) {
        composable(Screen.TodoList.route) {
            TodoListScreen(navController, viewModel)
        }
        composable(Screen.EmptyList.route) {
            EmptyListScreen(navController)
        }
        composable(Screen.Detail.route, arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            TaskDetailScreen(navController, viewModel, id)
        }
    }
}