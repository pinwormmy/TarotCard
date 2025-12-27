package com.pinwormmy.midoritarot.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pinwormmy.midoritarot.data.DailyCardRepository
import com.pinwormmy.midoritarot.data.DrawHistoryCard
import com.pinwormmy.midoritarot.data.DrawHistoryRepository
import com.pinwormmy.midoritarot.data.TarotRepository
import com.pinwormmy.midoritarot.domain.spread.SpreadCatalog
import com.pinwormmy.midoritarot.domain.spread.SpreadSlot
import com.pinwormmy.midoritarot.domain.spread.SpreadType
import com.pinwormmy.midoritarot.ui.screens.CardBrowserScreen
import com.pinwormmy.midoritarot.ui.screens.CardDetailScreen
import com.pinwormmy.midoritarot.ui.screens.DailyCardScreen
import com.pinwormmy.midoritarot.ui.screens.DrawHistoryScreen
import com.pinwormmy.midoritarot.ui.screens.MainMenuScreen
import com.pinwormmy.midoritarot.ui.screens.OptionsScreen
import com.pinwormmy.midoritarot.ui.screens.ReadingResultScreen
import com.pinwormmy.midoritarot.ui.screens.ReadingSetupScreen
import com.pinwormmy.midoritarot.ui.screens.SpreadMenuScreen
import com.pinwormmy.midoritarot.ui.screens.ShuffleAndDrawScreen
import com.pinwormmy.midoritarot.ui.state.SpreadFlowViewModel
import com.pinwormmy.midoritarot.ui.state.SpreadCardResult
import com.pinwormmy.midoritarot.domain.spread.SpreadStep
import com.pinwormmy.midoritarot.ui.state.TarotSettingsViewModel
import com.pinwormmy.midoritarot.ui.theme.TarotSkins
import com.pinwormmy.midoritarot.ui.state.AppLanguage
import com.pinwormmy.midoritarot.R

