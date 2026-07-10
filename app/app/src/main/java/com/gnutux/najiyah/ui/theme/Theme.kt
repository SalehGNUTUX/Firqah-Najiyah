package com.gnutux.najiyah.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * سمة تطبيق الفرقة الناجية — تعتمد رموز التصميم في [Tokens.kt] المستخرجة من
 * ألوان الموقع (style.css)، وتُسقطها على Material 3 ColorScheme للمكوّنات
 * القياسية. اتجاه RTL تلقائي (المحتوى عربي بالكامل).
 */
@Composable
fun NajiyahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val c = if (darkTheme) NajiyahDark else NajiyahLight
    val resolvedTokens = NajiyahTokens(colors = c, dark = darkTheme)

    val scheme = if (darkTheme) {
        darkColorScheme(
            primary = c.accent,
            onPrimary = c.accentText,
            primaryContainer = c.accentSoft,
            onPrimaryContainer = c.accent,
            secondary = c.gold,
            onSecondary = c.accentText,
            secondaryContainer = c.goldSoft,
            background = c.bg,
            onBackground = c.text,
            surface = c.surface,
            onSurface = c.text,
            surfaceVariant = c.surfaceAlt,
            onSurfaceVariant = c.textDim,
            outline = c.line,
        )
    } else {
        lightColorScheme(
            primary = c.accent,
            onPrimary = c.accentText,
            primaryContainer = c.accentSoft,
            onPrimaryContainer = c.accentDark,
            secondary = c.gold,
            onSecondary = c.accentText,
            secondaryContainer = c.goldSoft,
            background = c.bg,
            onBackground = c.text,
            surface = c.surface,
            onSurface = c.text,
            surfaceVariant = c.surfaceAlt,
            onSurfaceVariant = c.textDim,
            outline = c.line,
        )
    }

    CompositionLocalProvider(LocalTokens provides resolvedTokens) {
        MaterialTheme(
            colorScheme = scheme,
            typography = Typography(),
            content = content,
        )
    }
}
