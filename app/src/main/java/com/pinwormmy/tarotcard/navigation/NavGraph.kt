package com.pinwormmy.tarotcard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pinwormmy.tarotcard.data.TarotRepository
import com.pinwormmy.tarotcard.ui.screens.CardDetailScreen
import com.pinwormmy.tarotcard.ui.screens.MainMenuScreen
import com.pinwormmy.tarotcard.ui.screens.ReadingResultScreen
import com.pinwormmy.tarotcard.ui.screens.ReadingSetupScreen
import com.pinwormmy.tarotcard.ui.screens.SpreadMenuScreen
import com.pinwormmy.tarotcard.ui.screens.ShuffleAndDrawScreen
import com.pinwormmy.tarotcard.ui.state.SpreadFlowViewModel
import com.pinwormmy.tarotcard.ui.state.SpreadStep

@Composable
fun TarotNavGraph(
    repository: TarotRepository
) {
    val navController = rememberNavController()
    val spreadViewModel: SpreadFlowViewModel = viewModel(
        factory = SpreadFlowViewModel.Factory(repository)
    )
    val spreadUiState by spreadViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onStartReading = {
                    navController.navigate(Screen.SpreadMenu.route)
                }
            )
        }

        composable(Screen.SpreadMenu.route) {
            SpreadMenuScreen(
                onPastPresentFuture = {
                    navController.navigate(Screen.ReadingSetup.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ReadingSetup.route) {
            ReadingSetupScreen(
                positions = spreadUiState.positions,
                questionText = spreadUiState.questionText,
                useReversedCards = spreadUiState.useReversedCards,
                onBack = { navController.popBackStack() },
                onQuestionChange = { spreadViewModel.updateQuestion(it) },
                onUseReversedChange = { spreadViewModel.updateUseReversed(it) },
                onShuffle = {
                    when (spreadViewModel.startReading()) {
                        SpreadStep.ShuffleAndDraw -> navController.navigate(Screen.ShuffleAndDraw.route)
                        SpreadStep.ReadingResult -> navController.navigate(Screen.ReadingResult.route)
                        else -> Unit
                    }
                },
                onQuickReading = {
                    when (spreadViewModel.startQuickReading()) {
                        SpreadStep.ReadingResult -> navController.navigate(Screen.ReadingResult.route)
                        else -> Unit
                    }
                }
            )
        }

        composable(Screen.ShuffleAndDraw.route) {
            ShuffleAndDrawScreen(
                uiState = spreadUiState,
                positions = spreadUiState.positions,
                onDeckTap = { spreadViewModel.triggerShuffle() },
                onCutRequest = { spreadViewModel.enterCutMode() },
                onCutSelect = { index -> spreadViewModel.applyCutChoice(index) },
                onCutCancel = { spreadViewModel.cancelCutMode() },
                onShowGrid = { spreadViewModel.revealDrawGrid() },
                onCardSelected = { card ->
                    val finished = spreadViewModel.handleDrawSelection(card)
                    if (finished) {
                        navController.navigate(Screen.ReadingResult.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ReadingResult.route) {
            ReadingResultScreen(
                positions = spreadUiState.positions,
                cardsBySlot = spreadUiState.finalCards,
                onNavigateHome = {
                    spreadViewModel.resetFlow()
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.CardDetail.route,
            arguments = listOf(navArgument("cardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            val card = repository.getCard(cardId)
            CardDetailScreen(
                card = card,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private sealed class Screen(val route: String) {
    data object MainMenu : Screen("main_menu")
    data object SpreadMenu : Screen("spread_menu")
    data object ReadingSetup : Screen("reading_setup")
    data object ShuffleAndDraw : Screen("shuffle_and_draw")
    data object ReadingResult : Screen("reading_result")
    data object CardDetail : Screen("card_detail/{cardId}") {
        fun createRoute(cardId: String) = "card_detail/$cardId"
    }
}
