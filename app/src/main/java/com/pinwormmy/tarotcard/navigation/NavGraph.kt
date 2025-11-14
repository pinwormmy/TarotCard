package com.pinwormmy.tarotcard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pinwormmy.tarotcard.data.TarotRepository
import com.pinwormmy.tarotcard.ui.screens.CardDetailScreen
import com.pinwormmy.tarotcard.ui.screens.CardLibraryScreen
import com.pinwormmy.tarotcard.ui.screens.ReadingResultScreen
import com.pinwormmy.tarotcard.ui.screens.ShuffleAndDrawScreen
import com.pinwormmy.tarotcard.ui.screens.SpreadSelectionScreen
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
        startDestination = Screen.Preselection.route
    ) {
        composable(Screen.Preselection.route) {
            SpreadSelectionScreen(
                positions = spreadUiState.positions,
                preselectionState = spreadUiState.preselection,
                onPickCard = { slot ->
                    spreadViewModel.prepareCardSelection(slot)
                    navController.navigate(Screen.CardLibrary.route)
                },
                onStartReading = {
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

        composable(Screen.CardLibrary.route) {
            val targetSlot = spreadUiState.targetSlotForLibrary
            if (targetSlot == null) {
                LaunchedEffect(targetSlot) {
                    navController.popBackStack()
                }
            } else {
                val slotTitle = spreadUiState.positions.firstOrNull { it.slot == targetSlot }?.title
                    ?: "카드"
                CardLibraryScreen(
                    cards = spreadUiState.availableCards,
                    selectedCategory = spreadUiState.selectedCategory,
                    targetSlotTitle = slotTitle,
                    onCategoryChange = { spreadViewModel.updateCategory(it) },
                    onCardSelected = { card ->
                        spreadViewModel.assignPreselectedCard(targetSlot, card)
                    },
                    onBack = {
                        spreadViewModel.clearTargetSlot()
                    }
                )
            }
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
                onRestart = {
                    spreadViewModel.resetFlow()
                    navController.navigate(Screen.Preselection.route) {
                        popUpTo(Screen.Preselection.route) { inclusive = true }
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
    data object Preselection : Screen("preselection")
    data object CardLibrary : Screen("card_library")
    data object ShuffleAndDraw : Screen("shuffle_and_draw")
    data object ReadingResult : Screen("reading_result")
    data object CardDetail : Screen("card_detail/{cardId}") {
        fun createRoute(cardId: String) = "card_detail/$cardId"
    }
}
