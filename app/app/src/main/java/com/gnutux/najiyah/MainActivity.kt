package com.gnutux.najiyah

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gnutux.najiyah.data.ContentRepository
import com.gnutux.najiyah.data.model.HomeContent
import com.gnutux.najiyah.data.model.SectDetail
import com.gnutux.najiyah.data.model.SectSummary
import com.gnutux.najiyah.data.model.StaticPage
import com.gnutux.najiyah.feature.about.AboutScreen
import com.gnutux.najiyah.feature.encyclopedia.EncyclopediaListScreen
import com.gnutux.najiyah.feature.encyclopedia.SectDetailScreen
import com.gnutux.najiyah.feature.home.HomeScreen
import com.gnutux.najiyah.feature.manhaj.ManhajScreen
import com.gnutux.najiyah.feature.najiyah.NajiyahScreen
import com.gnutux.najiyah.feature.search.SearchScreen
import com.gnutux.najiyah.feature.sources.SourcesScreen
import com.gnutux.najiyah.ui.components.NajiyahTopBar
import com.gnutux.najiyah.ui.navigation.Screen
import com.gnutux.najiyah.ui.theme.NajiyahTheme

private const val SHARE_SITE_URL = "https://salehgnutux.github.io/Firqah-Najiyah/index.html"

/** النشاط الوحيد — يستضيف واجهة Compose بالكامل. */
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository: ContentRepository = (application as NajiyahApp).repository

        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(systemDark) }

            NajiyahTheme(darkTheme = darkTheme) {
                var screen by remember { mutableStateOf<Screen>(Screen.Home) }

                var home by remember { mutableStateOf<HomeContent?>(null) }
                var najiyahPage by remember { mutableStateOf<StaticPage?>(null) }
                var manhajPage by remember { mutableStateOf<StaticPage?>(null) }
                var sourcesPage by remember { mutableStateOf<StaticPage?>(null) }
                var aboutPage by remember { mutableStateOf<StaticPage?>(null) }
                var sectIndex by remember { mutableStateOf<List<SectSummary>?>(null) }
                var currentDetail by remember { mutableStateOf<SectDetail?>(null) }

                // تحميل أولي + فحص صامت لتحديث المحتوى عن بُعد.
                LaunchedEffect(Unit) {
                    home = repository.loadHome()
                    sectIndex = repository.loadSectIndex()
                    repository.checkAndApplyUpdate()
                }

                LaunchedEffect(screen) {
                    when (val s = screen) {
                        Screen.Najiyah -> if (najiyahPage == null) najiyahPage = repository.loadStaticPage("najiyah.json")
                        Screen.Manhaj -> if (manhajPage == null) manhajPage = repository.loadStaticPage("manhaj.json")
                        Screen.Sources -> if (sourcesPage == null) sourcesPage = repository.loadStaticPage("sources.json")
                        Screen.About -> if (aboutPage == null) aboutPage = repository.loadStaticPage("about.json")
                        is Screen.SectDetail -> {
                            currentDetail = null
                            currentDetail = repository.loadSectDetail(s.id)
                        }
                        else -> Unit
                    }
                }

                fun shareApp() {
                    val text = "📖 موقع \"الفرقة الناجية\": تعريفٌ بعقيدة أهل السنة والجماعة، وموسوعة شاملة للفرق الضالة.\n$SHARE_SITE_URL"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    startActivity(Intent.createChooser(intent, null))
                }

                val title = when (val s = screen) {
                    Screen.Home -> getString(R.string.app_name)
                    Screen.Najiyah -> getString(R.string.nav_najiyah)
                    Screen.Encyclopedia -> getString(R.string.nav_encyclopedia)
                    is Screen.SectDetail -> s.title
                    Screen.Manhaj -> getString(R.string.nav_manhaj)
                    Screen.Sources -> getString(R.string.nav_sources)
                    Screen.About -> getString(R.string.nav_about)
                    Screen.Search -> getString(R.string.nav_search)
                }

                Scaffold(
                    topBar = {
                        NajiyahTopBar(
                            title = title,
                            onBack = if (screen != Screen.Home) {
                                {
                                    screen = when (screen) {
                                        is Screen.SectDetail -> Screen.Encyclopedia
                                        else -> Screen.Home
                                    }
                                }
                            } else null,
                            actions = {
                                IconButton(onClick = { darkTheme = !darkTheme }) {
                                    Icon(
                                        if (darkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                        contentDescription = getString(
                                            if (darkTheme) R.string.action_light_mode else R.string.action_dark_mode,
                                        ),
                                    )
                                }
                                IconButton(onClick = { shareApp() }) {
                                    Icon(Icons.Filled.Share, contentDescription = getString(R.string.action_share))
                                }
                            },
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = screen == Screen.Home,
                                onClick = { screen = Screen.Home },
                                icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                                label = { Text(getString(R.string.nav_home)) },
                            )
                            NavigationBarItem(
                                selected = screen == Screen.Encyclopedia,
                                onClick = { screen = Screen.Encyclopedia },
                                icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                                label = { Text(getString(R.string.nav_encyclopedia)) },
                            )
                            NavigationBarItem(
                                selected = screen == Screen.Search,
                                onClick = { screen = Screen.Search },
                                icon = { Icon(Icons.Filled.Search, contentDescription = null) },
                                label = { Text(getString(R.string.nav_search)) },
                            )
                            NavigationBarItem(
                                selected = screen == Screen.Sources || screen == Screen.About || screen == Screen.Manhaj,
                                onClick = { screen = Screen.Manhaj },
                                icon = { Icon(Icons.Filled.Menu, contentDescription = null) },
                                label = { Text("المزيد") },
                            )
                        }
                    },
                ) { padding ->
                    val contentModifier = Modifier.padding(padding)
                    when (val s = screen) {
                        Screen.Home -> HomeScreen(
                            content = home,
                            onOpenNajiyah = { screen = Screen.Najiyah },
                            onOpenEncyclopedia = { screen = Screen.Encyclopedia },
                            onOpenManhaj = { screen = Screen.Manhaj },
                            onOpenSources = { screen = Screen.Sources },
                            modifier = contentModifier,
                        )
                        Screen.Najiyah -> NajiyahScreen(najiyahPage, contentModifier)
                        Screen.Encyclopedia -> EncyclopediaListScreen(
                            sects = sectIndex,
                            onOpenSect = { screen = Screen.SectDetail(it.id, it.title) },
                            modifier = contentModifier,
                        )
                        is Screen.SectDetail -> {
                            val summary = sectIndex?.firstOrNull { it.id == s.id }
                            SectDetailScreen(
                                detail = currentDetail,
                                notPortedYet = summary != null && !summary.hasDetail,
                                modifier = contentModifier,
                            )
                        }
                        Screen.Manhaj -> ManhajScreen(manhajPage, contentModifier)
                        Screen.Sources -> SourcesScreen(sourcesPage, contentModifier)
                        Screen.About -> AboutScreen(aboutPage, contentModifier)
                        Screen.Search -> SearchScreen(
                            repository = repository,
                            onOpenSect = { screen = Screen.SectDetail(it.id, it.title) },
                            modifier = contentModifier,
                        )
                    }
                }
            }
        }
    }
}
