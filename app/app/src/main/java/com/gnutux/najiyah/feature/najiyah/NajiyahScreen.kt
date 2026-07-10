package com.gnutux.najiyah.feature.najiyah

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gnutux.najiyah.data.model.StaticPage
import com.gnutux.najiyah.ui.components.LoadingIndicator
import com.gnutux.najiyah.ui.components.StaticPageContent

@Composable
fun NajiyahScreen(page: StaticPage?, modifier: Modifier = Modifier) {
    if (page == null) {
        LoadingIndicator(modifier)
        return
    }
    StaticPageContent(page, modifier)
}
