<template>
  <div class="home-page">
    <section class="page-hero">
      <div class="hero-banner">
        <div class="hero-kicker">Graduation Thesis Platform</div>
        <h1 class="hero-title">AI时代大学生信息焦虑分析与个性化学习推荐系统</h1>
        <p class="hero-desc">
          这是一个面向大学生信息焦虑场景的毕业设计平台，围绕测评、干预、复测、推荐、新闻和可视化分析形成完整闭环。
          你可以在这里快速进入 FOMO 测评、查看学习建议、浏览 AI 新闻，或者直接打开后台管理。
        </p>
        <div class="hero-actions">
          <el-button type="primary" @click="router.push('/fomo')">开始测评</el-button>
          <el-button @click="router.push('/visualization')">查看数据大屏</el-button>
          <el-button @click="router.push('/news')">浏览新闻栏目</el-button>
          <el-button v-if="isAdmin" @click="router.push('/admin')">进入后台</el-button>
        </div>
      </div>

      <div class="hero-side">
        <div class="hero-card">
          <div class="hero-card__label">当前用户</div>
          <div class="hero-card__value">{{ profile?.username || '未登录' }}</div>
          <div class="hero-card__meta">
            {{ profile?.major || '尚未填写专业' }}<br />
            {{ profile?.learningGoal || '尚未填写学习目标' }}
          </div>
        </div>
        <div class="hero-card">
          <div class="hero-card__label">最新状态</div>
          <div class="hero-card__value">{{ statusText }}</div>
          <div class="hero-card__meta">{{ statusMeta }}</div>
        </div>
      </div>
    </section>

    <section class="feature-grid">
      <div class="feature-tile">
        <div class="feature-label">系统总览</div>
        <div class="feature-value">{{ overviewCards[0].value }}</div>
        <div class="feature-desc">{{ overviewCards[0].desc }}</div>
      </div>
      <div class="feature-tile">
        <div class="feature-label">干预闭环</div>
        <div class="feature-value">{{ overviewCards[1].value }}</div>
        <div class="feature-desc">{{ overviewCards[1].desc }}</div>
      </div>
      <div class="feature-tile">
        <div class="feature-label">新闻栏目</div>
        <div class="feature-value">{{ overviewCards[2].value }}</div>
        <div class="feature-desc">{{ overviewCards[2].desc }}</div>
      </div>
      <div class="feature-tile">
        <div class="feature-label">后台能力</div>
        <div class="feature-value">{{ overviewCards[3].value }}</div>
        <div class="feature-desc">{{ overviewCards[3].desc }}</div>
      </div>
    </section>

    <div class="grid-2">
      <div class="panel">
        <div class="toolbar">
          <div>
            <div class="page-title" style="font-size: 18px; margin-bottom: 6px">核心功能入口</div>
            <div class="page-subtitle" style="margin: 0">项目的主要模块都在这里，适合答辩演示时快速跳转。</div>
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
            <div class="page-title" style="font-size: 18px; margin-bottom: 6px">最近状态</div>
            <div class="page-subtitle" style="margin: 0">展示最新测评和学习反馈，帮助快速理解系统当前作用。</div>
          </div>
        </div>
        <template v-if="latestScore">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="最新总分">{{ latestScore.totalScore }}</el-descriptions-item>
            <el-descriptions-item label="焦虑等级">{{ latestScore.level }}</el-descriptions-item>
            <el-descriptions-item label="补充画像">睡眠 {{ profileSnapshot.sleepHours }} 小时 / 专注 {{ profileSnapshot.focusLevelLabel }}</el-descriptions-item>
          </el-descriptions>
          <div class="status-chip-row">
            <el-tag effect="plain" type="info">干预计划已生成</el-tag>
            <el-tag effect="plain" type="success">支持复测对比</el-tag>
            <el-tag effect="plain" type="warning">可查看历史记录</el-tag>
          </div>
        </template>
        <template v-else>
          <el-empty description="尚未完成测评，完成后这里会显示最新结果" />
        </template>
      </div>
    </div>

    <div class="grid-2" style="margin-top: 16px">
      <div class="panel">
        <div class="toolbar">
          <div>
            <div class="page-title" style="font-size: 18px; margin-bottom: 6px">AI 与科技新闻摘要</div>
            <div class="page-subtitle" style="margin: 0">展示最近的新闻卡片，方便答辩时说明项目具备内容聚合能力。</div>
          </div>
          <el-button text type="primary" @click="router.push('/news')">进入栏目</el-button>
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
            <div class="page-title" style="font-size: 18px; margin-bottom: 6px">项目说明</div>
            <div class="page-subtitle" style="margin: 0">简短说明系统在毕业设计答辩中的核心价值。</div>
          </div>
        </div>
        <el-space direction="vertical" fill style="width: 100%">
          <el-alert title="测评不是终点：系统会根据测评结果自动生成干预计划。" type="info" :closable="false" />
          <el-alert title="复测是关键：可以看到干预前后分数变化和趋势。" type="success" :closable="false" />
          <el-alert title="后台有总览：管理员可查看用户、测评、聊天、推荐和干预完成率。" type="warning" :closable="false" />
          <el-alert title="新闻栏目与数据大屏：让系统具备展示与内容聚合能力。" type="info" :closable="false" />
        </el-space>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getProfileApi } from '../api/auth'
