<template>
  <div class="home-page" :class="themeClass">
    <section class="page-hero">
      <div class="hero-banner">
        <div class="hero-kicker">{{ copy.hero.kicker }}</div>
        <h1 class="hero-title">{{ copy.hero.title }}</h1>
        <p class="hero-desc">
          {{ copy.hero.desc }}
        </p>
        <div class="hero-actions">
          <el-button type="primary" @click="router.push('/fomo')">{{ copy.hero.primaryAction }}</el-button>
          <el-button @click="router.push('/visualization')">{{ copy.hero.visualizationAction }}</el-button>
          <el-button @click="router.push('/news')">{{ copy.hero.newsAction }}</el-button>
          <el-button v-if="isAdmin" @click="router.push('/admin')">{{ copy.hero.adminAction }}</el-button>
          <el-divider direction="vertical" />
          <el-button :icon="themeMode === 'light' ? Moon : Sunny" plain @click="toggleTheme">
            {{ themeMode === 'light' ? copy.hero.darkThemeLabel : copy.hero.lightThemeLabel }}
          </el-button>
          <el-button :icon="SwitchButton" plain @click="toggleLang">
            {{ lang === 'zh' ? 'EN' : '中' }}
          </el-button>
        </div>
      </div>

      <div class="hero-side">
        <div class="hero-card">
          <div class="hero-card__label">{{ copy.hero.currentUser }}</div>
          <div class="hero-card__value">{{ profile?.username || copy.hero.notLoggedIn }}</div>
          <div class="hero-card__meta">
            {{ profile?.major || copy.hero.majorFallback }}<br />
            {{ profile?.learningGoal || copy.hero.goalFallback }}
          </div>
        </div>
        <div class="hero-card">
          <div class="hero-card__label">{{ copy.hero.latestStatus }}</div>
          <div class="hero-card__value">{{ statusText }}</div>
          <div class="hero-card__meta">{{ statusMeta }}</div>
        </div>
      </div>
    </section>

    <section class="feature-grid">
      <div class="feature-tile">
        <div class="feature-label">{{ copy.features.system.label }}</div>
        <div class="feature-value">{{ overviewCards[0].value }}</div>
        <div class="feature-desc">{{ overviewCards[0].desc }}</div>
      </div>
      <div class="feature-tile">
        <div class="feature-label">{{ copy.features.closure.label }}</div>
        <div class="feature-value">{{ overviewCards[1].value }}</div>
        <div class="feature-desc">{{ overviewCards[1].desc }}</div>
      </div>
      <div class="feature-tile">
        <div class="feature-label">{{ copy.features.news.label }}</div>
        <div class="feature-value">{{ overviewCards[2].value }}</div>
        <div class="feature-desc">{{ overviewCards[2].desc }}</div>
      </div>
      <div class="feature-tile">
        <div class="feature-label">{{ copy.features.admin.label }}</div>
        <div class="feature-value">{{ overviewCards[3].value }}</div>
        <div class="feature-desc">{{ overviewCards[3].desc }}</div>
      </div>
    </section>

    <div class="grid-2">
      <div class="panel">
        <div class="toolbar">
          <div>
            <div class="page-title page-title--compact">{{ copy.sections.entry.title }}</div>
            <div class="page-subtitle page-subtitle--compact">{{ copy.sections.entry.subtitle }}</div>
          </div>
        </div>
        <div class="entry-grid">
          <el-card v-for="item in entryCards" :key="item.title" class="entry-card" shadow="never" @click="router.push(item.path)">
            <div class="entry-icon">{{ item.icon }}</div>
            <div class="entry-title">{{ item.title }}</div>
            <div class="entry-desc">{{ item.desc }}</div>
          </el-card>
        </div>
      </div>

      <div class="panel">
        <div class="toolbar">
          <div>
            <div class="page-title page-title--compact">{{ copy.sections.status.title }}</div>
            <div class="page-subtitle page-subtitle--compact">{{ copy.sections.status.subtitle }}</div>
          </div>
        </div>
        <template v-if="latestScore">
          <el-descriptions :column="1" border>
            <el-descriptions-item :label="copy.sections.status.totalScoreLabel">{{ latestScore.totalScore }}</el-descriptions-item>
            <el-descriptions-item :label="copy.sections.status.levelLabel">{{ localizedLevel }}</el-descriptions-item>
            <el-descriptions-item :label="copy.sections.status.profileLabel">
              {{ copy.sections.status.sleepLabel }} {{ profileSnapshot.sleepHours }} {{ copy.sections.status.hoursUnit }} /
              {{ copy.sections.status.focusLabel }} {{ profileSnapshot.focusLevelLabel }}
            </el-descriptions-item>
          </el-descriptions>
          <div class="status-chip-row">
            <el-tag effect="plain" type="info">{{ copy.sections.status.tags.intervention }}</el-tag>
            <el-tag effect="plain" type="success">{{ copy.sections.status.tags.retest }}</el-tag>
            <el-tag effect="plain" type="warning">{{ copy.sections.status.tags.history }}</el-tag>
          </div>
        </template>
        <template v-else>
          <el-empty :description="copy.sections.status.empty" />
        </template>
      </div>
    </div>

    <div class="grid-2" style="margin-top: 16px">
      <div class="panel">
        <div class="toolbar">
          <div>
            <div class="page-title page-title--compact">{{ copy.sections.news.title }}</div>
            <div class="page-subtitle page-subtitle--compact">{{ copy.sections.news.subtitle }}</div>
          </div>
          <el-button text type="primary" @click="router.push('/news')">{{ copy.sections.news.button }}</el-button>
        </div>
        <div class="news-preview">
          <div v-for="item in newsPreview" :key="item.title" class="news-preview-item">
            <img :src="item.urlToImage" :alt="item.title" />
            <div class="news-preview-content">
              <div class="news-preview-title">{{ item.title }}</div>
              <div class="news-preview-desc">{{ item.description }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="toolbar">
          <div>
            <div class="page-title page-title--compact">{{ copy.sections.about.title }}</div>
            <div class="page-subtitle page-subtitle--compact">{{ copy.sections.about.subtitle }}</div>
          </div>
        </div>
        <el-space direction="vertical" fill style="width: 100%">
          <el-alert :title="copy.sections.about.alerts.intervention" type="info" :closable="false" />
          <el-alert :title="copy.sections.about.alerts.retest" type="success" :closable="false" />
          <el-alert :title="copy.sections.about.alerts.admin" type="warning" :closable="false" />
          <el-alert :title="copy.sections.about.alerts.news" type="info" :closable="false" />
        </el-space>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Moon, Sunny, SwitchButton } from '@element-plus/icons-vue'
