package cn.coolbet.orbit

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cn.coolbet.orbit.view.home.HomeScreen

object NavRoutes {
    const val MAIN = "/"
    const val HOME = "/home"
    const val PROFILE = "/profile"
    const val DETAIL = "/entries/{metaId}"

}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        // --- 1. HOME 屏幕 ---
        composable(NavRoutes.HOME) {
            HomeScreen(
                toProfile = {
                    navController.navigate(NavRoutes.PROFILE)
                }
            )
        }

        composable(
            route = NavRoutes.DETAIL,
//            arguments = listOf(
//                // 声明参数类型
//                navArgument(NavRoutes.ARG_ITEM_ID) { type = NavType.StringType }
//            )
        ) { backStackEntry ->
            // 从 NavBackStackEntry 中取出参数
//            val itemId = backStackEntry.arguments?.getString(NavRoutes.ARG_ITEM_ID)
//
//            // 确保 itemId 不为空，否则显示错误或回退
//            if (itemId.isNullOrEmpty()) {
//                // 如果参数缺失，可以选择回退
//                navController.popBackStack()
//                return@composable
//            }

//            DetailScreen(
//                itemId = itemId,
//                onBack = { navController.popBackStack() }
//            )
        }
        // ... 更多页面
    }
}