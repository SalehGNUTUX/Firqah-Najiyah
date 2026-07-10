package com.gnutux.najiyah.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gnutux.najiyah.data.ContentRepository
import com.gnutux.najiyah.data.model.SectSummary
import com.gnutux.najiyah.ui.theme.tokens

@Composable
fun SearchScreen(
    repository: ContentRepository,
    onOpenSect: (SectSummary) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<SectSummary>>(emptyList()) }

    LaunchedEffect(query) {
        results = repository.search(query)
    }

    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("ابحث في الموسوعة…") },
            singleLine = true,
        )

        if (query.isNotBlank() && results.isEmpty()) {
            Text("لا نتائج مطابقة", color = tokens.colors.textDim)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(results) { sect ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = tokens.colors.surface),
                    shape = RoundedCornerShape(tokens.shape.md),
                    onClick = { onOpenSect(sect) },
                ) {
                    Column(Modifier.padding(tokens.shape.cardPad)) {
                        Text("${sect.icon} ${sect.title}", fontWeight = FontWeight.Bold, color = tokens.colors.accent)
                        Text(sect.tagline, color = tokens.colors.textDim, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
