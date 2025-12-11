package com.pinwormmy.midoritarot.ui.state

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.test.core.app.ApplicationProvider
import com.pinwormmy.midoritarot.data.TarotRepository
import com.pinwormmy.midoritarot.domain.spread.SpreadStep
import com.pinwormmy.midoritarot.domain.spread.SpreadType
import java.util.Locale
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SpreadFlowViewModelTest {

    private lateinit var repository: TarotRepository
    private lateinit var viewModel: SpreadFlowViewModel
    private lateinit var originalLocales: LocaleListCompat
    private var originalDefaultLocale: Locale = Locale.getDefault()

    @Before
    fun setUp() {
        originalLocales = AppCompatDelegate.getApplicationLocales()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
        Locale.setDefault(Locale.ENGLISH)

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        repository = TarotRepository(context)
        viewModel = SpreadFlowViewModel(repository, initialUseReversed = true)
    }

    @After
    fun tearDown() {
        AppCompatDelegate.setApplicationLocales(originalLocales)
        Locale.setDefault(originalDefaultLocale)
    }

    @Test
    fun startReading_movesToShuffleAndDraw_withPendingSlots() {
        val resultStep = viewModel.startReading()
        val state = viewModel.uiState.value

        assertEquals(SpreadStep.ShuffleAndDraw, resultStep)
        assertEquals(SpreadStep.ShuffleAndDraw, state.step)
        assertEquals(state.spread.positions.size, state.pendingSlots.size)
        assertTrue(state.drawPile.isNotEmpty())
        assertTrue(state.drawnCards.isEmpty())
        assertTrue(state.finalCards.isEmpty())
    }

    @Test
    fun startQuickReading_assignsAllSlotsAndFinishes() {
        viewModel.selectSpread(SpreadType.PastPresentFuture)

        viewModel.startQuickReading()
        val state = viewModel.uiState.value

        assertEquals(SpreadStep.ReadingResult, state.step)
        assertEquals(state.spread.positions.size, state.finalCards.size)
        assertTrue(state.pendingSlots.isEmpty())
        assertTrue(state.drawnCards.isEmpty())
    }

    @Test
    fun handleDrawSelection_fillsSlotsInOrder_andEndsAtResult() {
        viewModel.startReading()
        val cards = repository.getCards()
        val slots = viewModel.uiState.value.pendingSlots

        var lastResult = false
        slots.forEachIndexed { index, slot ->
            lastResult = viewModel.handleDrawSelection(cards[index])
            val current = viewModel.uiState.value
            assertEquals(index + 1, current.drawnCards.size)
            assertTrue(current.drawnCards.containsKey(slot))
        }

        val finalState = viewModel.uiState.value
        assertTrue(lastResult)
        assertEquals(SpreadStep.ReadingResult, finalState.step)
        assertEquals(finalState.drawnCards.size, finalState.finalCards.size)
    }

    @Test
    fun handleDrawSelection_ignoresDuplicateCard() {
        viewModel.startReading()
        val firstCard = repository.getCards().first()

        viewModel.handleDrawSelection(firstCard)
        val before = viewModel.uiState.value.drawnCards.size
        viewModel.handleDrawSelection(firstCard)
        val after = viewModel.uiState.value.drawnCards.size

        assertEquals(before, after)
    }

    @Test
    fun selectSpread_resetsStateToChosenSpread() {
        viewModel.selectSpread(SpreadType.EnergyAdvice)
        val state = viewModel.uiState.value

        assertEquals(SpreadType.EnergyAdvice, state.spread.type)
        assertEquals(SpreadStep.Preselection, state.step)
        assertTrue(state.pendingSlots.isEmpty())
    }

    @Test
    fun resetFlow_restoresPreselectionWithSameSpread() {
        viewModel.startReading()

        viewModel.resetFlow()
        val state = viewModel.uiState.value

        assertEquals(SpreadStep.Preselection, state.step)
        assertTrue(state.drawnCards.isEmpty())
        assertTrue(state.finalCards.isEmpty())
        assertTrue(state.drawPile.isNotEmpty())
    }

    @Test
    fun applyUseReversedPreference_updatesFlag() {
        viewModel.applyUseReversedPreference(false)

        val state = viewModel.uiState.value
        assertFalse(state.useReversedCards)
    }

    @Test
    fun revealDrawGrid_setsNextInstruction() {
        viewModel.startReading()
        viewModel.revealDrawGrid()

        val state = viewModel.uiState.value
        assertTrue(state.gridVisible)
        assertTrue(state.nextInstruction?.isNotBlank() == true)
    }

    @Test
    fun handleDrawSelection_usesAppLocaleForMessages() {
        val originalLocales = AppCompatDelegate.getApplicationLocales()
        val originalDefault = Locale.getDefault()
        try {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ja"))
            Locale.setDefault(Locale.JAPAN)

            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            val jaRepository = TarotRepository(context)
            val jaViewModel = SpreadFlowViewModel(jaRepository, initialUseReversed = true)

            jaViewModel.startReading()
            val firstCard = jaRepository.getCards().first()

            jaViewModel.handleDrawSelection(firstCard)
            val status = jaViewModel.uiState.value.statusMessage

            assertTrue(status?.contains("カードを選びました") == true)
        } finally {
            AppCompatDelegate.setApplicationLocales(originalLocales)
            Locale.setDefault(originalDefault)
        }
    }

    @Test
    fun handleDrawSelection_defaultsToEnglishForUnsupportedLanguage() {
        val originalLocales = AppCompatDelegate.getApplicationLocales()
        val originalDefault = Locale.getDefault()
        try {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("fr"))
            Locale.setDefault(Locale.FRANCE)

            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            val repo = TarotRepository(context)
            val vm = SpreadFlowViewModel(repo, initialUseReversed = true)

            vm.startReading()
            val firstCard = repo.getCards().first()

            vm.handleDrawSelection(firstCard)
            val status = vm.uiState.value.statusMessage

            assertTrue(status?.contains("You picked") == true)
        } finally {
            AppCompatDelegate.setApplicationLocales(originalLocales)
            Locale.setDefault(originalDefault)
        }
    }
}