import { getProfileApi } from '../api/auth'
import { scoreApi, latestFomoApi } from '../api/fomo'
import { myRecommendationApi } from '../api/recommendation'
import { createVisualizationMock } from '../mock/visualizationMock'
import { createMockNewsFeed } from '../mock/newsMock'
import { authState } from '../utils/auth'

const STORAGE_THEME_KEY = 'home-theme-mode'
const STORAGE_LANG_KEY = 'home-lang'

const router = useRouter()
const profile = ref(null)
const score = ref(null)
const latestScore = ref(null)
const recommendations = ref([])
const latestTest = ref(null)
const themeMode = ref(localStorage.getItem(STORAGE_THEME_KEY) || 'light')
const lang = ref(localStorage.getItem(STORAGE_LANG_KEY) || 'zh')

const dictionary = {
  zh: {
    hero: {
      kicker: 'Graduation Thesis Platform',
      title: 'AI时代大学生信息焦虑分析与个性化学习推荐系统',
      desc: '这是一个面向大学生信息焦虑场景的毕业设计平台，围绕测评、干预、复测、推荐、新闻和可视化分析形成完整闭环。你可以在这里快速进入 FOMO 测评、查看学习建议、浏览 AI 新闻，或者直接打开后台管理。',
      primaryAction: '开始测评',
      visualizationAction: '查看数据大屏',
      newsAction: '浏览新闻栏目',
      adminAction: '进入后台',
      darkThemeLabel: '切换深色',
      lightThemeLabel: '切换浅色',
      currentUser: '当前用户',
      latestStatus: '最新状态',
      notLoggedIn: '未登录',
      majorFallback: '尚未填写专业',
      goalFallback: '尚未填写学习目标'
    },
    features: {
      system: { label: '系统总览' },
      closure: { label: '干预闭环' },
      news: { label: '新闻栏目' },
      admin: { label: '后台能力' }
    },
    sections: {
      entry: {
        title: '核心功能入口',
        subtitle: '项目的主要模块都在这里，适合答辩演示时快速跳转。'
      },
      status: {
        title: '最近状态',
        subtitle: '展示最新测评和学习反馈，帮助快速理解系统当前作用。',
        totalScoreLabel: '最新总分',
        levelLabel: '焦虑等级',
        profileLabel: '补充画像',
        sleepLabel: '睡眠',
        hoursUnit: '小时',
        focusLabel: '专注',
        empty: '尚未完成测评，完成后这里会显示最新结果',
        tags: {
          intervention: '干预计划已生成',
          retest: '支持复测对比',
          history: '可查看历史记录'
        }
      },
      news: {
        title: 'AI 与科技新闻摘要',
        subtitle: '展示最近的新闻卡片，方便答辩时说明项目具备内容聚合能力。',
        button: '进入栏目'
      },
      about: {
        title: '项目说明',
        subtitle: '简短说明系统在毕业设计答辩中的核心价值。',
        alerts: {
          intervention: '测评不是终点：系统会根据测评结果自动生成干预计划。',
          retest: '复测是关键：可以看到干预前后分数变化和趋势。',
          admin: '后台有总览：管理员可查看用户、测评、聊天、推荐和干预完成率。',
          news: '新闻栏目与数据大屏：让系统具备展示与内容聚合能力。'
        }
      }
    },
    levels: {
      正常: 'Normal',
      轻度焦虑: 'Mild Anxiety',
      中度焦虑: 'Moderate Anxiety',
      高度焦虑: 'High Anxiety'
    },
    focusLevels: {
      1: 'Very Low',
      2: 'Low',
      3: 'Medium',
      4: 'High',
      5: 'Very High'
    }
  },
  en: {
    hero: {
      kicker: 'Graduation Thesis Platform',
      title: 'AI-era Information Anxiety Analysis and Personalized Learning Recommendation System',
      desc: 'This graduation project focuses on information anxiety among college students. It forms a closed loop around assessment, intervention, retest, recommendation, news, and visualization. You can start a FOMO assessment, review learning guidance, browse AI news, or open the admin console directly.',
      primaryAction: 'Start Assessment',
      visualizationAction: 'Open Dashboard',
      newsAction: 'Browse News',
      adminAction: 'Admin Console',
      darkThemeLabel: 'Dark Mode',
      lightThemeLabel: 'Light Mode',
      currentUser: 'Current User',
      latestStatus: 'Latest Status',
      notLoggedIn: 'Not signed in',
      majorFallback: 'Major not set',
      goalFallback: 'Learning goal not set'
    },
    features: {
      system: { label: 'System Overview' },
      closure: { label: 'Intervention Loop' },
      news: { label: 'News Feed' },
      admin: { label: 'Admin Access' }
    },
    sections: {
      entry: {
        title: 'Core Entry Points',
        subtitle: 'The main modules are here for fast navigation during demos.'
      },
      status: {
        title: 'Recent Status',
        subtitle: 'Shows the latest assessment and learning feedback for a quick overview.',
        totalScoreLabel: 'Latest Score',
        levelLabel: 'Anxiety Level',
        profileLabel: 'Profile Snapshot',
        sleepLabel: 'Sleep',
        hoursUnit: 'hours',
        focusLabel: 'Focus',
        empty: 'No assessment yet. Results will appear here after you finish one.',
        tags: {
          intervention: 'Intervention plan ready',
          retest: 'Retest comparison supported',
          history: 'History available'
        }
      },
      news: {
        title: 'AI and Tech News',
        subtitle: 'Recent news cards help demonstrate the content aggregation feature.',
        button: 'Open Section'
      },
      about: {
        title: 'Project Notes',
        subtitle: 'A short summary of the system value for thesis defense.',
        alerts: {
          intervention: 'Assessment is not the end: the system generates intervention plans automatically.',
          retest: 'Retest matters: compare scores and trends before and after intervention.',
          admin: 'Admin overview: users, assessments, chat, recommendations, and intervention completion rate.',
          news: 'News feed and dashboard: content aggregation and presentation in one system.'
        }
      }
    },
    levels: {
      正常: 'Normal',
      轻度焦虑: 'Mild Anxiety',
      中度焦虑: 'Moderate Anxiety',
      高度焦虑: 'High Anxiety'
    },
    focusLevels: {
      1: 'Very Low',
      2: 'Low',
      3: 'Medium',
      4: 'High',
      5: 'Very High'
    }
  }
}

