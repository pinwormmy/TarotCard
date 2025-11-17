package com.pinwormmy.tarotcard.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pinwormmy.tarotcard.data.TarotRepository
import com.pinwormmy.tarotcard.ui.screens.CardBrowserScreen
import com.pinwormmy.tarotcard.ui.screens.CardDetailScreen
import com.pinwormmy.tarotcard.ui.screens.DailyCardScreen
import com.pinwormmy.tarotcard.ui.screens.MainMenuScreen
import com.pinwormmy.tarotcard.ui.screens.OptionsScreen
import com.pinwormmy.tarotcard.ui.screens.ReadingResultScreen
import com.pinwormmy.tarotcard.ui.screens.ReadingSetupScreen
import com.pinwormmy.tarotcard.ui.screens.SpreadMenuScreen
import com.pinwormmy.tarotcard.ui.screens.ShuffleAndDrawScreen
import com.pinwormmy.tarotcard.ui.state.SpreadFlowViewModel
import com.pinwormmy.tarotcard.ui.state.SpreadStep
import com.pinwormmy.tarotcard.ui.state.TarotSettingsViewModel
import com.pinwormmy.tarotcard.ui.theme.TarotSkins

@Composable
fun TarotNavGraph(
    repository: TarotRepository,
    settingsViewModel: TarotSettingsViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val spreadViewModel: SpreadFlowViewModel = viewModel(
        factory = SpreadFlowViewModel.Factory(repository)
    )
    val spreadUiState by spreadViewModel.uiState.collectAsState()
    val allCards = remember { repository.getCards() }
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route,
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onStartReading = {
                    navController.navigate(Screen.SpreadMenu.route)
                },
                onDailyCard = {
                    navController.navigate(Screen.DailyCard.route)
                },
                onBrowseCards = { navController.navigate(Screen.CardBrowser.route) },
                onOpenOptions = { navController.navigate(Screen.Options.route) }
            )
        }

        composable(Screen.SpreadMenu.route) {
            SpreadMenuScreen(
                spreads = spreadViewModel.availableSpreads,
                onSpreadSelected = { type ->
                    spreadViewModel.selectSpread(type)
                    navController.navigate(Screen.ReadingSetup.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ReadingSetup.route) {
            ReadingSetupScreen(
                spread = spreadUiState.spread,
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
                spread = spreadUiState.spread,
                cardsBySlot = spreadUiState.finalCards,
                questionText = spreadUiState.questionText,
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

        composable(Screen.DailyCard.route) {
            val card = remember { allCards.random() }
            DailyCardScreen(
                card = card,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CardBrowser.route) {
            CardBrowserScreen(
                cards = allCards,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Options.route) {
            OptionsScreen(
                settings = settingsUiState,
                availableSkins = TarotSkins.all,
                onSelectSkin = { settingsViewModel.selectSkin(it) },
                onSelectCardBack = { settingsViewModel.selectCardBack(it) },
                onSelectCardFace = { settingsViewModel.selectCardFace(it) },
                onToggleDailyCard = { settingsViewModel.toggleDailyCard(it) },
                onDailyCardTimeChange = { settingsViewModel.updateDailyCardTime(it) },
                onToggleHaptics = { settingsViewModel.toggleHaptics(it) },
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
    data object DailyCard : Screen("daily_card")
    data object CardBrowser : Screen("card_browser")
    data object Options : Screen("options")
    data object CardDetail : Screen("card_detail/{cardId}") {
        fun createRoute(cardId: String) = "card_detail/$cardId"
    }
}
