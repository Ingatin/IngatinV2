package id.co.ingatin

import android.app.Application
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.HiltAndroidApp
import id.co.ingatin.ui.screen.auth.LoginScreen
import id.co.ingatin.ui.screen.auth.RegisterScreen
import id.co.ingatin.ui.screen.home.HomeScreen
import id.co.ingatin.ui.screen.task.DetailTaskScreen
import id.co.ingatin.ui.screen.task.MyTaskScreen
import id.co.ingatin.ui.screen.task.TaskScreen

@Composable
fun IngatinNav(startDestination: String) {

    val navController = rememberNavController()

    NavHost(navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
//        composable("task") {
//            TaskScreen(navController)
//        }

        composable(
            route = "task/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                defaultValue = ""
            })
        ) {
            val taskId = it.arguments?.getString("taskId") ?: ""
            TaskScreen(navController, taskId = taskId)
        }

        composable("DetailTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            Log.d("DetailTaskScreen_navhost", "Received taskId: $taskId")
            DetailTaskScreen(navController, taskId = taskId)
        }

        composable("MyTask/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            MyTaskScreen(navController, category = category)
        }


    }

}

@HiltAndroidApp
class IngatinApp : Application()