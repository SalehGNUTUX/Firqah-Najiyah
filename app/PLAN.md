# خطة: تطبيق أندرويد "الفرقة الناجية" (Kotlin) داخل مجلد app/

## Context

موقع "الفرقة الناجية" (HTML/CSS/JS ثابت، منشور على GitHub Pages) اكتمل ونُشر بنجاح
هذه الجلسة. طلب المستخدم الآن تخصيص مجلد `app/` داخل نفس مستودع المشروع
(`Firqah-Najiyah/`، وليس مستودعاً منفصلاً) لإنتاج تطبيق أندرويد أصلي بلغة Kotlin
يقدّم نفس محتوى الموقع (تعريف أهل السنة، موسوعة الفرق الـ19+، منهج النجاة،
المصادر) بتجربة تطبيق أصلي بدل متصفح.

استُكشف مشروعه السابق **GT-TAHAKOM** (Kotlin+Compose، نفس الحساب) لاستخلاص
قواعده المعمارية الثابتة ليتّبعها هذا المشروع لضمان الاتساق. بحسب اختيار
المستخدم: **بلا Hilt** (أبسط)، و**محتوى مضمَّن أوفلاين + تحديث اختياري عن بُعد**
(بدل الاعتماد الكامل على الإنترنت أو الاكتفاء بنسخة مجمَّدة).

## القرارات المعمارية (مبنية على نمط GT-TAHAKOM)

| المحور | القرار |
|---|---|
| نظام البناء | Gradle **Kotlin DSL** + **Version Catalog** (`gradle/libs.versions.toml`) — طابق GT-TAHAKOM |
| الإصدارات | Kotlin 2.1.0، AGP 8.9.2، Gradle 8.11.1، JDK 17 target (بيئة التطوير الحالية: JDK 21 + Gradle مثبَّتان ويعملان) |
| اسم الحزمة | `com.gnutux.najiyah` (نفس نمط `com.gnutux.<اسم-المشروع>`) |
| SDK | compileSdk = targetSdk = 36، minSdk = 26 (طابق GT-TAHAKOM) |
| واجهة المستخدم | **Jetpack Compose بالكامل**، Material3، بدون XML views (إلا themes.xml للإقلاع) |
| التنقل | `sealed interface Screen` + توجيه يدوي في MainActivity (بلا مكتبة Navigation Compose) — نفس نمط GT-TAHAKOM |
| حقن التبعيات | **بلا Hilt** — إنشاء الكائنات يدوياً (ContentRepository كـ singleton بسيط عبر Application/remember) |
| الثيم | نظام Design Tokens مخصّص (`NajiyahColors` بحقول bg/surface/text/accent...) بنمطي فاتح/داكن، مبني من ألوان الموقع: أخضر `#0e6b3e` (accent)، ذهبي `#b68b40` (accent ثانوي)، طابق بنية `Tokens.kt` في GT-TAHAKOM |
| تخزين المحتوى | JSON في `assets/content/` (نسخة أوفلاين مُضمَّنة) + فحص/تنزيل تحديث اختياري من نفس رابط GitHub Pages، مخزَّن في `filesDir` عند توفره — تفصيل أدناه |

## استراتيجية المحتوى (الأهم)

مصدر واحد للحقيقة: **`app/content/*.json`** — يُنشأ ويُحرَّر يدوياً (بمساعدتي) من
محتوى صفحات HTML الحالية (`pages/firaq/*.html` وغيرها)، حسب مخطط JSON بسيط
لكل نوع محتوى (فرقة، صفحة ثابتة، الرئيسية). بما أن هذا المجلد يقع داخل نفس
مستودع GitHub الذي يُنشر تلقائياً بالكامل عبر GitHub Pages، فإن هذه الملفات
تصبح متاحة فوراً وبلا أي إعداد إضافي على:
`https://salehgnutux.github.io/Firqah-Najiyah/app/content/manifest.json`

- **نسخة مضمَّنة (offline):** نفس ملفات `app/content/*.json` تُنسَخ إلى
  `app/app/src/main/assets/content/` وقت التطوير (تُحدَّث يدوياً كلما تغيّر
  المحتوى) — يعمل التطبيق بها فوراً بلا إنترنت مطلقاً، بنفس فلسفة
  "IrDatabase" في GT-TAHAKOM (فهرس + تحميل كسول عبر `context.assets`).
- **تحديث اختياري:** `ContentRepository.checkForUpdate()` يجلب
  `content/manifest.json` من رابط GitHub Pages أعلاه (مهلة قصيرة، فشل صامت
  إن لم يوجد إنترنت)، يقارن حقل `version` بالنسخة المخزَّنة محلياً
  (`SharedPreferences`)، فإن كانت أحدث يُنزّل الملفات المتغيّرة إلى
  `context.filesDir/content/` وتُقرأ من هناك بالأولوية على `assets/`.
