package com.gnutux.najiyah.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.gnutux.najiyah.data.model.ContentSection
import com.gnutux.najiyah.data.model.SourceRef
import com.gnutux.najiyah.ui.theme.tokens

/**
 * صفّ مصدر واحد: نصّ عادٍ إن لم يوجد رابط، أو نصّ قابل للنقر يفتح المتصفح
 * (Intent.ACTION_VIEW) إذا توفّر رابط — يعمل فقط عند توفّر إنترنت فعلياً،
 * والتطبيق يبقى يعمل أوفلاين بلا أي اعتماد على هذا الرابط.
 */
@Composable
fun SourceLinkRow(source: SourceRef, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val url = source.url
    if (url == null) {
        Text("• ${source.label}", color = tokens.colors.textDim, modifier = modifier)
        return
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "• ${source.label}",
                color = tokens.colors.accent,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.weight(1f, fill = false),
            )
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = tokens.colors.accent,
                modifier = Modifier.padding(start = 4.dp).size(16.dp),
            )
        }
    }
}

/** شريط علوي موحَّد لكل الشاشات، مع زر رجوع اختياري وزر مشاركة اختياري. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NajiyahTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = tokens.colors.surface,
            titleContentColor = tokens.colors.text,
        ),
    )
}

/** مؤشر تحميل بسيط يتوسّط الشاشة. */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = tokens.colors.accent)
    }
}

/** يعرض قسم محتوى عام: عنوان فرعي، فقرات، قائمة نقطية، وجدول — أياً منها متوفراً. */
@Composable
fun SectionBlock(section: ContentSection, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        section.heading?.let {
            Text(it, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = tokens.colors.accentDark)
        }
        section.paragraphs.forEach { p ->
            Text(p, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge, color = tokens.colors.text)
        }
        section.items.forEach { item ->
            Text("• $item", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge, color = tokens.colors.text)
        }
        section.links.forEach { link ->
            SourceLinkRow(link)
        }
        section.table?.let { table ->
            Card(colors = CardDefaults.cardColors(containerColor = tokens.colors.surfaceAlt)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(table.headers.joinToString(" — "), fontWeight = FontWeight.Bold, color = tokens.colors.accent)
                    table.rows.forEach { row ->
                        Text(row.joinToString(" — "), color = tokens.colors.text)
                    }
                }
            }
        }
    }
}

/** قائمة المصادر في نهاية أي صفحة فرقة. */
@Composable
fun SourcesBlock(sources: List<SourceRef>, modifier: Modifier = Modifier) {
    if (sources.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("📚 المصادر", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = tokens.colors.gold)
        sources.forEach { s -> SourceLinkRow(s) }
    }
}

/** حاوية قائمة بمسافات موحّدة، تُستخدم في أغلب شاشات القوائم. */
@Composable
fun <T> NajiyahListColumn(
    items: List<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    itemContent: @Composable (T) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items) { itemContent(it) }
    }
}
