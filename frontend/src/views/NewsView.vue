<template>
  <div class="news-page">
    <div class="news-hero">
      <div>
        <div class="page-title news-title">AI 与科技新闻栏目</div>
        <div class="news-subtitle">
          聚合 TechCrunch RSS 中的 AI 最新进展、科技动态和研究趋势，支持图文浏览、分类切换与关键词搜索。
        </div>
      </div>
      <div class="news-hero-actions">
        <el-tag effect="plain" type="info">图文资讯</el-tag>
        <el-tag effect="plain" type="success">实时聚合</el-tag>
        <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadNews">刷新新闻</el-button>
      </div>
    </div>

    <div class="news-toolbar">
      <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
        <el-tab-pane label="AI 进展" name="ai" />
        <el-tab-pane label="科技新闻" name="tech" />
        <el-tab-pane label="研究趋势" name="research" />
      </el-tabs>

      <div class="news-search">
        <el-input
          v-model="keyword"
          clearable
          placeholder="输入关键词搜索，如：OpenAI、芯片、机器人"
          style="width: 360px"
          @keyup.enter="loadNews"
        />
        <el-button @click="keyword = ''; loadNews()">重置</el-button>
      </div>
    </div>

    <el-skeleton :loading="loading && !ready" animated :rows="8">
      <template #default>
        <div class="news-stats">
          <el-card class="news-stat" shadow="never">
            <div class="stat-label">新闻总数</div>
            <div class="stat-value">{{ feed.total }}</div>
          </el-card>
          <el-card class="news-stat" shadow="never">
            <div class="stat-label">当前分类</div>
            <div class="stat-value">{{ categoryText }}</div>
          </el-card>
          <el-card class="news-stat" shadow="never">
            <div class="stat-label">数据来源</div>
            <div class="stat-value">{{ feed.mock ? '本地回退' : feed.sourceName }}</div>
          </el-card>
          <el-card class="news-stat" shadow="never">
            <div class="stat-label">更新时间</div>
            <div class="stat-value">{{ formatDate(feed.fetchedAt) }}</div>
          </el-card>
        </div>

        <section v-if="featuredArticle" class="news-featured">
          <div class="featured-media">
            <img :src="featuredArticle.urlToImage" :alt="featuredArticle.title" />
          </div>
          <div class="featured-body">
            <el-tag effect="plain" type="primary">{{ featuredArticle.sourceName }}</el-tag>
            <h2>{{ featuredArticle.title }}</h2>
            <p>{{ featuredArticle.description }}</p>
            <div class="featured-meta">
              <span>{{ formatDate(featuredArticle.publishedAt) }}</span>
              <span>{{ featuredArticle.categoryText }}</span>
            </div>
            <div class="featured-actions">
              <el-button type="primary" @click="openArticle(featuredArticle.url)">阅读原文</el-button>
              <el-button @click="copyLink(featuredArticle.url)">复制链接</el-button>
            </div>
          </div>
        </section>

        <section class="news-grid">
          <el-card v-for="article in articles" :key="article.url + article.title" class="news-card" shadow="never">
            <div class="news-card__image">
              <img :src="article.urlToImage" :alt="article.title" />
            </div>
            <div class="news-card__content">
              <div class="news-card__meta">
                <el-tag size="small" effect="plain">{{ article.sourceName }}</el-tag>
                <span>{{ formatDate(article.publishedAt) }}</span>
              </div>
              <div class="news-card__title">{{ article.title }}</div>
              <div class="news-card__desc">{{ article.description }}</div>
              <div class="news-card__footer">
                <el-button size="small" text type="primary" @click="openArticle(article.url)">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </section>

        <el-empty v-if="!articles.length" description="暂无新闻数据，已自动切换到本地回退源" />
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { fetchNewsApi } from '../api/news'
import { createMockNewsFeed } from '../mock/newsMock'

const loading = ref(false)
const ready = ref(false)
const activeCategory = ref('ai')
const keyword = ref('')
const feed = ref({
  ...createMockNewsFeed('ai', '', 9)
})

const categoryText = computed(() => {
  return {
    ai: 'AI 进展',
    tech: '科技新闻',
    research: '研究趋势'
  }[feed.value.category] || 'AI 进展'
})

const articles = computed(() =>
  (feed.value.articles || []).map((item) => ({
    ...item,
    categoryText: categoryText.value
  }))
)