- **مخطط JSON** (مبدئي):
  - `manifest.json`: `{ "version": 1, "files": ["home.json","najiyah.json",...] }`
  - `home.json`: نص البسملة، الحديث وتخريجه، وصف الصفحة الرئيسية
  - `sects/index.json`: فهرس خفيف لكل فرقة (id, title, icon, category, tagline)
  - `sects/<id>.json`: تفصيل الفرقة (عنوان، تصنيف/نشأة كنص، أقسام `[{heading, body}]`، جدول تشعّب اختياري، قائمة مصادر `[{label, url?}]`)
  - `najiyah.json` / `manhaj.json` / `sources.json` / `about.json`: بنية أبسط (عنوان + أقسام نصية)

## بنية المجلدات المستهدفة

```
Firqah-Najiyah/
├── (الموقع الحالي دون تغيير: index.html, style.css, script.js, pages/, components/, assets/, 404.html)
└── app/                                    ← جذر مشروع Gradle للتطبيق
    ├── settings.gradle.kts, build.gradle.kts, gradle.properties
    ├── gradle/ (wrapper + libs.versions.toml), gradlew(.bat)
    ├── .gitignore                          (build/, .gradle/, local.properties, *.jks, keystore.properties)
    ├── PLAN.md                             (نسخة موسّعة من هذه الخطة كمرجع دائم داخل المستودع)
    ├── content/                            ← مصدر المحتوى المنشور تلقائياً عبر GitHub Pages
    │   ├── manifest.json
    │   ├── home.json / najiyah.json / manhaj.json / sources.json / about.json
    │   └── sects/ (index.json + ملف لكل فرقة)
    └── app/                                ← وحدة Gradle (نمط أندرويد القياسي)
        ├── build.gradle.kts
        └── src/main/
            ├── AndroidManifest.xml
            ├── assets/content/              (نسخة مطابقة لـ app/content/ أعلاه، للعمل أوفلاين)
            ├── res/ (أيقونة من assets/logo.png، values/strings.xml، themes.xml)
            └── java/com/gnutux/najiyah/
                ├── MainActivity.kt
                ├── ui/theme/ (Color.kt, Tokens.kt, Theme.kt)
                ├── ui/navigation/Screen.kt
                ├── data/model/ (Sect.kt, SectSection.kt, HomeContent.kt, StaticPage.kt, ContentManifest.kt)
                ├── data/ContentRepository.kt   (تحميل assets + تحديث اختياري + بحث نصي بسيط)
                └── feature/
                    ├── home/HomeScreen.kt
                    ├── encyclopedia/EncyclopediaListScreen.kt + SectDetailScreen.kt
                    ├── najiyah/NajiyahScreen.kt, manhaj/ManhajScreen.kt
                    ├── sources/SourcesScreen.kt, about/AboutScreen.kt
                    └── search/SearchScreen.kt
```

## نطاق هذه الجلسة (Phase 0) مقابل العمل اللاحق

**الآن (بعد الموافقة):**
1. سقالة Gradle/Compose كاملة وقابلة للبناء فعلياً (تحقَّق بـ `./gradlew assembleDebug`).
2. نظام الثيم (فاتح/داكن) بألوان الموقع، وشاشة رئيسية + فهرس الموسوعة + شاشة تفصيل عامة + بحث بسيط.
3. `ContentRepository` بمنطق أوفلاين + تحديث اختياري كامل وقابل للاختبار.
4. محتوى JSON فعلي لكل الصفحات الثابتة (الرئيسية، الفرقة الناجية، المنهج، المصادر، من نحن) + **3 فرق نموذجية كاملة** (الأشاعرة، الشيعة، الدروز) لإثبات الأنبوب كاملاً من طرف لطرف.
5. تحديث `README.md` الجذري بقسم قصير يشير لتطبيق الهاتف.
6. Commit وPush إلى نفس مستودع `Firqah-Najiyah` (فرع `main`).

**لاحقاً (مهام تتبَّع عبر TaskCreate، تُنفَّذ في جلسات/أوامر تالية):**
- تحويل بقية الفرق الـ16 المتبقية إلى JSON (يمكن توزيعها على وكلاء متوازيين كما فعلنا مع تدقيق المصادر).
- أيقونة تطبيق تكيّفية (adaptive icon) حقيقية من الشعار + splash screen.
- اختبار فعلي على مُحاكي/جهاز، ضبط التوقيع (`keystore.properties` بنمط GT-TAHAKOM).
- CI (GitHub Actions build+release) وFastlane وF-Droid metadata، بنفس نمط GT-TAHAKOM، عند الاستعداد للنشر.

## التحقق

- `cd app && ./gradlew assembleDebug` يجب أن ينجح بلا أخطاء (يؤكد صحة إعداد Gradle/Kotlin/Compose وتوافق النماذج مع JSON الفعلي).
- `./gradlew lint` (اختياري) للتأكد من نظافة الكود الأساسي.
- مراجعة يدوية لملفات JSON الثلاثة النموذجية للفرق للتأكد من مطابقتها لمحتوى صفحات HTML المقابلة (لا اختلاق ولا حذف معلومات).
