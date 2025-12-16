package com.pinwormmy.midoritarot.core.di

import android.content.Context
import com.pinwormmy.midoritarot.data.DailyCardRepository
import com.pinwormmy.midoritarot.data.DrawHistoryRepository
import com.pinwormmy.midoritarot.data.SettingsRepository
import com.pinwormmy.midoritarot.data.TarotRepository

interface AppContainer {
    val settingsRepository: SettingsRepository
    val tarotRepository: TarotRepository
    val dailyCardRepository: DailyCardRepository
    val drawHistoryRepository: DrawHistoryRepository
}

class DefaultAppContainer(
    context: Context
) : AppContainer {
    private val appContext = context.applicationContext

    override val settingsRepository: SettingsRepository by lazy { SettingsRepository(appContext) }
    override val tarotRepository: TarotRepository by lazy { TarotRepository(appContext) }
    override val dailyCardRepository: DailyCardRepository by lazy {
        DailyCardRepository(appContext, tarotRepository)
    }
    override val drawHistoryRepository: DrawHistoryRepository by lazy { DrawHistoryRepository(appContext) }
}
