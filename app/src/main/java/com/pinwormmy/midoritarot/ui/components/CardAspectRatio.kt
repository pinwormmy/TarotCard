package com.pinwormmy.midoritarot.ui.components

const val CARD_ASPECT_RATIO = 2f / 3f
const val CARD_LANDSCAPE_RATIO = 1f / CARD_ASPECT_RATIO

// Physical size cap: 목표 80mm x 120mm 이하. 약간의 여유를 두어 실물보다 작게(≤79.4mm) 보이도록 500dp로 제한.
const val CARD_MAX_WIDTH_DP = 500f
const val CARD_MAX_HEIGHT_DP = CARD_MAX_WIDTH_DP / CARD_ASPECT_RATIO

// Daily card 화면용 별도 상한: 시각적 피로를 줄이기 위해 더 보수적으로 제한.
const val DAILY_CARD_MAX_WIDTH_DP = 420f
const val DAILY_CARD_MAX_HEIGHT_DP = DAILY_CARD_MAX_WIDTH_DP / CARD_ASPECT_RATIO
