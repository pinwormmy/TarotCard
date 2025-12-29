package com.pinwormmy.midoritarot.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pinwormmy.midoritarot.R
import com.pinwormmy.midoritarot.ui.theme.TarotcardTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun mainMenu_showsAllEntries() {
        composeRule.setContent {
            TarotcardTheme {
                MainMenuScreen(
                    onStartReading = {},
                    onDailyCard = {},
                    onOpenHistory = {},
                    onBrowseCards = {},
                    onOpenOptions = {},
                )
            }
        }

        val context = composeRule.activity
        val labels = listOf(
            context.getString(R.string.menu_daily_card),
            context.getString(R.string.menu_start_reading),
            context.getString(R.string.menu_history),
            context.getString(R.string.menu_browse_cards),
            context.getString(R.string.menu_options),
        )

        labels.forEach { label ->
            composeRule.onNodeWithText(label).assertIsDisplayed()
        }
    }
}
