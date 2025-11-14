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
import com.pinwormmy.tarotcard.ui.screens.CardLibraryScreen
import com.pinwormmy.tarotcard.ui.screens.PositionSelectScreen
import com.pinwormmy.tarotcard.ui.screens.ReadingResultScreen
import com.pinwormmy.tarotcard.ui.screens.ReadingSetupScreen
import com.pinwormmy.tarotcard.ui.screens.ShuffleAndDrawScreen
import com.pinwormmy.tarotcard.ui.screens.SpreadSelectionScreen
import com.pinwormmy.tarotcard.ui.state.SpreadFlowViewModel

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
        startDestination = Screen.SpreadSelection.route
    ) {
        composable(Screen.SpreadSelection.route) {
            SpreadSelectionScreen(
                onStartPositions = {
                    spreadViewModel.moveToPositionSelect()
                    navController.navigate(Screen.PositionSelect.route)
                },
                onQuickReading = {
                    spreadViewModel.moveToReadingSetup()
                    navController.navigate(Screen.ReadingSetup.route)
                }
            )
        }

        composable(Screen.PositionSelect.route) {
            PositionSelectScreen(
                positions = spreadUiState.positions,
                currentIndex = spreadUiState.currentPositionIndex,
                selectedCards = spreadUiState.selectedPositionCards,
                onPickCard = { navController.navigate(Screen.CardLibrary.route) },
                onPrevious = { spreadViewModel.goToPreviousPosition() },
                onNext = { spreadViewModel.goToNextPosition() },
                onContinue = {
                    spreadViewModel.moveToReadingSetup()
                    navController.navigate(Screen.ReadingSetup.route)
                },
                canContinue = spreadUiState.isPositionSelectionComplete
            )
        }

        composable(Screen.CardLibrary.route) {
            CardLibraryScreen(
                cards = spreadUiState.availableCards,
                selectedCategory = spreadUiState.selectedCategory,
                onCategoryChange = { spreadViewModel.updateCategory(it) },
                onCardSelected = { card ->
                    spreadViewModel.selectCardForCurrentPosition(card)
                    spreadViewModel.goToNextPosition()
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ReadingSetup.route) {
            ReadingSetupScreen(
                question = spreadUiState.question,
                useReversed = spreadUiState.useReversed,
                selectedCount = spreadUiState.selectedPositionCards.count { it != null },
                canImmediateReading = spreadUiState.isPositionSelectionComplete,
                onQuestionChange = { spreadViewModel.updateQuestion(it) },
                onToggleReversed = { spreadViewModel.toggleUseReversed() },
                onShuffleRequested = {
                    spreadViewModel.moveToShuffle()
                    navController.navigate(Screen.ShuffleAndDraw.route)
                },
                onImmediateReading = {
                    if (spreadViewModel.immediateReadingFromSelections()) {
                        navController.navigate(Screen.ReadingResult.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ShuffleAndDraw.route) {
            ShuffleAndDrawScreen(
                uiState = spreadUiState,
                onDeckTap = { spreadViewModel.triggerShuffle() },
                onCut = { spreadViewModel.cutDeck() },
                onDrawToggle = { spreadViewModel.revealDrawGrid() },
                onCardSelected = { spreadViewModel.selectDrawCard(it) },
                onComplete = {
                    if (spreadViewModel.completeDrawIfReady()) {
                        navController.navigate(Screen.ReadingResult.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ReadingResult.route) {
            ReadingResultScreen(
                cards = spreadUiState.drawnCards,
                question = spreadUiState.question,
                useReversed = spreadUiState.useReversed,
                onRestart = {
                    spreadViewModel.resetFlow()
                    val startRoute = navController.graph.startDestinationRoute
                        ?: Screen.SpreadSelection.route
                    navController.navigate(Screen.SpreadSelection.route) {
                        popUpTo(startRoute) { inclusive = true }
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
    data object SpreadSelection : Screen("spread_selection")
    data object PositionSelect : Screen("position_select")
    data object CardLibrary : Screen("card_library")
    data object ReadingSetup : Screen("reading_setup")
    data object ShuffleAndDraw : Screen("shuffle_and_draw")
    data object ReadingResult : Screen("reading_result")
    data object CardDetail : Screen("card_detail/{cardId}") {
        fun createRoute(cardId: String) = "card_detail/$cardId"
    }
}
