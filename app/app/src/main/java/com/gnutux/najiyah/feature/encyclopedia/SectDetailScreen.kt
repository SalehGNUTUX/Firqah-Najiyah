package com.gnutux.najiyah.feature.encyclopedia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gnutux.najiyah.data.model.SectDetail
import com.gnutux.najiyah.ui.components.LoadingIndicator
import com.gnutux.najiyah.ui.components.SectionBlock
import com.gnutux.najiyah.ui.components.SourcesBlock
import com.gnutux.najiyah.ui.theme.tokens

@Composable
fun SectDetailScreen(detail: SectDetail?, notPortedYet: Boolean, modifier: Modifier = Modifier) {
    if (notPortedYet) {
        Column(
            modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "لم يُضَف محتوى هذه الفرقة إلى التطبيق بعد. يمكنك مطالعتها كاملة على الموقع.",
                color = tokens.colors.textDim,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        return
    }

    if (detail == null) {
        LoadingIndicator(modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = tokens.colors.surfaceAlt),
            shape = RoundedCornerShape(tokens.shape.md),
        ) {
            Column(Modifier.padding(tokens.shape.cardPad)) {
                Text("📌 التصنيف", fontWeight = FontWeight.Bold, color = tokens.colors.accent)
                Text(detail.classification, color = tokens.colors.text)
            }
        }

        detail.sections.forEach { section -> SectionBlock(section) }

        SourcesBlock(detail.sources)
    }
}
