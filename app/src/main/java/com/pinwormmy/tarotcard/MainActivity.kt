package com.pinwormmy.tarotcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pinwormmy.tarotcard.data.TarotRepository
import com.pinwormmy.tarotcard.navigation.TarotNavGraph
import com.pinwormmy.tarotcard.notifications.DailyCardNotificationScheduler
import com.pinwormmy.tarotcard.ui.state.TarotSettingsViewModel
import com.pinwormmy.tarotcard.ui.theme.LocalHapticsEnabled
import com.pinwormmy.tarotcard.ui.theme.LocalCardFaceSkin
import com.pinwormmy.tarotcard.ui.theme.TarotBackground
import com.pinwormmy.tarotcard.ui.theme.TarotcardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = TarotRepository(applicationContext)
        setContent {
            val settingsViewModel: TarotSettingsViewModel = viewModel()
            val settingsState by settingsViewModel.uiState.collectAsState()
            val appContext = applicationContext
            LaunchedEffect(settingsState.dailyCardNotification, settingsState.dailyCardTime) {
                if (settingsState.dailyCardNotification) {
                    DailyCardNotificationScheduler.schedule(appContext, settingsState.dailyCardTime)
                } else {
                    DailyCardNotificationScheduler.cancel(appContext)
                }
            }
            CompositionLocalProvider(
                LocalHapticsEnabled provides settingsState.hapticsEnabled,
                LocalCardFaceSkin provides settingsState.cardFaceSkin
            ) {
                TarotcardTheme(
                    skin = settingsState.skin,
                    cardFaceSkin = settingsState.cardFaceSkin
                ) {
                    TarotBackground {
                        TarotNavGraph(
                            repository = repository,
                            settingsViewModel = settingsViewModel
                        )
                    }
                }
            }
        }
    }
}
