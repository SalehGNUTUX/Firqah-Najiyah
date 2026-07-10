import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// تحميل بيانات التوقيع من keystore.properties إن وُجد (غير مرفوع إلى git).
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) keystorePropsFile.inputStream().use { load(it) }
}

// اسم ملف الحزمة الناتجة يطابق اسم المشروع (Firqah-Najiyah) بدل الاسم
// الافتراضي المشتق من اسم الوحدة "app" (app-debug.apk).
base {
    archivesName.set("Firqah-Najiyah")
}

android {
    namespace = "com.gnutux.najiyah"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gnutux.najiyah"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.0"

        vectorDrawables { useSupportLibrary = true }
    }

    signingConfigs {
        // يُفعَّل التوقيع فقط عند توفّر keystore.properties (محلياً) — وإلا يبني debug عادياً.
        if (keystorePropsFile.exists()) {
            create("release") {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (keystorePropsFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // أساسيات أندرويد + دورة الحياة
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Jetpack Compose (عبر BOM)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // تزامن + تحديث المحتوى عن بُعد (اختياري، فشل صامت بلا إنترنت)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.okhttp)

    // أدوات التطوير
    debugImplementation(libs.androidx.ui.tooling)
}