const featuredArticle = computed(() => articles.value[0] || null)

async function loadNews() {
  loading.value = true
  try {
    const res = await fetchNewsApi({
      category: activeCategory.value,
      keyword: keyword.value,
      pageSize: 9
    })
    const payload = res.data
    if (payload && Array.isArray(payload.articles) && payload.articles.length > 0) {
      feed.value = payload
    } else {
      feed.value = createMockNewsFeed(activeCategory.value, keyword.value, 9)
    }
    ready.value = true
  } catch (error) {
    feed.value = createMockNewsFeed(activeCategory.value, keyword.value, 9)
    ready.value = true
    console.warn('News API unavailable, using local mock feed.', error)
  } finally {
    loading.value = false
  }
}

function handleCategoryChange() {
  loadNews()
}

function formatDate(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN', { hour12: false })
}

function openArticle(url) {
  if (!url) return
  window.open(url, '_blank', 'noopener,noreferrer')
}

async function copyLink(url) {
  if (!url) return
  try {
    await navigator.clipboard.writeText(url)
    ElMessage.success('链接已复制')
  } catch {
    ElMessage.warning('复制失败，请手动复制')
  }
}

onMounted(loadNews)
</script>

<style scoped>
.news-page {
  max-width: 1600px;
  margin: 0 auto;
  padding: 4px 0 24px;
  color: #284062;
}

.news-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  padding: 18px 20px;
  margin-bottom: 18px;
  border-radius: 14px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.96), rgba(239, 244, 255, 0.98)),
    radial-gradient(circle at top right, rgba(112, 150, 255, 0.1), transparent 40%);
  border: 1px solid rgba(114, 145, 214, 0.14);
  box-shadow: 0 20px 50px rgba(83, 104, 145, 0.12);
}

.news-title {
  margin: 0;
  color: #213a62;
}

.news-subtitle {
  max-width: 920px;
  margin-top: 10px;
  color: #647a9d;
  line-height: 1.7;
}

.news-hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.news-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 16px;
  padding: 0 4px;
}

.news-search {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.news-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.news-stat {
  border-radius: 10px;
  border: 1px solid rgba(114, 145, 214, 0.14);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 250, 255, 0.98));
  box-shadow: 0 10px 24px rgba(67, 97, 139, 0.08);
}

.stat-label {
  color: #6d82a2;
  font-size: 12px;
}

.stat-value {
  margin-top: 10px;
  font-size: 20px;
  font-weight: 700;
  color: #1f3554;
  word-break: break-all;
}

.news-featured {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  gap: 16px;
  margin-bottom: 18px;
  padding: 16px;
  border-radius: 14px;
  border: 1px solid rgba(114, 145, 214, 0.14);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 250, 255, 0.98));
  box-shadow: 0 10px 24px rgba(67, 97, 139, 0.08);
}

.featured-media img {
  width: 100%;
  height: 100%;
  min-height: 280px;
  object-fit: cover;
  border-radius: 12px;
}

.featured-body {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.featured-body h2 {
  margin: 12px 0 10px;
  font-size: 24px;
  color: #20365a;
}

.featured-body p {
  margin: 0;
  color: #61748f;
  line-height: 1.8;
}

.featured-meta {
  display: flex;
  gap: 14px;
  margin: 16px 0;
  color: #7a8ca8;
  font-size: 13px;
}

.featured-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.news-card {
  overflow: hidden;
  border-radius: 12px;
  border: 1px solid rgba(114, 145, 214, 0.14);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 250, 255, 0.98));
  box-shadow: 0 10px 24px rgba(67, 97, 139, 0.08);
}

.news-card__image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 10px;
}

.news-card__content {
  padding-top: 12px;
}

.news-card__meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  color: #7a8ca8;
  font-size: 12px;
}

.news-card__title {
  margin-top: 10px;
  font-size: 16px;
  font-weight: 700;
  color: #20365a;
  line-height: 1.5;
  min-height: 48px;
}

.news-card__desc {
  margin-top: 8px;
  color: #61748f;
  line-height: 1.7;
  min-height: 66px;
}

.news-card__footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

@media (max-width: 1360px) {
  .news-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .news-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .news-hero,
  .news-toolbar,
  .news-featured {
    grid-template-columns: 1fr;
    flex-direction: column;
    align-items: flex-start;
  }

  .news-stats,
  .news-grid {
    grid-template-columns: 1fr;
  }

  .news-search {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
