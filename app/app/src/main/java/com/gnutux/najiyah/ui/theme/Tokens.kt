package com.gnutux.najiyah.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * نظام رموز التصميم (Design Tokens) مقتبس من ألوان موقع الفرقة الناجية
 * (style.css: --bg, --text, --primary, --gold...). Material 3 ColorScheme
 * لا يغطّي كل الرموز (gold/line الدقيقة) فنحملها عبر [LocalTokens].
 */
data class NajiyahColors(
    val bg: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val text: Color,
    val textDim: Color,
    val line: Color,
    val accent: Color,
    val accentDark: Color,
    val accentSoft: Color,
    val accentText: Color,
    val gold: Color,
    val goldDark: Color,
    val goldSoft: Color,
)

data class NajiyahShape(
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 20.dp,
    val space: Dp = 16.dp,
    val cardPad: Dp = 18.dp,
)

data class NajiyahTokens(
    val colors: NajiyahColors,
    val shape: NajiyahShape = NajiyahShape(),
    val dark: Boolean,
)

// النمط النهاري (مطابق لـ :root في style.css)
val NajiyahLight = NajiyahColors(
    bg = Color(0xFFFEFDF8),
    surface = Color(0xFFFFFFFF),
    surfaceAlt = Color(0xFFE6F4EC),
    text = Color(0xFF1E1E1E),
    textDim = Color(0xFF5A5A5A),
    line = Color(0xFFE0D5C1),
    accent = Color(0xFF0E6B3E),
    accentDark = Color(0xFF094D2C),
    accentSoft = Color(0xFFE6F4EC),
    accentText = Color(0xFFFFFFFF),
    gold = Color(0xFFB68B40),
    goldDark = Color(0xFF8A652E),
    goldSoft = Color(0xFFFDF3E0),
)

// النمط الليلي (مطابق لـ [data-theme="dark"] في style.css)
val NajiyahDark = NajiyahColors(
    bg = Color(0xFF1A1A1A),
    surface = Color(0xFF252525),
    surfaceAlt = Color(0xFF1E3A2A),
    text = Color(0xFFE0D5C1),
    textDim = Color(0xFFAFAFAF),
    line = Color(0xFF444444),
    accent = Color(0xFF2E9A5C),
    accentDark = Color(0xFF1F6E3F),
    accentSoft = Color(0xFF1E3A2A),
    accentText = Color(0xFFFFFFFF),
    gold = Color(0xFFD4A84B),
    goldDark = Color(0xFFA07828),
    goldSoft = Color(0xFF3A2E1A),
)

val LocalTokens = staticCompositionLocalOf { NajiyahTokens(NajiyahLight, dark = false) }

/** وصول مختصر لرموز التصميم داخل أي Composable. */
val tokens: NajiyahTokens
    @Composable @ReadOnlyComposable
    get() = LocalTokens.current
