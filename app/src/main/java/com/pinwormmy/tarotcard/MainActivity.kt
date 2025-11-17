package com.pinwormmy.tarotcard

import android.os.Bundle
import android.util.Log
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
import com.pinwormmy.tarotcard.ui.theme.TarotBackground
import com.pinwormmy.tarotcard.ui.theme.TarotcardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = TarotRepository(applicationContext)
        Log.d("TEST", "Codex working!");
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
            CompositionLocalProvider(LocalHapticsEnabled provides settingsState.hapticsEnabled) {
                TarotcardTheme(skin = settingsState.skin) {
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
