package com.pinwormmy.tarotcard.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.data.TarotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

enum class CardCategory(val displayName: String) {
    MajorArcana("Major Arcana"),
    Wands("Wands"),
    Cups("Cups"),
    Swords("Swords"),
    Pentacles("Pentacles")
}

data class SpreadFlowUiState(
    val step: SpreadStep = SpreadStep.Preselection,
    val spread: SpreadDefinition = SpreadCatalog.default,
    val questionText: String = "",
    val useReversedCards: Boolean = SpreadCatalog.default.defaultUseReversed,
    val shuffleTrigger: Int = 0,
    val drawPile: List<TarotCardModel> = emptyList(),
    val pendingSlots: List<SpreadSlot> = emptyList(),
    val drawnCards: Map<SpreadSlot, SpreadCardResult> = emptyMap(),
    val finalCards: Map<SpreadSlot, SpreadCardResult> = emptyMap(),
    val gridVisible: Boolean = false,
    val statusMessage: String? = null,
    val cutMode: Boolean = false
)

data class SpreadCardResult(
    val card: TarotCardModel,
    val isReversed: Boolean
)

class SpreadFlowViewModel(
    repository: TarotRepository
) : ViewModel() {

    val availableSpreads: List<SpreadDefinition> = SpreadCatalog.all

    private val allCards = repository.getCards()

    private val _uiState = MutableStateFlow(
        SpreadFlowUiState(
            spread = SpreadCatalog.default,
            drawPile = allCards,
            useReversedCards = SpreadCatalog.default.defaultUseReversed
        )
    )
    val uiState: StateFlow<SpreadFlowUiState> = _uiState.asStateFlow()

    private fun slotsFor(spread: SpreadDefinition): List<SpreadSlot> =
        spread.positions.sortedBy { it.order }.map { it.slot }

    fun selectSpread(type: SpreadType) {
        val target = SpreadCatalog.find(type)
        _uiState.value = SpreadFlowUiState(
            step = SpreadStep.Preselection,
            spread = target,
            questionText = "",
            useReversedCards = target.defaultUseReversed,
            drawPile = allCards,
            finalCards = emptyMap()
        )
    }

    fun updateQuestion(text: String) {
        updateState { it.copy(questionText = text) }
    }

    fun updateUseReversed(enabled: Boolean) {
        updateState { it.copy(useReversedCards = enabled) }
    }

    fun startReading(): SpreadStep {
        val spread = _uiState.value.spread
        val pendingSlots = slotsFor(spread)
        val fixedCards = emptyMap<SpreadSlot, SpreadCardResult>()

        return if (pendingSlots.isEmpty()) {
            updateState {
                it.copy(
                    finalCards = fixedCards,
                    pendingSlots = emptyList(),
                    drawnCards = emptyMap(),
                    gridVisible = false,
                    statusMessage = null,
                    cutMode = false,
                    step = SpreadStep.ReadingResult
                )
            }
            SpreadStep.ReadingResult
        } else {
            val remaining = allCards.shuffled(Random(System.currentTimeMillis()))
            updateState {
                it.copy(
                    finalCards = fixedCards,
                    pendingSlots = pendingSlots,
                    drawnCards = emptyMap(),
                    drawPile = remaining,
                    gridVisible = false,
                    statusMessage = null,
                    step = SpreadStep.ShuffleAndDraw,
                    cutMode = false
                )
            }
            SpreadStep.ShuffleAndDraw
        }
    }

    fun startQuickReading(): SpreadStep {
        val current = _uiState.value
        val pendingSlots = slotsFor(current.spread)
        if (pendingSlots.isEmpty()) {
            return SpreadStep.ReadingResult
        }
        val remaining = allCards.shuffled(Random(System.currentTimeMillis()))
        val assignments = pendingSlots.mapIndexedNotNull { index, slot ->
            val card = remaining.getOrNull(index) ?: return@mapIndexedNotNull null
            slot to SpreadCardResult(
                card = card,
                isReversed = current.useReversedCards && Random.nextBoolean()
            )
        }.toMap()
        updateState {
            it.copy(
                finalCards = assignments,
                pendingSlots = emptyList(),
                drawnCards = emptyMap(),
                step = SpreadStep.ReadingResult,
                gridVisible = false,
                cutMode = false,
                statusMessage = null
            )
        }
        return SpreadStep.ReadingResult
    }

    fun triggerShuffle() {
        updateState {
            it.copy(
                shuffleTrigger = it.shuffleTrigger + 1,
                statusMessage = null
            )
        }
    }

    fun revealDrawGrid() {
        updateState { it.copy(gridVisible = true) }
    }

    fun enterCutMode() {
        updateState { it.copy(cutMode = true, statusMessage = null) }
    }

    fun cancelCutMode() {
        updateState { it.copy(cutMode = false, statusMessage = null) }
    }

    fun applyCutChoice(stackIndex: Int) {
        updateState { state ->
            if (state.drawPile.isEmpty()) {
                return@updateState state.copy(cutMode = false)
            }
            val stacks = splitDrawPile(state.drawPile)
            val normalizedIndex = stackIndex.coerceIn(0, stacks.lastIndex)
            val reordered = buildList {
                addAll(stacks[normalizedIndex])
                stacks.forEachIndexed { index, stack ->
                    if (index != normalizedIndex) addAll(stack)
                }
            }
            state.copy(
                drawPile = reordered,
                cutMode = false,
                statusMessage = state.statusMessage
            )
        }
    }

    fun handleDrawSelection(card: TarotCardModel): Boolean {
        var shouldShowResult = false
        updateState { state ->
            if (state.pendingSlots.isEmpty()) return@updateState state
            if (state.drawnCards.values.any { it.card.id == card.id }) return@updateState state
            val nextIndex = state.drawnCards.size
            if (nextIndex >= state.pendingSlots.size) return@updateState state

            val nextSlot = state.pendingSlots[nextIndex]
            val placement = SpreadCardResult(
                card = card,
                isReversed = state.useReversedCards && Random.nextBoolean()
            )
            val updatedDrawn = state.drawnCards + (nextSlot to placement)
            val updatedFinal = state.finalCards + (nextSlot to placement)
            val titleLookup = state.spread.positions.associateBy { it.slot }
            val message = titleLookup[nextSlot]?.let { "${it.title} 카드를 선택했습니다." }
            shouldShowResult = updatedDrawn.size == state.pendingSlots.size
            state.copy(
                drawnCards = updatedDrawn,
                finalCards = updatedFinal,
                statusMessage = message
            )
        }
        if (shouldShowResult) {
            updateState { it.copy(step = SpreadStep.ReadingResult) }
        }
        return shouldShowResult
    }

    fun resetFlow() {
        val current = _uiState.value
        _uiState.value = SpreadFlowUiState(
            step = SpreadStep.Preselection,
            spread = current.spread,
            questionText = "",
            useReversedCards = current.useReversedCards,
            drawPile = allCards,
            finalCards = emptyMap()
        )
    }

    private fun splitDrawPile(pile: List<TarotCardModel>): List<List<TarotCardModel>> {
        if (pile.isEmpty()) return listOf(emptyList(), emptyList(), emptyList())
        val baseSize = (pile.size / 3).coerceAtLeast(1)
        val first = pile.take(baseSize)
        val second = pile.drop(baseSize).take(baseSize)
        val third = pile.drop(baseSize * 2)
        return listOf(first, second, third)
    }

    private fun updateState(transform: (SpreadFlowUiState) -> SpreadFlowUiState) {
        _uiState.update(transform)
    }

    companion object {
        fun Factory(repository: TarotRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SpreadFlowViewModel::class.java)) {
                        return SpreadFlowViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
    }
}

fun TarotCardModel.category(): CardCategory {
    val normalizedId = id.lowercase()
    return when {
        normalizedId.startsWith("major_") -> CardCategory.MajorArcana
        normalizedId.startsWith("wands_") -> CardCategory.Wands
        normalizedId.startsWith("cups_") -> CardCategory.Cups
        normalizedId.startsWith("swords_") -> CardCategory.Swords
        normalizedId.startsWith("pentacles_") -> CardCategory.Pentacles
        else -> {
            val value = arcana.lowercase()
            when {
                value.contains("wand") -> CardCategory.Wands
                value.contains("cup") -> CardCategory.Cups
                value.contains("sword") -> CardCategory.Swords
                value.contains("pentacle") || value.contains("coin") -> CardCategory.Pentacles
                else -> CardCategory.MajorArcana
            }
        }
    }
}
