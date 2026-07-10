// ========== تحديد المسار الأساسي ==========
// نعتمد على مسار ملف script.js نفسه (عبر document.currentScript) بدل حساب
// عمق المسار من جذر النطاق، لأن الحساب بالعمق ينكسر عند استضافة الموقع
// في مسار فرعي (مثل GitHub Pages: user.github.io/Firqah-Najiyah/).
const CURRENT_SCRIPT = document.currentScript;
const BASE_URL = new URL('.', CURRENT_SCRIPT.src);
const BASE_PATH = BASE_URL.pathname;

// ========== تحميل الهيدر والفوتر ==========
document.addEventListener('DOMContentLoaded', function() {
    const headerDiv = document.getElementById('header');
    const footerDiv = document.getElementById('footer');

    if (headerDiv) {
        fetch(new URL('components/header.html', BASE_URL).href)
            .then(res => {
                if (!res.ok) throw new Error('Header not found');
                return res.text();
            })
            .then(data => {
                headerDiv.innerHTML = data;
                attachThemeToggle();
                fixHeaderLinks();
                highlightCurrentPage();
                syncHeaderHeight();
            })
            .catch(err => {
                console.error('Error loading header:', err);
                headerDiv.innerHTML = `
                <header>
                    <a href="${BASE_PATH}index.html" class="logo">
                        <img src="${BASE_PATH}assets/logo.png" alt="شعار الفرقة الناجية" class="logo-icon" onerror="this.style.display='none'">
                        <span>الفرقة الناجية</span>
                    </a>
                    <nav>
                        <a href="${BASE_PATH}index.html">الرئيسية</a>
                        <a href="${BASE_PATH}pages/al-firqah-an-najiyah.html">الفرقة الناجية</a>
                        <a href="${BASE_PATH}pages/firaq/">الفرق الضالة</a>
                        <a href="${BASE_PATH}pages/manhaj.html">منهج النجاة</a>
                        <a href="${BASE_PATH}pages/sources.html">المصادر</a>
                        <a href="${BASE_PATH}pages/about.html">من نحن</a>
                    </nav>
                    <button id="theme-toggle" class="theme-toggle" aria-label="تبديل الوضع الليلي">🌙</button>
                </header>`;
                attachThemeToggle();
                highlightCurrentPage();
                syncHeaderHeight();
            });
    }

    if (footerDiv) {
        fetch(new URL('components/footer.html', BASE_URL).href)
            .then(res => {
                if (!res.ok) throw new Error('Footer not found');
                return res.text();
            })
            .then(data => {
                footerDiv.innerHTML = data;
            })
            .catch(err => {
                console.error('Error loading footer:', err);
                footerDiv.innerHTML = `
                <footer>
                    <p>موقع الفرقة الناجية – مشروع مفتوح المصدر ضمن <a href="https://github.com/SalehGNUTUX">SalehGNUTUX</a></p>
                    <p>قال رسول الله ﷺ: «من كان على مثل ما أنا عليه وأصحابي».</p>
                </footer>`;
            });
    }

    // تحميل الثيم المخزن
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeButtonText(savedTheme);

    initBackButton();
    initScrollFloatButton();

    window.addEventListener('resize', syncHeaderHeight);
    window.addEventListener('load', syncHeaderHeight);
    if (document.fonts && document.fonts.ready) {
        document.fonts.ready.then(syncHeaderHeight);
    }
});

// ========== ضبط ارتفاع الهيدر الثابت (position: fixed) ==========
// الهيدر أصبح ثابتاً (fixed) ليبقى ظاهراً دوماً أعلى الصفحة أثناء التمرير،
// فنحتاج ضبط padding-top للصفحة بمقدار ارتفاعه الفعلي، لأنه يختلف باختلاف
// حجم الشاشة (نمط الهيدر يتكدّس عمودياً على الجوال فيزداد ارتفاعه).
function syncHeaderHeight() {
    const header = document.querySelector('#header header');
    if (!header) return;
    document.documentElement.style.setProperty('--header-height', header.offsetHeight + 'px');
}

// ========== الوضع الليلي ==========
function attachThemeToggle() {
    const btn = document.getElementById('theme-toggle');
    if (!btn) return;
    btn.addEventListener('click', () => {
        const current = document.documentElement.getAttribute('data-theme');
        const next = current === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', next);
        localStorage.setItem('theme', next);
        updateThemeButtonText(next);
    });
}

