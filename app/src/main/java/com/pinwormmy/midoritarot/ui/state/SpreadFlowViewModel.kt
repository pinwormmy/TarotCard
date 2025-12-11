package com.pinwormmy.midoritarot.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pinwormmy.midoritarot.data.TarotRepository
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.domain.spread.SpreadCatalog
import com.pinwormmy.midoritarot.domain.spread.SpreadDefinition
import com.pinwormmy.midoritarot.domain.spread.SpreadSlot
import com.pinwormmy.midoritarot.domain.spread.SpreadStep
import com.pinwormmy.midoritarot.domain.spread.SpreadType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random
import java.util.Locale

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
    val cutMode: Boolean = false,
    val nextInstruction: String? = null
)

data class SpreadCardResult(
    val card: TarotCardModel,
    val isReversed: Boolean
)

class SpreadFlowViewModel(
    repository: TarotRepository,
    initialUseReversed: Boolean
) : ViewModel() {

    val availableSpreads: List<SpreadDefinition> = SpreadCatalog.all

    private val allCards = repository.getCards()
    private val random = Random(System.currentTimeMillis())
    private var useReversedPreference: Boolean = initialUseReversed

    private val _uiState = MutableStateFlow(baseState(SpreadCatalog.default, useReversedPreference))
    val uiState: StateFlow<SpreadFlowUiState> = _uiState.asStateFlow()

    private fun slotsFor(spread: SpreadDefinition): List<SpreadSlot> =
        spread.positions.sortedBy { it.order }.map { it.slot }

    private fun instructionFor(spread: SpreadDefinition, index: Int): String? {
        val position = spread.positions.getOrNull(index) ?: return null
        val title = position.title.resolve()
        val lang = Locale.getDefault().language.lowercase()
        return if (lang == "en") {
            "Select the $title card."
        } else {
            "$title 카드를 선택하세요."
        }
    }

    fun selectSpread(type: SpreadType) {
        val target = SpreadCatalog.find(type)
        _uiState.value = baseState(target, useReversedPreference)
    }

    fun updateQuestion(text: String) {
        updateState { it.copy(questionText = text) }
    }

    fun applyUseReversedPreference(enabled: Boolean) {
        useReversedPreference = enabled
        updateState { state ->
            if (state.useReversedCards == enabled) {
                state
            } else {
                state.copy(useReversedCards = enabled)
            }
        }
    }

    fun startReading(): SpreadStep {
        val spread = _uiState.value.spread
        val pendingSlots = slotsFor(spread)
        return if (pendingSlots.isEmpty()) {
            updateState {
                it.copy(
                    finalCards = emptyMap(),
                    pendingSlots = emptyList(),
                    drawnCards = emptyMap(),
                    gridVisible = false,
                    statusMessage = null,
                    cutMode = false,
                    step = SpreadStep.ReadingResult,
                    nextInstruction = null
                )
            }
            SpreadStep.ReadingResult
        } else {
            updateState {
                it.copy(
                    finalCards = emptyMap(),
                    pendingSlots = pendingSlots,
                    drawnCards = emptyMap(),
                    drawPile = newShuffledDeck(),
                    gridVisible = false,
                    statusMessage = null,
                    step = SpreadStep.ShuffleAndDraw,
                    cutMode = false,
                    nextInstruction = null
                )
            }
            SpreadStep.ShuffleAndDraw
        }
    }

    fun startQuickReading() {
        val spread = _uiState.value.spread
        val pendingSlots = slotsFor(spread)
        if (pendingSlots.isEmpty()) {
            updateState {
                it.copy(
                    finalCards = emptyMap(),
                    pendingSlots = emptyList(),
                    drawnCards = emptyMap(),
                    step = SpreadStep.ReadingResult,
                    gridVisible = false,
                    cutMode = false,
                    statusMessage = null,
                    nextInstruction = null
                )
            }
            return
        }

        val remaining = newShuffledDeck()
        val assignments = pendingSlots.mapIndexedNotNull { index, slot ->
            val card = remaining.getOrNull(index) ?: return@mapIndexedNotNull null
            slot to SpreadCardResult(
                card = card,
                isReversed = randomReversed(_uiState.value.useReversedCards)
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
                statusMessage = null,
                drawPile = remaining,
                nextInstruction = null
            )
        }
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
        updateState { state ->
            state.copy(
                gridVisible = true,
                nextInstruction = instructionFor(state.spread, state.drawnCards.size)
            )
        }
    }

    fun enterCutMode() {
        updateState { it.copy(cutMode = true, statusMessage = null) }
    }

    @Suppress("unused")
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
                isReversed = randomReversed(state.useReversedCards)
            )
            val updatedDrawn = state.drawnCards + (nextSlot to placement)
            val updatedFinal = state.finalCards + (nextSlot to placement)
            val titleLookup = state.spread.positions.associateBy { it.slot }
            val message = titleLookup[nextSlot]?.let { position ->
                val title = position.title.resolve()
                val lang = Locale.getDefault().language.lowercase()
                if (lang == "en") {
                    "You picked the $title card."
                } else {
                    "$title 카드를 선택했습니다."
                }
            }
            shouldShowResult = updatedDrawn.size == state.pendingSlots.size
            val nextInstruction = instructionFor(state.spread, nextIndex + 1)
            state.copy(
                drawnCards = updatedDrawn,
                finalCards = updatedFinal,
                statusMessage = message,
                nextInstruction = if (shouldShowResult) null else nextInstruction
            )
        }
        if (shouldShowResult) {
            updateState { it.copy(step = SpreadStep.ReadingResult) }
        }
        return shouldShowResult
    }

    fun resetFlow() {
        val current = _uiState.value
        _uiState.value = baseState(
            spread = current.spread,
            useReversed = useReversedPreference
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

    private fun baseState(
        spread: SpreadDefinition,
        useReversed: Boolean = useReversedPreference
    ): SpreadFlowUiState =
        SpreadFlowUiState(
            step = SpreadStep.Preselection,
            spread = spread,
            questionText = "",
            useReversedCards = useReversed,
            drawPile = allCards,
            pendingSlots = emptyList(),
            drawnCards = emptyMap(),
            finalCards = emptyMap(),
            gridVisible = false,
            statusMessage = null,
            cutMode = false,
            nextInstruction = null,
            shuffleTrigger = 0
        )

    private fun newShuffledDeck(): List<TarotCardModel> =
        allCards.shuffled(random)

    private fun randomReversed(enabled: Boolean): Boolean =
        enabled && random.nextBoolean()

    private fun updateState(transform: (SpreadFlowUiState) -> SpreadFlowUiState) {
        _uiState.update(transform)
    }

    companion object {
        fun Factory(
            repository: TarotRepository,
            initialUseReversed: Boolean
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SpreadFlowViewModel::class.java)) {
                        return SpreadFlowViewModel(repository, initialUseReversed) as T
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
        normalizedId.startsWith("pentacles_") || normalizedId.startsWith("pents_") -> CardCategory.Pentacles
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
