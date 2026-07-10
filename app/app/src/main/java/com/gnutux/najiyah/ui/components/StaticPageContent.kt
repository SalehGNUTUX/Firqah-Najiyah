package com.gnutux.najiyah.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gnutux.najiyah.data.model.StaticPage
import com.gnutux.najiyah.ui.theme.tokens

/**
 * عارض عام لأي صفحة ثابتة (الفرقة الناجية، منهج النجاة، المصادر، من نحن)
 * تُستدعى من كل شاشة اختصاصها الخاص بعد تحميل [StaticPage] من [ContentRepository].
 */
@Composable
fun StaticPageContent(page: StaticPage, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        page.intro?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = tokens.colors.text)
        }
        page.sections.forEach { section ->
            SectionBlock(section)
        }
    }
}
