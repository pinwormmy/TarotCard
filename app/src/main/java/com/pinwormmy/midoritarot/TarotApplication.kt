package com.pinwormmy.midoritarot

import android.app.Application
import com.pinwormmy.midoritarot.core.di.AppContainer
import com.pinwormmy.midoritarot.core.di.DefaultAppContainer

class TarotApplication : Application() {
    val appContainer: AppContainer by lazy { DefaultAppContainer(this) }
}

