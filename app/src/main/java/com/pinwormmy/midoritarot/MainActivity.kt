package com.pinwormmy.midoritarot

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.pm.ActivityInfo
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.os.LocaleListCompat
import com.pinwormmy.midoritarot.data.SettingsRepository
import com.pinwormmy.midoritarot.navigation.TarotNavGraph
import com.pinwormmy.midoritarot.notifications.DailyCardNotificationScheduler
import com.pinwormmy.midoritarot.ui.state.AppLanguage
import com.pinwormmy.midoritarot.ui.state.TarotSettingsViewModel
import com.pinwormmy.midoritarot.ui.theme.LocalCardBackStyle
import com.pinwormmy.midoritarot.ui.theme.LocalCardFaceSkin
import com.pinwormmy.midoritarot.ui.theme.LocalHapticsEnabled
import com.pinwormmy.midoritarot.ui.theme.TarotBackground
import com.pinwormmy.midoritarot.ui.theme.TarotcardTheme

@SuppressLint("SourceLockedOrientationActivity")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val container = (application as TarotApplication).appContainer
        val settingsRepository = container.settingsRepository
        applySavedLocale(settingsRepository)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = container.tarotRepository
        val dailyCardRepository = container.dailyCardRepository
        val drawHistoryRepository = container.drawHistoryRepository
        setContent {
            val lastAppliedLocaleTag = rememberSaveable {
                mutableStateOf(AppCompatDelegate.getApplicationLocales().toLanguageTags())
            }
            val settingsViewModel: TarotSettingsViewModel =
                viewModel(factory = TarotSettingsViewModel.factory(settingsRepository))
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            val appContext = applicationContext
            LaunchedEffect(settingsState.dailyCardNotification, settingsState.dailyCardTime) {
                if (settingsState.dailyCardNotification) {
                    DailyCardNotificationScheduler.schedule(appContext, settingsState.dailyCardTime)
                } else {
                    DailyCardNotificationScheduler.cancel(appContext)
                }
            }
            LaunchedEffect(settingsState.language) {
                val localeList = settingsState.language.toLocaleListCompat()
                val targetTag = localeList.toLanguageTags()
                if (lastAppliedLocaleTag.value != targetTag) {
                    AppCompatDelegate.setApplicationLocales(localeList)
                    lastAppliedLocaleTag.value = targetTag
                    this@MainActivity.recreate()
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
                            dailyCardRepository = dailyCardRepository,
                            drawHistoryRepository = drawHistoryRepository,
                            settingsViewModel = settingsViewModel
                        )
                    }
                }
            }
        }
    }
}

private fun AppLanguage.toLocaleListCompat(): LocaleListCompat =
    toLocaleOrNull()?.let { LocaleListCompat.create(it) } ?: LocaleListCompat.getEmptyLocaleList()

private fun applySavedLocale(settingsRepository: SettingsRepository) {
    val localeList = settingsRepository.load().language.toLocaleListCompat()
    AppCompatDelegate.setApplicationLocales(localeList)
}
