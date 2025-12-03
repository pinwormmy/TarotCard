package com.pinwormmy.midoritarot.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.pinwormmy.midoritarot.ui.state.CardFaceSkin
import com.pinwormmy.midoritarot.ui.state.CardBackStyle

val LocalCardFaceSkin = staticCompositionLocalOf { CardFaceSkin.Animation }
val LocalCardBackStyle = staticCompositionLocalOf { CardBackStyle.Byzantine }
