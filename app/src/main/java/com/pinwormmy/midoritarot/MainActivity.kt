package com.pinwormmy.midoritarot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pinwormmy.midoritarot.data.SettingsRepository
import com.pinwormmy.midoritarot.data.TarotRepository
import com.pinwormmy.midoritarot.navigation.TarotNavGraph
import com.pinwormmy.midoritarot.notifications.DailyCardNotificationScheduler
import com.pinwormmy.midoritarot.ui.state.TarotSettingsViewModel
import com.pinwormmy.midoritarot.ui.theme.LocalHapticsEnabled
import com.pinwormmy.midoritarot.ui.theme.LocalCardFaceSkin
import com.pinwormmy.midoritarot.ui.theme.LocalCardBackStyle
import com.pinwormmy.midoritarot.ui.theme.TarotBackground
import com.pinwormmy.midoritarot.ui.theme.TarotcardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = TarotRepository(applicationContext)
        val settingsRepository = SettingsRepository(applicationContext)
        setContent {
            val settingsViewModel: TarotSettingsViewModel =
                viewModel(factory = TarotSettingsViewModel.factory(settingsRepository))
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
                LocalCardFaceSkin provides settingsState.cardFaceSkin,
                LocalCardBackStyle provides settingsState.cardBackStyle
            ) {
                TarotcardTheme(
                    skin = settingsState.skin,
                    cardFaceSkin = settingsState.cardFaceSkin,
                    cardBackStyle = settingsState.cardBackStyle
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
