package id.co.ingatin

import android.app.Application
import android.net.Uri
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

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val TASK = "task"
    const val TASK_FORM = "task/{taskId}"
    const val DETAIL_TASK = "DetailTask"
    const val DETAIL_TASK_FORM = "DetailTask/{taskId}"
    const val MY_TASK = "MyTask"
    const val MY_TASK_FORM = "MyTask/{category}"
}

@Composable
fun IngatinNav(startDestination: String) {

    val navController = rememberNavController()

    NavHost(navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(
            route = Routes.TASK_FORM,
            arguments = listOf(
                navArgument("taskId") {
                defaultValue = ""
            })
        ) {
            val taskId = it.arguments?.getString("taskId") ?: ""
            TaskScreen(navController, taskId = taskId)
        }

        composable(Routes.DETAIL_TASK_FORM) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            DetailTaskScreen(navController, taskId = taskId)
        }

        composable(Routes.MY_TASK_FORM) { backStackEntry ->
            val category = Uri.decode(backStackEntry.arguments?.getString("category") ?: "")
            MyTaskScreen(navController, category = category)
        }
    }

}

@HiltAndroidApp
class IngatinApp : Application()