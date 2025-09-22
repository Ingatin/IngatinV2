package id.co.ingatin

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import id.co.ingatin.ui.screen.auth.LoginScreen
import id.co.ingatin.ui.screen.auth.RegisterScreen
import id.co.ingatin.ui.screen.home.HomeScreen
import id.co.ingatin.ui.screen.task.DetailTaskScreen
import id.co.ingatin.ui.screen.task.MyTaskScreen
import id.co.ingatin.ui.screen.task.TaskScreen

@Composable
fun IngatinApp(startDestination: String){

    val navController = rememberNavController()

    NavHost(navController, startDestination = startDestination){
        composable("login"){
            LoginScreen(navController)
        }
        composable("register"){
            RegisterScreen(navController)
        }
        composable("home"){
            HomeScreen(navController)
        }
        composable("task") {
            TaskScreen(navController = navController, taskId = null) // Create Task
        }

        composable(
            route = "task/{taskId}",
            arguments = listOf(navArgument("taskId") { defaultValue = "" })
        ) {
            val taskId = it.arguments?.getString("taskId")
            TaskScreen(
                navController = navController,
                taskId = if (taskId.isNullOrEmpty()) null else taskId
            ) // Edit Task
        }
        composable("DetailTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            if (taskId != null) {
                Log.d("DetailTaskScreen_navhost", "Received taskId: $taskId")
                DetailTaskScreen(navController, taskId)
            }
        }
        composable("MyTask/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "All Task"
            MyTaskScreen(navController, category)
        }


    }

}