package com.gnutux.najiyah.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gnutux.najiyah.data.model.HomeContent
import com.gnutux.najiyah.ui.components.LoadingIndicator
import com.gnutux.najiyah.ui.theme.tokens

@Composable
fun HomeScreen(
    content: HomeContent?,
    onOpenNajiyah: () -> Unit,
    onOpenEncyclopedia: () -> Unit,
    onOpenManhaj: () -> Unit,
    onOpenSources: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (content == null) {
        LoadingIndicator(modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(content.bismillah, fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = tokens.colors.gold)

        // بطاقة الحديث الشريف
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = tokens.colors.surface),
            shape = RoundedCornerShape(tokens.shape.md),
        ) {
            Column(Modifier.padding(tokens.shape.cardPad), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(content.hadithIntro, color = tokens.colors.text)
                Card(colors = CardDefaults.cardColors(containerColor = tokens.colors.surfaceAlt)) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(content.hadithText, fontWeight = FontWeight.Medium, color = tokens.colors.text)
                        Text(content.hadithQuestion, fontWeight = FontWeight.Bold, color = tokens.colors.accentDark)
                        Text(content.hadithAnswer, fontWeight = FontWeight.Bold, color = tokens.colors.accent)
                    }
                }
                content.takhreej.forEach { line ->
                    Text(line, style = MaterialTheme.typography.bodySmall, color = tokens.colors.textDim)
                }
            }
        }

        Text(
            content.description,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = tokens.colors.text,
        )

        // أزرار التنقّل الرئيسية (تطابق الصفحة الرئيسية للموقع)
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onOpenNajiyah,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = tokens.colors.accent),
            ) { Text("🕌 من هم أهل السنة والجماعة؟") }

            OutlinedButton(onClick = onOpenEncyclopedia, modifier = Modifier.fillMaxWidth()) {
                Text("⚠️ تصفح الفرق الضالة")
            }
            OutlinedButton(onClick = onOpenManhaj, modifier = Modifier.fillMaxWidth()) {
                Text("📖 كيف تنجو بنفسك؟")
            }
        }

        // بطاقات المميزات الثلاث
        content.features.forEach { feature ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = tokens.colors.surface),
                shape = RoundedCornerShape(tokens.shape.md),
            ) {
                Row(Modifier.padding(tokens.shape.cardPad)) {
                    Text(feature.icon, style = MaterialTheme.typography.headlineSmall)
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(feature.title, fontWeight = FontWeight.Bold, color = tokens.colors.accent)
                        Text(feature.body, color = tokens.colors.textDim, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        OutlinedButton(onClick = onOpenSources, modifier = Modifier.fillMaxWidth()) {
            Text("📚 عرض المصادر")
        }
    }
}