const copy = computed(() => dictionary[lang.value] || dictionary.zh)

const profileSnapshot = computed(() => ({
  sleepHours: latestTest.value?.sleepHours ?? 7,
  focusLevelLabel: copy.value.focusLevels[latestTest.value?.focusLevel] || (lang.value === 'zh' ? '一般' : 'Medium')
}))

const isAdmin = computed(() => authState.value.user?.username === 'admin')

const statusText = computed(() => {
  if (latestScore.value) return `${localizedLevel.value} · ${latestScore.value.totalScore} ${lang.value === 'zh' ? '分' : 'pts'}`
  if (profile.value) return lang.value === 'zh' ? '欢迎回来' : 'Welcome back'
  return copy.value.hero.notLoggedIn
})

const statusMeta = computed(() => {
  if (latestScore.value) {
    return lang.value === 'zh'
      ? '系统已记录你的最新测评，干预计划与复测对比可在结果页查看。'
      : 'Your latest assessment is recorded. Intervention and retest comparison are available on the results page.'
  }
  if (profile.value) {
    return lang.value === 'zh'
      ? '你可以先完成一次 FOMO 测评，再进入推荐、新闻和数据大屏。'
      : 'Finish a FOMO assessment first, then explore recommendations, news, and the dashboard.'
  }
  return lang.value === 'zh'
    ? '登录后可以查看个人画像、学习推荐和测评历史。'
    : 'Sign in to view your profile, learning recommendations, and assessment history.'
})

