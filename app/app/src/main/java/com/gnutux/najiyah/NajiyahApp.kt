package com.gnutux.najiyah

import android.app.Application
import com.gnutux.najiyah.data.ContentRepository

/** تطبيق بسيط بلا إطار حقن تبعيات: يملك نسخة وحيدة من [ContentRepository]. */
class NajiyahApp : Application() {
    val repository: ContentRepository by lazy { ContentRepository(this) }
}
