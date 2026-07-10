package com.gnutux.najiyah.data

import android.content.Context
import android.util.Log
import com.gnutux.najiyah.data.model.ContentManifest
import com.gnutux.najiyah.data.model.ContentSection
import com.gnutux.najiyah.data.model.ContentTable
import com.gnutux.najiyah.data.model.HomeContent
import com.gnutux.najiyah.data.model.HomeFeature
import com.gnutux.najiyah.data.model.SectDetail
import com.gnutux.najiyah.data.model.SectSummary
import com.gnutux.najiyah.data.model.SourceRef
import com.gnutux.najiyah.data.model.StaticPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

private const val REMOTE_MANIFEST_URL = "https://salehgnutux.github.io/Firqah-Najiyah/app/content/manifest.json"
private const val REMOTE_BASE_URL = "https://salehgnutux.github.io/Firqah-Najiyah/app/content/"
private const val PREFS_NAME = "najiyah_content"
private const val PREF_CONTENT_VERSION = "content_version"
private const val TAG = "ContentRepository"

/**
 * يدير الوصول إلى محتوى الموسوعة: نسخة مضمَّنة في assets/content/ تعمل بلا
 * إنترنت مطلقاً، مع فحص/تنزيل تحديث اختياري من نفس مسار الموقع على GitHub
 * Pages عند توفر الاتصال (فشل صامت إن تعذّر، لا يؤثر على عمل التطبيق).
 */
class ContentRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val updatedContentDir = File(context.filesDir, "content")

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    /** يقرأ نص ملف: من نسخة التحديث المخزَّنة إن وُجدت، وإلا من assets المضمَّنة. */
    private fun readText(relativePath: String): String {
        val updatedFile = File(updatedContentDir, relativePath)
        return if (updatedFile.exists()) {
            updatedFile.readText(Charsets.UTF_8)
        } else {
            context.assets.open("content/$relativePath").bufferedReader(Charsets.UTF_8).use { it.readText() }
        }
    }

    suspend fun loadHome(): HomeContent = withContext(Dispatchers.IO) {
        parseHome(JSONObject(readText("home.json")))
    }

    suspend fun loadStaticPage(fileName: String): StaticPage = withContext(Dispatchers.IO) {
        parseStaticPage(JSONObject(readText(fileName)))
    }

    suspend fun loadSectIndex(): List<SectSummary> = withContext(Dispatchers.IO) {
        val arr = JSONArray(readText("sects/index.json"))
        (0 until arr.length()).map { parseSectSummary(arr.getJSONObject(it)) }
    }

    suspend fun loadSectDetail(id: String): SectDetail? = withContext(Dispatchers.IO) {
        try {
            parseSectDetail(JSONObject(readText("sects/$id.json")))
        } catch (e: Exception) {
            Log.w(TAG, "تعذّر تحميل تفصيل الفرقة: $id", e)
            null
        }
    }

    /** بحث نصي بسيط عبر عناوين الفرق ونبذاتها (v1: بلا فهرسة، كافٍ لحجم المحتوى الحالي). */
    suspend fun search(query: String): List<SectSummary> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val q = query.trim()
        loadSectIndex().filter {
            it.title.contains(q, ignoreCase = true) ||
                it.tagline.contains(q, ignoreCase = true) ||
                it.category.contains(q, ignoreCase = true)
        }
    }

    /**
     * يفحص نسخة المحتوى المنشورة على الموقع، وإن كانت أحدث من المخزَّنة محلياً
     * ينزّل كل ملفاتها إلى [updatedContentDir]. يفشل بصمت (يعيد false) عند
     * غياب الإنترنت أو أي خطأ، فلا يتأثر عمل التطبيق أوفلاين.
     */
    suspend fun checkAndApplyUpdate(): Boolean = withContext(Dispatchers.IO) {
        try {
            val manifest = fetchRemoteManifest() ?: return@withContext false
            val localVersion = prefs.getInt(PREF_CONTENT_VERSION, bundledVersion())
            if (manifest.version <= localVersion) return@withContext false

            updatedContentDir.mkdirs()
            for (relativePath in manifest.files) {
                val body = httpGet(REMOTE_BASE_URL + relativePath) ?: return@withContext false
                val target = File(updatedContentDir, relativePath)
                target.parentFile?.mkdirs()
                target.writeText(body, Charsets.UTF_8)
            }
            prefs.edit().putInt(PREF_CONTENT_VERSION, manifest.version).apply()
            true
        } catch (e: Exception) {
            Log.w(TAG, "تعذّر تحديث المحتوى عن بُعد", e)
            false
        }
    }

    private fun bundledVersion(): Int = try {
        JSONObject(
            context.assets.open("content/manifest.json").bufferedReader(Charsets.UTF_8).use { it.readText() },
        ).getInt("version")
    } catch (e: Exception) {
        1
    }

    private fun fetchRemoteManifest(): ContentManifest? {
        val body = httpGet(REMOTE_MANIFEST_URL) ?: return null
        val json = JSONObject(body)
        val filesArr = json.getJSONArray("files")
        val files = (0 until filesArr.length()).map { filesArr.getString(it) }
        return ContentManifest(version = json.getInt("version"), files = files)
    }

    private fun httpGet(url: String): String? {
        val request = Request.Builder().url(url).build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            return response.body?.string()
        }
    }

    // ===== تحويل JSON إلى نماذج Kotlin =====

    private fun parseHome(json: JSONObject): HomeContent = HomeContent(
        bismillah = json.getString("bismillah"),
        hadithIntro = json.getString("hadithIntro"),
        hadithText = json.getString("hadithText"),
        hadithQuestion = json.getString("hadithQuestion"),
        hadithAnswer = json.getString("hadithAnswer"),
        takhreej = json.getJSONArray("takhreej").toStringList(),
        description = json.getString("description"),
        features = json.getJSONArray("features").let { arr ->
            (0 until arr.length()).map { i ->
                val f = arr.getJSONObject(i)
                HomeFeature(f.getString("icon"), f.getString("title"), f.getString("body"))
            }
        },
    )

    private fun parseStaticPage(json: JSONObject): StaticPage = StaticPage(
        title = json.getString("title"),
        icon = json.optString("icon", ""),
        intro = json.optString("intro").ifBlank { null },
        sections = json.getJSONArray("sections").toSectionList(),
    )

    private fun parseSectSummary(json: JSONObject): SectSummary = SectSummary(
        id = json.getString("id"),
        title = json.getString("title"),
        icon = json.getString("icon"),
        category = json.getString("category"),
        tagline = json.getString("tagline"),
        hasDetail = json.optBoolean("hasDetail", false),
    )

    private fun parseSectDetail(json: JSONObject): SectDetail = SectDetail(
        id = json.getString("id"),
        title = json.getString("title"),
        icon = json.getString("icon"),
        classification = json.getString("classification"),
        sections = json.getJSONArray("sections").toSectionList(),
        sources = json.getJSONArray("sources").toSourceRefList(),
    )

    private fun JSONArray.toSectionList(): List<ContentSection> =
        (0 until length()).map { i ->
            val s = getJSONObject(i)
            ContentSection(
                heading = s.optString("heading").ifBlank { null },
                paragraphs = s.optJSONArray("paragraphs")?.toStringList() ?: emptyList(),
                items = s.optJSONArray("items")?.toStringList() ?: emptyList(),
                table = s.optJSONObject("table")?.let { t ->
                    ContentTable(
                        headers = t.getJSONArray("headers").toStringList(),
                        rows = t.getJSONArray("rows").let { rowsArr ->
                            (0 until rowsArr.length()).map { r -> rowsArr.getJSONArray(r).toStringList() }
                        },
                    )
                },
                links = s.optJSONArray("links")?.toSourceRefList() ?: emptyList(),
            )
        }

    private fun JSONArray.toSourceRefList(): List<SourceRef> =
        (0 until length()).map { i ->
            val s = getJSONObject(i)
            SourceRef(s.getString("label"), s.optString("url").ifBlank { null })
        }

    private fun JSONArray.toStringList(): List<String> = (0 until length()).map { getString(it) }
}
