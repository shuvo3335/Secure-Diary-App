package com.shuvo.journalismapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shuvo.journalismapplication.ui.screens.EntryCreationScreen
import com.shuvo.journalismapplication.ui.screens.TimelineScreen
import com.shuvo.journalismapplication.ui.viewmodel.JournalViewModel

sealed class Screen(val route: String) {
    object Timeline : Screen("timeline")
    object EntryCreation : Screen("entry_creation?entryId={entryId}") {
        fun createRoute(entryId: Int? = null) = if (entryId != null) "entry_creation?entryId=$entryId" else "entry_creation"
    }
}

@Composable
fun JournalNavHost(navController: NavHostController, viewModel: JournalViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Timeline.route
    ) {
        composable(Screen.Timeline.route) {
            TimelineScreen(
                viewModel = viewModel,
                onAddEntryClick = {
                    navController.navigate(Screen.EntryCreation.createRoute())
                },
                onEditEntryClick = { entryId ->
                    navController.navigate(Screen.EntryCreation.createRoute(entryId))
                }
            )
        }
        composable(
            route = Screen.EntryCreation.route,
            arguments = listOf(
                androidx.navigation.navArgument("entryId") {
                    type = androidx.navigation.NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getInt("entryId") ?: -1
            EntryCreationScreen(
                viewModel = viewModel,
                entryId = if (entryId == -1) null else entryId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