function updateThemeButtonText(theme) {
    const btn = document.getElementById('theme-toggle');
    if (btn) {
        btn.textContent = theme === 'dark' ? '☀️ الوضع النهاري' : '🌙 الوضع الليلي';
    }
}

// ========== تصحيح روابط وشعار الهيدر بعد التحميل ==========
function fixHeaderLinks() {
    // روابط التنقل وشعار الموقع (نص + صورة) قد تُكتب بمسار نسبي بسيط
    // (مثل "index.html" أو "assets/logo.png")، فنحوّلها لمسار مطلق صحيح
    // بالاعتماد على BASE_PATH، بغض النظر عن عمق الصفحة الحالية.
    document.querySelectorAll('header nav a, header a.logo').forEach(link => {
        const href = link.getAttribute('href');
        if (href && !href.startsWith('http') && !href.startsWith('/') && !href.startsWith('./') && !href.startsWith('#')) {
            link.setAttribute('href', BASE_PATH + href);
        }
    });

    document.querySelectorAll('header .logo img').forEach(img => {
        const src = img.getAttribute('src');
        if (src && !src.startsWith('http') && !src.startsWith('/') && !src.startsWith('./') && !src.startsWith('data:')) {
            img.setAttribute('src', BASE_PATH + src);
        }
    });
}

// ========== تحديد الصفحة الحالية ==========
function highlightCurrentPage() {
    const normalize = (path) => {
        if (!path) return '';
        try {
            // نحوّل الرابط لمسار مطلق كامل قبل المقارنة
            path = new URL(path, window.location.href).pathname;
        } catch (e) { /* يبقى كما هو */ }
        return path.replace(/\/index\.html$/, '/').replace(/\/$/, '');
    };

    const currentPath = normalize(window.location.pathname);

    document.querySelectorAll('nav a').forEach(link => {
        const linkPath = link.getAttribute('href');
        if (linkPath && normalize(linkPath) === currentPath) {
            link.classList.add('active');
        }
    });
}

// ========== زر الرجوع للصفحة السابقة ==========
function initBackButton() {
    if (document.getElementById('fn-back-btn')) return;

    const btn = document.createElement('button');
    btn.id = 'fn-back-btn';
    btn.className = 'fn-float-btn fn-back-btn';
    btn.type = 'button';
    btn.setAttribute('aria-label', 'الرجوع للصفحة السابقة');
    btn.innerHTML = '<span aria-hidden="true">→</span><span class="fn-float-label">رجوع</span>';

    btn.addEventListener('click', () => {
        if (window.history.length > 1) {
            window.history.back();
        } else {
            window.location.href = new URL('index.html', BASE_URL).href;
        }
    });

    document.body.appendChild(btn);
}

// ========== زر عائم للتنقل أعلى/أسفل الصفحة ==========
function initScrollFloatButton() {
    if (document.getElementById('fn-scroll-btn')) return;

    const btn = document.createElement('button');
    btn.id = 'fn-scroll-btn';
    btn.className = 'fn-float-btn fn-scroll-btn';
    btn.type = 'button';
    document.body.appendChild(btn);

    function isPageScrollable() {
        return document.documentElement.scrollHeight > window.innerHeight + 150;
    }

    function update() {
        if (!isPageScrollable()) {
            btn.classList.remove('is-visible');
            return;
        }
        btn.classList.add('is-visible');

        const scrolledToBottom = window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 80;
        if (scrolledToBottom) {
            btn.innerHTML = '↑';
            btn.setAttribute('aria-label', 'الصعود لأعلى الصفحة');
            btn.dataset.dir = 'up';
        } else {
            btn.innerHTML = '↓';
            btn.setAttribute('aria-label', 'النزول لأسفل الصفحة');
            btn.dataset.dir = 'down';
        }
    }

    btn.addEventListener('click', () => {
        if (btn.dataset.dir === 'up') {
            window.scrollTo({ top: 0, behavior: 'smooth' });
        } else {
            window.scrollTo({ top: document.documentElement.scrollHeight, behavior: 'smooth' });
        }
    });

    window.addEventListener('scroll', update, { passive: true });
    window.addEventListener('resize', update);
    update();
}