import { scoreApi } from '../api/fomo'
import { latestFomoApi } from '../api/fomo'
import { myRecommendationApi } from '../api/recommendation'
import { createVisualizationMock } from '../mock/visualizationMock'
import { createMockNewsFeed } from '../mock/newsMock'
import { authState } from '../utils/auth'

const router = useRouter()
const profile = ref(null)
const score = ref(null)
const latestScore = ref(null)
const recommendations = ref([])
const latestTest = ref(null)

const profileSnapshot = computed(() => ({
  sleepHours: latestTest.value?.sleepHours ?? 7,
  focusLevelLabel: {
    1: '很低',
    2: '较低',
    3: '一般',
    4: '较高',
    5: '很高'
  }[latestTest.value?.focusLevel] || '一般'
}))

const isAdmin = computed(() => authState.value.user?.username === 'admin')
const statusText = computed(() => {
  if (latestScore.value) return `${latestScore.value.level} · ${latestScore.value.totalScore} 分`
  if (profile.value) return '欢迎回来'
  return '未登录'
})
const statusMeta = computed(() => {
  if (latestScore.value) return '系统已记录你的最新测评，干预计划与复测对比可在结果页查看。'
  if (profile.value) return '你可以先完成一次 FOMO 测评，再进入推荐、新闻和数据大屏。'
  return '登录后可以查看个人画像、学习推荐和测评历史。'
})

const overviewCards = computed(() => {
  const viz = createVisualizationMock()
  return [
    { value: `${viz.kpis[0].value}+`, desc: '系统围绕测评、推荐、聊天、数据可视化和新闻栏目形成完整闭环。' },
    { value: '测评-干预-复测', desc: '提交测评后自动生成干预计划，并可在结果页标记完成。' },
    { value: 'TechCrunch RSS', desc: '新闻栏目默认接入 TechCrunch RSS，并在失败时回退本地示例。' },
    { value: 'Admin Dashboard', desc: '后台可查看用户、测评、聊天、推荐、干预完成率等数据。' }
  ]
})

const entryCards = [
  { icon: '1', title: 'FOMO 测试', desc: '填写行为与画像项，生成焦虑评分和干预计划。', path: '/fomo' },
  { icon: '2', title: '结果分析', desc: '查看最新测评、干预建议和复测对比。', path: '/result' },
  { icon: '3', title: '学习推荐', desc: '依据焦虑等级和学习目标生成学习路径。', path: '/recommendation' },
  { icon: '4', title: 'AI 聊天', desc: '支持上下文对话的学习助手。', path: '/chat' },
  { icon: '5', title: '数据可视化', desc: '查看焦虑分布、趋势和关联分析。', path: '/visualization' },
  { icon: '6', title: '新闻栏目', desc: '浏览 AI 与科技领域新闻图文聚合。', path: '/news' }
]

const newsPreview = ref(createMockNewsFeed('ai', 'OpenAI', 3).articles)

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
.entry-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.entry-card {
  cursor: pointer;
  border-radius: 12px;
  border: 1px solid #e0e9f7;
  background: linear-gradient(180deg, rgba(255,255,255,0.98), rgba(246,250,255,0.98));
  box-shadow: 0 12px 28px rgba(37, 99, 235, 0.06);
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
  background: #e9f1ff;
  color: #2563eb;
  font-weight: 700;
}

.entry-title {
  margin-top: 10px;
  font-size: 15px;
  font-weight: 700;
  color: #18324e;
}

.entry-desc {
  margin-top: 8px;
  color: #718297;
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
  border: 1px solid #e0e9f7;
  border-radius: 12px;
  background: #fff;
}

.news-preview-item img {
  width: 130px;
  height: 86px;
  object-fit: cover;
  border-radius: 10px;
}

.news-preview-title {
  font-weight: 700;
  color: #18324e;
  line-height: 1.5;
}

.news-preview-desc {
  margin-top: 6px;
  color: #718297;
  line-height: 1.6;
  font-size: 12px;
}

@media (max-width: 960px) {
  .entry-grid {
    grid-template-columns: 1fr;
  }

  .news-preview-item {
    grid-template-columns: 1fr;
  }

  .news-preview-item img {
    width: 100%;
  }
}
</style>