const localizedLevel = computed(() => copy.value.levels[latestScore.value?.level] || latestScore.value?.level || (lang.value === 'zh' ? '未完成' : 'Not available'))

const overviewCards = computed(() => {
  const viz = createVisualizationMock()
  return [
    {
      value: `${viz.kpis[0].value}+`,
      desc:
        lang.value === 'zh'
          ? '系统围绕测评、推荐、聊天、数据可视化和新闻栏目形成完整闭环。'
          : 'The system forms a complete loop around assessment, recommendation, chat, visualization, and news.'
    },
    {
      value: '测评-干预-复测',
      desc:
        lang.value === 'zh'
          ? '提交测评后自动生成干预计划，并可在结果页标记完成。'
          : 'Submit an assessment to generate an intervention plan and track completion on the results page.'
    },
    {
      value: 'TechCrunch RSS',
      desc:
        lang.value === 'zh'
          ? '新闻栏目默认接入 TechCrunch RSS，并在失败时回退本地示例。'
          : 'The news feed uses TechCrunch RSS and falls back to local samples when needed.'
    },
    {
      value: 'Admin Dashboard',
      desc:
        lang.value === 'zh'
          ? '后台可查看用户、测评、聊天、推荐、干预完成率等数据。'
          : 'The admin console shows users, assessments, chats, recommendations, and intervention metrics.'
    }
  ]
})

