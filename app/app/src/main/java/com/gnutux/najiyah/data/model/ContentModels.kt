package com.gnutux.najiyah.data.model

/** بيان المحتوى (manifest.json): رقم النسخة وقائمة الملفات المكوِّنة لها. */
data class ContentManifest(
    val version: Int,
    val files: List<String>,
)

/** مصدر مذكور في نهاية أي صفحة (فتوى، كتاب، موقع)، والرابط اختياري. */
data class SourceRef(
    val label: String,
    val url: String? = null,
)

/**
 * قسم عام قابل لإعادة الاستخدام في كل الصفحات (فرقة أو صفحة ثابتة): عنوان
 * فرعي اختياري، فقرات نصية، قائمة نقطية اختيارية، وجدول اختياري.
 */
data class ContentSection(
    val heading: String? = null,
    val paragraphs: List<String> = emptyList(),
    val items: List<String> = emptyList(),
    val table: ContentTable? = null,
)

data class ContentTable(
    val headers: List<String>,
    val rows: List<List<String>>,
)

/** فهرس خفيف لفرقة ضمن قائمة الموسوعة (sects/index.json). */
data class SectSummary(
    val id: String,
    val title: String,
    val icon: String,
    val category: String,
    val tagline: String,
    val hasDetail: Boolean,
)

/** تفصيل كامل لفرقة (sects/<id>.json). */
data class SectDetail(
    val id: String,
    val title: String,
    val icon: String,
    val classification: String,
    val sections: List<ContentSection>,
    val sources: List<SourceRef>,
)

/** صفحة ثابتة عامة (الفرقة الناجية، منهج النجاة، المصادر، من نحن). */
data class StaticPage(
    val title: String,
    val icon: String,
    val intro: String? = null,
    val sections: List<ContentSection>,
)

data class HomeFeature(
    val icon: String,
    val title: String,
    val body: String,
)

/** محتوى الصفحة الرئيسية (home.json). */
data class HomeContent(
    val bismillah: String,
    val hadithIntro: String,
    val hadithText: String,
    val hadithQuestion: String,
    val hadithAnswer: String,
    val takhreej: List<String>,
    val description: String,
    val features: List<HomeFeature>,
)
