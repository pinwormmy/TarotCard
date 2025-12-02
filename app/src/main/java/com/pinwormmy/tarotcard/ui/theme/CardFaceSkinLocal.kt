package com.pinwormmy.tarotcard.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.pinwormmy.tarotcard.ui.state.CardFaceSkin
import com.pinwormmy.tarotcard.ui.state.CardBackStyle

val LocalCardFaceSkin = staticCompositionLocalOf { CardFaceSkin.Animation }
val LocalCardBackStyle = staticCompositionLocalOf { CardBackStyle.Byzantine }