const entryCards = computed(() => [
  { icon: '1', title: lang.value === 'zh' ? 'FOMO 测试' : 'FOMO Test', desc: lang.value === 'zh' ? '填写行为与画像项，生成焦虑评分和干预计划。' : 'Enter behavior and profile items to generate anxiety scores and intervention plans.', path: '/fomo' },
  { icon: '2', title: lang.value === 'zh' ? '结果分析' : 'Results', desc: lang.value === 'zh' ? '查看最新测评、干预建议和复测对比。' : 'Review the latest assessment, intervention advice, and retest comparison.', path: '/result' },
  { icon: '3', title: lang.value === 'zh' ? '学习推荐' : 'Recommendations', desc: lang.value === 'zh' ? '依据焦虑等级和学习目标生成学习路径。' : 'Generate learning paths based on anxiety level and learning goals.', path: '/recommendation' },
  { icon: '4', title: lang.value === 'zh' ? 'AI 聊天' : 'AI Chat', desc: lang.value === 'zh' ? '支持上下文对话的学习助手。' : 'A learning assistant with contextual conversation support.', path: '/chat' },
  { icon: '5', title: lang.value === 'zh' ? '数据可视化' : 'Dashboard', desc: lang.value === 'zh' ? '查看焦虑分布、趋势和关联分析。' : 'Inspect anxiety distribution, trends, and correlations.', path: '/visualization' },
  { icon: '6', title: lang.value === 'zh' ? '新闻栏目' : 'News', desc: lang.value === 'zh' ? '浏览 AI 与科技领域新闻图文聚合。' : 'Browse AI and tech news with image cards.', path: '/news' }
])

const newsPreview = ref(createMockNewsFeed('ai', 'OpenAI', 3).articles)

function toggleTheme() {
  themeMode.value = themeMode.value === 'light' ? 'dark' : 'light'
  localStorage.setItem(STORAGE_THEME_KEY, themeMode.value)
}

function toggleLang() {
  lang.value = lang.value === 'zh' ? 'en' : 'zh'
  localStorage.setItem(STORAGE_LANG_KEY, lang.value)
}

const themeClass = computed(() => `home-page--${themeMode.value}`)

watch(themeMode, (value) => {
  localStorage.setItem(STORAGE_THEME_KEY, value)
})

watch(lang, (value) => {
  localStorage.setItem(STORAGE_LANG_KEY, value)
})

onMounted(async () => {
  try {
    profile.value = (await getProfileApi()).data
  } catch (e) {
    profile.value = null
  }

  try {
    score.value = (await scoreApi()).data
    latestScore.value = score.value
  } catch (e) {
    score.value = null
  }

  try {
    latestTest.value = (await latestFomoApi()).data
  } catch (e) {
    latestTest.value = null
  }

  try {
    recommendations.value = (await myRecommendationApi()).data || []
  } catch (e) {
    recommendations.value = []
  }
})
</script>

<style scoped>
.home-page {
  --home-page-bg: #f5f7fb;
  --home-panel-bg: #ffffff;
  --home-panel-soft: #f8fafc;
  --home-border: #e5e7eb;
  --home-text: #1f2937;
  --home-title: #0f172a;
  --home-muted: #6b7280;
  --home-accent: #2563eb;
  --home-accent-soft: #dbeafe;
  --home-shadow: 0 18px 42px rgba(37, 99, 235, 0.08);
  min-height: 100%;
  padding: 20px;
  border-radius: 16px;
  background: var(--home-page-bg);
  color: var(--home-text);
}

.home-page--dark {
  --home-page-bg: #0b0b0c;
  --home-panel-bg: #121214;
  --home-panel-soft: #18181b;
  --home-border: #2a2a2f;
  --home-text: #e5e7eb;
  --home-title: #f9fafb;
  --home-muted: #a1a1aa;
  --home-accent: #f5f5f5;
  --home-accent-soft: #27272a;
  --home-shadow: 0 18px 42px rgba(0, 0, 0, 0.35);
  background: var(--home-page-bg);
}

.home-page :deep(.el-button) {
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.home-page :deep(.el-button:hover) {
  transform: translateY(-1px);
}

.home-page :deep(.el-alert) {
  border-radius: 12px;
}

.home-page :deep(.el-descriptions) {
  background: var(--home-panel-bg);
  color: var(--home-text);
}

.home-page :deep(.el-descriptions__label),
.home-page :deep(.el-descriptions__content) {
  color: var(--home-text);
}

.home-page :deep(.el-tag) {
  border-color: var(--home-border);
  background: var(--home-panel-soft);
  color: var(--home-text);
}

.page-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.9fr);
  gap: 18px;
  margin-bottom: 18px;
}

.hero-banner,
.hero-card,
.feature-tile,
.panel,
.entry-card,
.news-preview-item {
  border-color: var(--home-border) !important;
  background: var(--home-panel-bg) !important;
  color: var(--home-text);
  box-shadow: var(--home-shadow);
}

