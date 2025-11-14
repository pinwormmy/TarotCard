package com.pinwormmy.tarotcard.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.data.TarotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class SpreadSlot {
    Past,
    Present,
    Future
}

data class SpreadPosition(
    val slot: SpreadSlot,
    val title: String,
    val description: String
)

data class SpreadPreselectionState(
    val past: TarotCardModel? = null,
    val present: TarotCardModel? = null,
    val future: TarotCardModel? = null
) {
    val selectedCount: Int
        get() = listOf(past, present, future).count { it != null }

    fun usedIds(): Set<String> = buildSet {
        past?.let { add(it.id) }
        present?.let { add(it.id) }
        future?.let { add(it.id) }
    }

    fun get(slot: SpreadSlot): TarotCardModel? = when (slot) {
        SpreadSlot.Past -> past
        SpreadSlot.Present -> present
        SpreadSlot.Future -> future
    }

    fun set(slot: SpreadSlot, card: TarotCardModel?): SpreadPreselectionState = when (slot) {
        SpreadSlot.Past -> copy(past = card)
        SpreadSlot.Present -> copy(present = card)
        SpreadSlot.Future -> copy(future = card)
    }

    fun removeCard(cardId: String): SpreadPreselectionState = copy(
        past = past.takeUnless { it?.id == cardId },
        present = present.takeUnless { it?.id == cardId },
        future = future.takeUnless { it?.id == cardId }
    )

    fun toFilledMap(): Map<SpreadSlot, TarotCardModel> = buildMap {
        past?.let { put(SpreadSlot.Past, it) }
        present?.let { put(SpreadSlot.Present, it) }
        future?.let { put(SpreadSlot.Future, it) }
    }
}

enum class CardCategory(val displayName: String) {
    MajorArcana("Major Arcana"),
    Wands("Wands"),
    Cups("Cups"),
    Swords("Swords"),
    Pentacles("Pentacles")
}

data class SpreadFlowUiState(
    val step: SpreadStep = SpreadStep.Preselection,
    val positions: List<SpreadPosition> = emptyList(),
    val preselection: SpreadPreselectionState = SpreadPreselectionState(),
    val targetSlotForLibrary: SpreadSlot? = null,
    val selectedCategory: CardCategory = CardCategory.MajorArcana,
    val availableCards: List<TarotCardModel> = emptyList(),
    val shuffleTrigger: Int = 0,
    val drawPile: List<TarotCardModel> = emptyList(),
    val pendingSlots: List<SpreadSlot> = emptyList(),
    val drawnCards: Map<SpreadSlot, TarotCardModel> = emptyMap(),
    val finalCards: Map<SpreadSlot, TarotCardModel> = emptyMap(),
    val gridVisible: Boolean = false,
    val statusMessage: String? = null,
    val cutMode: Boolean = false
)

class SpreadFlowViewModel(
    repository: TarotRepository
) : ViewModel() {

    private val positions = listOf(
        SpreadPosition(
            slot = SpreadSlot.Past,
            title = "과거",
            description = "과거가 현재 상황에 어떤 영향을 미치나요?"
        ),
        SpreadPosition(
            slot = SpreadSlot.Present,
            title = "현재",
            description = "현재 상황은 어떤가요?"
        ),
        SpreadPosition(
            slot = SpreadSlot.Future,
            title = "미래",
            description = "이 상황의 잠재적 결과는 무엇인가요?"
        )
    )

    private val allCards = repository.getCards()

    private val _uiState = MutableStateFlow(
        SpreadFlowUiState(
            positions = positions,
            availableCards = filteredAvailableCards(SpreadPreselectionState()),
            drawPile = allCards
        )
    )
    val uiState: StateFlow<SpreadFlowUiState> = _uiState.asStateFlow()

    fun prepareCardSelection(slot: SpreadSlot) {
        updateState {
            it.copy(
                targetSlotForLibrary = slot,
                availableCards = filteredAvailableCards(it.preselection)
            )
        }
    }

    fun assignPreselectedCard(slot: SpreadSlot, card: TarotCardModel) {
        updateState { state ->
            val cleaned = state.preselection.removeCard(card.id)
            val updated = cleaned.set(slot, card)
            state.copy(
                preselection = updated,
                targetSlotForLibrary = null,
                availableCards = filteredAvailableCards(updated)
            )
        }
    }

    fun clearTargetSlot() {
        updateState {
            it.copy(
                targetSlotForLibrary = null,
                availableCards = filteredAvailableCards(it.preselection)
            )
        }
    }

    fun updateCategory(category: CardCategory) {
        updateState { it.copy(selectedCategory = category) }
    }

    fun startReading(): SpreadStep {
        val current = _uiState.value
        val fixedCards = current.preselection.toFilledMap()
        val pendingSlots = SpreadSlot.values().filter { current.preselection.get(it) == null }

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
            val bannedIds = fixedCards.values.map { it.id }.toSet()
            val remaining = allCards.filterNot { it.id in bannedIds }
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
        val base = current.preselection.toFilledMap().toMutableMap()
        val pendingSlots = SpreadSlot.values().filter { current.preselection.get(it) == null }
        val bannedIds = base.values.map { it.id }.toMutableSet()
        val remaining = allCards.filterNot { it.id in bannedIds }.shuffled()
        val assignments = pendingSlots.mapIndexedNotNull { index, slot ->
            val card = remaining.getOrNull(index) ?: return@mapIndexedNotNull null
            bannedIds.add(card.id)
            slot to card
        }.toMap()
        base.putAll(assignments)
        updateState {
            it.copy(
                finalCards = base.toMap(),
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
            if (state.drawnCards.values.any { it.id == card.id }) return@updateState state
            val nextIndex = state.drawnCards.size
            if (nextIndex >= state.pendingSlots.size) return@updateState state

            val nextSlot = state.pendingSlots[nextIndex]
            val updatedDrawn = state.drawnCards + (nextSlot to card)
            val updatedFinal = state.finalCards + (nextSlot to card)
            val updatedPile = state.drawPile.filterNot { it.id == card.id }
            val message = when (nextSlot) {
                SpreadSlot.Past -> "과거 카드를 선택했습니다."
                SpreadSlot.Present -> "현재 카드를 선택했습니다."
                SpreadSlot.Future -> "미래 카드를 선택했습니다."
            }
            shouldShowResult = updatedDrawn.size == state.pendingSlots.size
            state.copy(
                drawPile = updatedPile,
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
        _uiState.value = SpreadFlowUiState(
            step = SpreadStep.Preselection,
            positions = positions,
            preselection = SpreadPreselectionState(),
            availableCards = filteredAvailableCards(SpreadPreselectionState()),
            drawPile = allCards,
            finalCards = emptyMap(),
            selectedCategory = CardCategory.MajorArcana
        )
    }

    private fun filteredAvailableCards(preselection: SpreadPreselectionState): List<TarotCardModel> {
        val usedIds = preselection.usedIds()
        if (usedIds.isEmpty()) return allCards
        return allCards.filterNot { it.id in usedIds }
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
    val value = arcana.lowercase()
    return when {
        value.contains("wand") -> CardCategory.Wands
        value.contains("cup") -> CardCategory.Cups
        value.contains("sword") -> CardCategory.Swords
        value.contains("pentacle") || value.contains("coin") -> CardCategory.Pentacles
        else -> CardCategory.MajorArcana
    }
}