@Composable
fun TarotNavGraph(
    repository: TarotRepository,
    dailyCardRepository: DailyCardRepository,
    drawHistoryRepository: DrawHistoryRepository,
    settingsViewModel: TarotSettingsViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val spreadViewModel: SpreadFlowViewModel = viewModel(
        factory = SpreadFlowViewModel.Factory(repository, settingsUiState.useReversedCards)
    )
    val spreadUiState by spreadViewModel.uiState.collectAsStateWithLifecycle()
    val allCards = remember(settingsUiState.language) {
        repository.getCards(locale = settingsUiState.language.toLocaleOrNull())
    }

    val navigateHomeFromReading = {
        spreadViewModel.resetFlow()
        navController.navigate(Screen.MainMenu.route) {
            popUpTo(Screen.MainMenu.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    LaunchedEffect(settingsUiState.useReversedCards) {
        spreadViewModel.applyUseReversedPreference(settingsUiState.useReversedCards)
    }

    LaunchedEffect(settingsUiState.language) {
        spreadViewModel.refreshLocaleContent(locale = settingsUiState.language.toLocaleOrNull())
    }

    fun recordHistoryFromState() {
        val state = spreadViewModel.uiState.value
        val finalBySlotId = state.finalCards.entries.associate { it.key.id to it.value }
        val orderedSlotIds = state.spread.positions
            .sortedBy { it.order }
            .map { it.slot.id }
        val cards = orderedSlotIds.mapNotNull { slotId ->
            finalBySlotId[slotId]?.let {
                DrawHistoryCard(
                    slotId = slotId,
                    cardId = it.card.id,
                    isReversed = it.isReversed,
                )
            }
        }
        if (cards.isEmpty()) return
        drawHistoryRepository.recordReading(
            spreadType = state.spread.type,
            questionText = state.questionText.trim(),
            cards = cards,
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route,
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.ime))
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onStartReading = {
                    navController.navigate(Screen.SpreadMenu.route)
                },
                onDailyCard = {
                    navController.navigate(Screen.DailyCard.route)
                },
                onOpenHistory = { navController.navigate(Screen.DrawHistory.route) },
                onBrowseCards = { navController.navigate(Screen.CardBrowser.route) },
                onOpenOptions = { navController.navigate(Screen.Options.route) }
            )
        }

        composable(Screen.SpreadMenu.route) {
            BackHandler(onBack = navigateHomeFromReading)
            SpreadMenuScreen(
                spreads = spreadViewModel.availableSpreads,
                onSpreadSelected = { type ->
                    spreadViewModel.selectSpread(type)
                    navController.navigate(Screen.ReadingSetup.route)
                },
                onBack = navigateHomeFromReading
            )
        }

        composable(Screen.ReadingSetup.route) {
            BackHandler(onBack = navigateHomeFromReading)
            ReadingSetupScreen(
                spread = spreadUiState.spread,
                questionText = spreadUiState.questionText,
                onBack = { navController.safePopBackStack() },
                onQuestionChange = { spreadViewModel.updateQuestion(it) },
                onShuffle = {
                    when (spreadViewModel.startReading()) {
                        SpreadStep.ShuffleAndDraw -> navController.navigate(Screen.ShuffleAndDraw.route)
                        SpreadStep.ReadingResult -> navController.navigate(Screen.ReadingResult.route)
                        else -> Unit
                    }
                },
                onQuickReading = {
                    spreadViewModel.startQuickReading()
                    recordHistoryFromState()
                    navController.navigate(Screen.ReadingResult.route)
                }
            )
        }

        composable(Screen.ShuffleAndDraw.route) {
            ShuffleAndDrawScreen(
                uiState = spreadUiState,
                onDeckTap = { spreadViewModel.triggerShuffle() },
                onCutRequest = { spreadViewModel.enterCutMode() },
                onCutSelect = { index -> spreadViewModel.applyCutChoice(index) },
                onShowGrid = { spreadViewModel.revealDrawGrid() },
                onCardSelected = { card ->
                    val finished = spreadViewModel.handleDrawSelection(card)
                    if (finished) {
                        recordHistoryFromState()
                        navController.navigate(Screen.ReadingResult.route)
                    }
                },
                onBack = navigateHomeFromReading
            )
        }

        composable(Screen.ReadingResult.route) {
            BackHandler(onBack = navigateHomeFromReading)
            ReadingResultScreen(
                spread = spreadUiState.spread,
                cardsBySlot = spreadUiState.finalCards,
                questionText = spreadUiState.questionText,
                onNavigateHome = {
                    navigateHomeFromReading()
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
                onBack = { navController.safePopBackStack() }
            )
        }

        composable(Screen.DailyCard.route) {
            val dailyCardResult = remember(settingsUiState.language) { dailyCardRepository.getCardForToday() }
            LaunchedEffect(dailyCardResult.card.id, dailyCardResult.isExisting) {
                if (!dailyCardResult.isExisting) {
                    drawHistoryRepository.recordReading(
                        spreadType = SpreadType.DailyCard,
                        questionText = "",
                        cards = listOf(
                            DrawHistoryCard(
                                slotId = SpreadCatalog.dailyCardSlot.id,
                                cardId = dailyCardResult.card.id,
                                isReversed = false,
                            )
                        ),
                    )
                }
            }
            DailyCardScreen(
                card = dailyCardResult.card,
                onBack = { navController.safePopBackStack() },
                showFrontImmediately = dailyCardResult.isExisting
            )
        }

        composable(Screen.CardBrowser.route) {
            CardBrowserScreen(
                cards = allCards,
                onBack = { navController.safePopBackStack() }
            )
        }

        composable(Screen.Options.route) {
            OptionsScreen(
                settings = settingsUiState,
                availableSkins = TarotSkins.all,
                availableLanguages = AppLanguage.entries.toList(),
                onSelectSkin = { settingsViewModel.selectSkin(it) },
                onSelectCardBack = { settingsViewModel.selectCardBack(it) },
                onSelectCardFace = { settingsViewModel.selectCardFace(it) },
                onSelectLanguage = { settingsViewModel.selectLanguage(it) },
                onToggleReversedCards = { settingsViewModel.toggleUseReversedCards(it) },
                onToggleDailyCard = { settingsViewModel.toggleDailyCard(it) },
                onDailyCardTimeChange = { settingsViewModel.updateDailyCardTime(it) },
                onToggleHaptics = { settingsViewModel.toggleHaptics(it) },
                onBack = { navController.safePopBackStack() }
            )
        }

        composable(Screen.DrawHistory.route) {
            val historyEntries by drawHistoryRepository.entries.collectAsStateWithLifecycle()
            val cardsById = remember(allCards) { allCards.associateBy { it.id } }
            DrawHistoryScreen(
                entries = historyEntries,
                cardsById = cardsById,
                onBack = { navController.safePopBackStack() },
                onEntrySelected = { entryId ->
                    navController.navigate(Screen.HistoryReading.createRoute(entryId))
                }
            )
        }

        composable(
            route = Screen.HistoryReading.route,
            arguments = listOf(navArgument("entryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId")
            val historyEntries by drawHistoryRepository.entries.collectAsStateWithLifecycle()
            val entry = remember(historyEntries, entryId) {
                entryId?.let { id -> historyEntries.firstOrNull { it.id == id } }
            }
            val cardsById = remember(allCards) { allCards.associateBy { it.id } }
            if (entry == null) {
                androidx.compose.material3.Text(text = stringResource(id = R.string.history_entry_not_found))
                return@composable
            }

            val spread = remember(entry.spreadType) { SpreadCatalog.find(entry.spreadType) }
            val cardsBySlot = remember(entry, cardsById) {
                entry.cards.mapNotNull { card ->
                    val model = cardsById[card.cardId] ?: return@mapNotNull null
                    SpreadSlot(card.slotId) to SpreadCardResult(
                        card = model,
                        isReversed = card.isReversed,
                    )
                }.toMap()
            }

            ReadingResultScreen(
                spread = spread,
                cardsBySlot = cardsBySlot,
                questionText = entry.questionText,
                startRevealed = true,
                actionLabelResId = R.string.back,
                onNavigateHome = { navController.safePopBackStack() }
            )
        }
    }
}

private fun androidx.navigation.NavController.safePopBackStack(): Boolean {
    val hasPrevious = previousBackStackEntry != null
    return if (hasPrevious) popBackStack() else false
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
    data object DrawHistory : Screen("draw_history")
    data object HistoryReading : Screen("history_reading/{entryId}") {
        fun createRoute(entryId: String) = "history_reading/$entryId"
    }
    data object CardDetail : Screen("card_detail/{cardId}") {
        @Suppress("unused")
        fun createRoute(cardId: String) = "card_detail/$cardId"
    }
}