.hero-banner {
  padding: 24px;
  border-radius: 14px;
  border: 1px solid var(--home-border);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(243, 248, 255, 0.98)),
    radial-gradient(circle at top right, rgba(74, 144, 255, 0.12), transparent 35%);
}

.home-page--dark .hero-banner {
  background:
    linear-gradient(135deg, rgba(24, 24, 27, 0.98), rgba(12, 12, 12, 0.98)),
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.08), transparent 38%);
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: var(--home-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.hero-title {
  margin: 0;
  font-size: 30px;
  line-height: 1.2;
  color: var(--home-title);
}

.hero-desc {
  max-width: 760px;
  margin: 12px 0 0;
  color: var(--home-muted);
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.hero-side {
  display: grid;
  gap: 14px;
}

.hero-card {
  padding: 18px;
  border-radius: 14px;
  border: 1px solid var(--home-border);
  background: linear-gradient(180deg, rgba(255,255,255,0.98), rgba(245,249,255,0.98));
}

.home-page--dark .hero-card,
.home-page--dark .feature-tile,
.home-page--dark .panel,
.home-page--dark .news-preview-item,
.home-page--dark .entry-card {
  background: var(--home-panel-bg) !important;
}

.hero-card__label,
.feature-label {
  color: var(--home-muted);
  font-size: 12px;
}

.hero-card__value,
.feature-value {
  margin-top: 10px;
  font-size: 22px;
  font-weight: 700;
  color: var(--home-title);
}

.hero-card__meta,
.feature-desc,
.entry-desc,
.news-preview-desc,
.page-subtitle {
  color: var(--home-muted);
}

.hero-card__meta {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.6;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.feature-tile {
  padding: 18px;
  border-radius: 14px;
  border: 1px solid var(--home-border);
  background: var(--home-panel-bg);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.feature-tile:hover {
  transform: translateY(-2px);
  box-shadow: 0 18px 36px rgba(37, 99, 235, 0.1);
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 16px;
  color: var(--home-title);
}

.page-title--compact {
  font-size: 18px;
  margin-bottom: 6px;
}

.page-subtitle {
  margin: -8px 0 16px;
}

.page-subtitle--compact {
  margin: 0;
}

.panel {
  background: var(--home-panel-bg);
  border: 1px solid var(--home-border);
  border-radius: 12px;
  padding: 16px;
}

.grid-2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.entry-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.entry-card {
  cursor: pointer;
  border-radius: 12px;
  border: 1px solid var(--home-border);
  background: var(--home-panel-bg);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.entry-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 18px 36px rgba(37, 99, 235, 0.1);
}

.entry-icon {
  width: 34px;
  height: 34px;
  line-height: 34px;
  text-align: center;
  border-radius: 10px;
  background: var(--home-accent-soft);
  color: var(--home-accent);
  font-weight: 700;
}

.entry-title {
  margin-top: 10px;
  font-size: 15px;
  font-weight: 700;
  color: var(--home-title);
}

.entry-desc {
  margin-top: 8px;
  line-height: 1.7;
  font-size: 12px;
}

.status-chip-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 14px;
}

.news-preview {
  display: grid;
  gap: 12px;
}

.news-preview-item {
  display: grid;
  grid-template-columns: 130px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
  padding: 10px;
  border: 1px solid var(--home-border);
  border-radius: 12px;
  background: var(--home-panel-bg);
}

.news-preview-item img {
  width: 130px;
  height: 86px;
  object-fit: cover;
  border-radius: 10px;
}

.news-preview-title {
  font-weight: 700;
  color: var(--home-title);
  line-height: 1.5;
}

.news-preview-desc {
  margin-top: 6px;
  line-height: 1.6;
  font-size: 12px;
}

@media (max-width: 960px) {
  .grid-2,
  .feature-grid,
  .page-hero,
  .entry-grid {
    grid-template-columns: 1fr;
  }

  .news-preview-item {
    grid-template-columns: 1fr;
  }

  .news-preview-item img {
    width: 100%;
  }

  .app-header {
    flex-wrap: wrap;
  }

  .nav-menu {
    width: 100%;
    overflow-x: auto;
  }
}
</style>
