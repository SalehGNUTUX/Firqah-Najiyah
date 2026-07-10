package com.gnutux.najiyah.feature.encyclopedia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gnutux.najiyah.data.model.SectSummary
import com.gnutux.najiyah.ui.components.LoadingIndicator
import com.gnutux.najiyah.ui.theme.tokens

@Composable
fun EncyclopediaListScreen(
    sects: List<SectSummary>?,
    onOpenSect: (SectSummary) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (sects == null) {
        LoadingIndicator(modifier)
        return
    }

    val grouped = sects.groupBy { it.category }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        grouped.forEach { (category, items) ->
            item {
                Text(
                    category,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = tokens.colors.accentDark,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                )
            }
            items(items) { sect -> SectCard(sect, onClick = { onOpenSect(sect) }) }
        }
    }
}

@Composable
private fun SectCard(sect: SectSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = tokens.colors.surface),
        shape = RoundedCornerShape(tokens.shape.md),
        onClick = onClick,
    ) {
        Column(Modifier.padding(tokens.shape.cardPad)) {
            Text("${sect.icon} ${sect.title}", fontWeight = FontWeight.Bold, color = tokens.colors.accent)
            Text(sect.tagline, color = tokens.colors.textDim, style = MaterialTheme.typography.bodyMedium)
            if (!sect.hasDetail) {
                Text("قريباً…", color = tokens.colors.gold, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
