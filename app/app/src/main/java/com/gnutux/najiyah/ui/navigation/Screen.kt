package com.gnutux.najiyah.ui.navigation

/** الوجهات المتاحة في التنقّل اليدوي المبسّط (بلا مكتبة Navigation Compose). */
sealed interface Screen {
    data object Home : Screen
    data object Najiyah : Screen
    data object Encyclopedia : Screen
    data class SectDetail(val id: String, val title: String) : Screen
    data object Manhaj : Screen
    data object Sources : Screen
    data object About : Screen
    data object Search : Screen
}
