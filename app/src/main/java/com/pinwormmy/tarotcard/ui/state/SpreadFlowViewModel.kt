package com.pinwormmy.tarotcard.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pinwormmy.tarotcard.data.TarotCardModel
import com.pinwormmy.tarotcard.data.TarotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SpreadPosition(
    val title: String,
    val description: String
)

enum class CardCategory(val displayName: String) {
    MajorArcana("Major Arcana"),
    Wands("Wands"),
    Cups("Cups"),
    Swords("Swords"),
    Pentacles("Pentacles")
}

data class SpreadFlowUiState(
    val step: SpreadStep = SpreadStep.SpreadSelection,
    val positions: List<SpreadPosition> = emptyList(),
    val currentPositionIndex: Int = 0,
    val selectedPositionCards: List<TarotCardModel?> = emptyList(),
    val selectedCategory: CardCategory = CardCategory.MajorArcana,
    val question: String = "",
    val useReversed: Boolean = true,
    val shuffleTrigger: Int = 0,
    val availableCards: List<TarotCardModel> = emptyList(),
    val drawPile: List<TarotCardModel> = emptyList(),
    val gridVisible: Boolean = false,
    val drawnCards: List<TarotCardModel> = emptyList(),
    val statusMessage: String? = null,
    val isPositionSelectionComplete: Boolean = false
)

class SpreadFlowViewModel(
    repository: TarotRepository
) : ViewModel() {

    private val positions = listOf(
        SpreadPosition("과거", "과거가 현재 상황에 어떤 영향을 미치나요?"),
        SpreadPosition("현재", "현재 상황은 어떤가요?"),
        SpreadPosition("미래", "이 상황의 잠재적 결과는 무엇인가요?")
    )

    private val allCards = repository.getCards()

    private val _uiState = MutableStateFlow(
        SpreadFlowUiState(
            positions = positions,
            selectedPositionCards = List(positions.size) { null },
            availableCards = allCards,
            drawPile = allCards.shuffled()
        )
    )
    val uiState: StateFlow<SpreadFlowUiState> = _uiState.asStateFlow()

    fun moveToStep(step: SpreadStep) {
        updateState { it.copy(step = step) }
    }

    fun moveToPositionSelect() {
        moveToStep(SpreadStep.PositionSelect)
        updateState { it.copy(currentPositionIndex = 0) }
    }

    fun moveToReadingSetup() {
        moveToStep(SpreadStep.ReadingSetup)
    }

    fun moveToShuffle() {
        moveToStep(SpreadStep.ShuffleAndDraw)
        updateState {
            it.copy(
                drawnCards = emptyList(),
                gridVisible = false,
                statusMessage = null,
                drawPile = it.availableCards.shuffled()
            )
        }
    }

    fun moveToReadingResult() {
        moveToStep(SpreadStep.ReadingResult)
    }

    fun selectCardForCurrentPosition(card: TarotCardModel) {
        updateState { state ->
            val updated = state.selectedPositionCards.toMutableList()
            if (updated.isEmpty()) return@updateState state
            updated[state.currentPositionIndex] = card
            state.copy(
                selectedPositionCards = updated
            )
        }
    }

    fun goToNextPosition() {
        updateState { state ->
            val next = (state.currentPositionIndex + 1).coerceAtMost(state.positions.lastIndex)
            state.copy(currentPositionIndex = next)
        }
    }

    fun goToPreviousPosition() {
        updateState { state ->
            val prev = (state.currentPositionIndex - 1).coerceAtLeast(0)
            state.copy(currentPositionIndex = prev)
        }
    }

    fun updateQuestion(text: String) {
        updateState { it.copy(question = text) }
    }

    fun toggleUseReversed() {
        updateState { it.copy(useReversed = !it.useReversed) }
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

    fun cutDeck() {
        updateState {
            it.copy(
                drawPile = it.drawPile.shuffled(),
                statusMessage = "덱을 다시 컷했습니다."
            )
        }
    }

    fun selectDrawCard(card: TarotCardModel) {
        updateState { state ->
            if (state.drawnCards.size >= 3) return@updateState state
            if (state.drawnCards.any { it.id == card.id }) return@updateState state
            val newDrawPile = state.drawPile.filterNot { it.id == card.id }
            val newDrawn = state.drawnCards + card
            state.copy(
                drawPile = newDrawPile,
                drawnCards = newDrawn,
                statusMessage = drawStatusMessage(newDrawn.size)
            )
        }
    }

    fun completeDrawIfReady(): Boolean {
        val ready = _uiState.value.drawnCards.size == 3
        if (ready) {
            moveToReadingResult()
        }
        return ready
    }

    fun immediateReadingFromSelections(): Boolean {
        val cards = _uiState.value.selectedPositionCards
        if (cards.any { it == null }) return false
        updateState {
            it.copy(
                drawnCards = cards.filterNotNull(),
                statusMessage = null
            )
        }
        moveToReadingResult()
        return true
    }

    fun resetFlow() {
        _uiState.value = SpreadFlowUiState(
            step = SpreadStep.SpreadSelection,
            positions = positions,
            currentPositionIndex = 0,
            selectedPositionCards = List(positions.size) { null },
            selectedCategory = CardCategory.MajorArcana,
            availableCards = allCards,
            drawPile = allCards.shuffled()
        )
    }

    fun updateCategory(category: CardCategory) {
        updateState { it.copy(selectedCategory = category) }
    }

    private fun drawStatusMessage(size: Int): String {
        return when (size) {
            1 -> "과거 카드를 선택했습니다."
            2 -> "현재 카드를 선택했습니다."
            3 -> "미래 카드를 선택했습니다."
            else -> ""
        }
    }

    private fun updateState(transform: (SpreadFlowUiState) -> SpreadFlowUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            updated.copy(
                isPositionSelectionComplete = updated.selectedPositionCards.all { it != null }
            )
        }
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
