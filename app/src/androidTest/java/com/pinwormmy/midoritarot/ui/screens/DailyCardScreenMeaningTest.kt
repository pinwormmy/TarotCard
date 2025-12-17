package com.pinwormmy.midoritarot.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pinwormmy.midoritarot.domain.model.TarotCardModel
import com.pinwormmy.midoritarot.ui.theme.TarotcardTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyCardScreenMeaningTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun meaningSheet_showsOnlyUprightMeaning() {
        val uprightMeaning = "UPRIGHT_MEANING"
        val reversedMeaning = "REVERSED_MEANING"

        composeRule.setContent {
            TarotcardTheme {
                DailyCardScreen(
                    card = TarotCardModel(
                        id = "test_card",
                        name = "Test Card",
                        arcana = "Major",
                        uprightMeaning = uprightMeaning,
                        reversedMeaning = reversedMeaning,
                        description = "",
                        keywords = emptyList(),
                    ),
                    onBack = {},
                    showFrontImmediately = true,
                )
            }
        }

        composeRule.onNodeWithTag("daily_card_card").performClick()

        composeRule.onNodeWithText(uprightMeaning).assertIsDisplayed()
        composeRule.onNodeWithText(reversedMeaning).assertDoesNotExist()
    }
}

