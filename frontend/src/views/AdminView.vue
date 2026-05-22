<template>
  <div class="admin-page">
    <div class="admin-hero">
      <div>
        <div class="page-title admin-title">后台管理中心</div>
        <div class="admin-subtitle">
          统一查看用户、FOMO 测评、AI 聊天和推荐数据，支持运营式总览与内容管理。
        </div>
      </div>
      <div class="admin-hero-actions">
        <el-tag effect="plain" type="info">账号：admin</el-tag>
        <el-tag effect="plain" type="success">密码：admin123</el-tag>
        <el-button type="primary" :icon="Refresh" :loading="loading.dashboard" @click="reloadAll">刷新后台</el-button>
      </div>
    </div>

    <el-skeleton :loading="loading.dashboard && !ready" animated :rows="8">
      <template #default>
        <section class="admin-section">
          <div class="section-head">
            <div>
              <div class="section-title">平台总览</div>
              <div class="section-desc">快速查看注册、测评、聊天和推荐规模。</div>
            </div>
          </div>
          <div class="stat-grid">
            <CountUpCard label="总用户数" :value="dashboard.summary.totalUsers" suffix="人" :meta="`高焦虑 ${dashboard.summary.highAnxietyCount} 人`" />
            <CountUpCard label="FOMO测评数" :value="dashboard.summary.totalFomoTests" suffix="次" :meta="`平均分 ${dashboard.summary.averageScore.toFixed(1)}`" />
            <CountUpCard label="AI聊天数" :value="dashboard.summary.totalAiChats" suffix="条" meta="累计问答记录" />
            <CountUpCard label="推荐总数" :value="dashboard.summary.totalRecommendations" suffix="条" meta="系统生成与管理内容" />
            <CountUpCard
              label="干预完成率"
              :value="dashboard.summary.interventionCompletionRate"
              suffix="%"
              :meta="`已完成 ${dashboard.summary.interventionCompletedCount} / ${dashboard.summary.interventionTotalCount}`"
            />
          </div>
        </section>

        <section class="admin-section">
          <div class="section-head">
            <div>
              <div class="section-title">运营图表</div>
              <div class="section-desc">围绕用户增长、焦虑结构、AI 使用和专业分布进行可视化。</div>
            </div>
          </div>

          <div class="chart-grid">
            <ChartPanel title="FOMO焦虑等级分布环形图" subtitle="整体焦虑结构" :option="chartOptions.anxiety" @chart-click="handleChartClick('anxiety', $event)" />
            <ChartPanel title="注册趋势折线图" subtitle="近周期用户注册变化" :option="chartOptions.registration" @chart-click="handleChartClick('registration', $event)" />
            <ChartPanel title="AI工具使用频率柱状图" subtitle="测评样本中的 AI 使用频率" :option="chartOptions.aiUsage" @chart-click="handleChartClick('aiUsage', $event)" />
            <ChartPanel title="专业分布柱状图" subtitle="用户专业聚合排行" :option="chartOptions.major" @chart-click="handleChartClick('major', $event)" />
            <ChartPanel title="推荐类型占比饼图" subtitle="推荐内容类型构成" :option="chartOptions.recommendationType" @chart-click="handleChartClick('recommendationType', $event)" />
          </div>
        </section>

        <section class="admin-section">
          <div class="section-head">
            <div>
              <div class="section-title">后台详情</div>
              <div class="section-desc">点击图表可查看当前点位信息，表格用于执行管理操作。</div>
            </div>
            <el-switch v-model="denseMode" inline-prompt active-text="紧凑" inactive-text="标准" />
          </div>

          <el-tabs v-model="activeTab" class="admin-tabs">
            <el-tab-pane label="概览" name="overview">
              <div class="detail-grid">
                <el-card class="detail-card" shadow="never">
                  <div class="detail-head">
                    <div class="section-title">最近注册用户</div>
                    <el-tag effect="plain">Recent Users</el-tag>
                  </div>
                  <el-table :data="dashboard.recentUsers" size="small" border>
                    <el-table-column prop="username" label="用户名" width="130" />
                    <el-table-column prop="major" label="专业" min-width="160" />
                    <el-table-column prop="learningGoal" label="学习目标" min-width="200" show-overflow-tooltip />
                    <el-table-column prop="createdTime" label="注册时间" width="180">
                      <template #default="{ row }">{{ formatDate(row.createdTime) }}</template>
                    </el-table-column>
                  </el-table>
                </el-card>

                <el-card class="detail-card" shadow="never">
                  <div class="detail-head">
                    <div class="section-title">最近测评记录</div>
                    <el-tag effect="plain" type="warning">Recent Tests</el-tag>
                  </div>
                  <el-table :data="dashboard.recentFomoTests" size="small" border>
                    <el-table-column prop="username" label="用户" width="110" />
                    <el-table-column prop="totalScore" label="总分" width="90" />
                    <el-table-column prop="anxietyLevel" label="等级" width="110" />
                    <el-table-column prop="aiUsageTimes" label="AI使用" width="110" />
                    <el-table-column prop="createdTime" label="提交时间" width="180">
                      <template #default="{ row }">{{ formatDate(row.createdTime) }}</template>
                    </el-table-column>
                  </el-table>
                </el-card>

                <el-card class="detail-card" shadow="never">
                  <div class="detail-head">
                    <div class="section-title">最近聊天记录</div>
                    <el-tag effect="plain" type="success">Recent Chats</el-tag>
                  </div>
                  <el-table :data="dashboard.recentChats" size="small" border>
                    <el-table-column prop="username" label="用户" width="110" />
                    <el-table-column prop="question" label="问题" min-width="220" show-overflow-tooltip />
                    <el-table-column prop="answer" label="回复" min-width="260" show-overflow-tooltip />
                    <el-table-column prop="createdTime" label="时间" width="180">
                      <template #default="{ row }">{{ formatDate(row.createdTime) }}</template>
                    </el-table-column>
                  </el-table>
                </el-card>
              </div>
            </el-tab-pane>

            <el-tab-pane label="用户管理" name="users">
              <el-card class="table-card" shadow="never">
                <div class="table-toolbar">
                  <div>
                    <div class="section-title">用户列表</div>
                    <div class="section-desc">查看全部用户并支持删除操作。</div>
                  </div>
                  <el-input v-model="userKeyword" placeholder="搜索用户名 / 专业" clearable style="width: 280px" />
                </div>
                <el-table :data="displayUsers" border :size="denseMode ? 'small' : 'default'" style="width: 100%">
                  <el-table-column prop="id" label="ID" width="80" />
                  <el-table-column prop="username" label="用户名" width="140" />
                  <el-table-column prop="major" label="专业" min-width="160" />
                  <el-table-column prop="learningGoal" label="学习目标" min-width="220" show-overflow-tooltip />
                  <el-table-column prop="createdTime" label="注册时间" width="180">
                    <template #default="{ row }">{{ formatDate(row.createdTime) }}</template>
                  </el-table-column>
                  <el-table-column label="操作" width="110">
                    <template #default="{ row }">
                      <el-button type="danger" link @click="removeUser(row.id)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div class="pager">
                  <el-pagination
                    v-model:current-page="userPage.current"
                    v-model:page-size="userPage.size"
                    :total="userPage.total"
                    layout="total, prev, pager, next, sizes"
                    :page-sizes="[10, 20, 50]"
                    @current-change="loadUsers"
                    @size-change="loadUsers(1)"
                  />
                </div>
              </el-card>
            </el-tab-pane>

            <el-tab-pane label="FOMO记录" name="tests">
              <el-card class="table-card" shadow="never">
                <div class="table-toolbar">
                  <div>
                    <div class="section-title">测评记录</div>
                    <div class="section-desc">查看所有测评记录和评分结果。</div>
                  </div>
                </div>
                <el-table :data="fomoTests" border :size="denseMode ? 'small' : 'default'" style="width: 100%">
                  <el-table-column prop="id" label="ID" width="80" />
                  <el-table-column prop="username" label="用户" width="130" />
                  <el-table-column prop="totalScore" label="总分" width="90" />
                  <el-table-column prop="anxietyLevel" label="等级" width="110" />
                  <el-table-column prop="aiUsageTimes" label="AI使用频率" width="110" />
                  <el-table-column prop="createdTime" label="时间" width="180">
                    <template #default="{ row }">{{ formatDate(row.createdTime) }}</template>
                  </el-table-column>
                </el-table>
                <div class="pager">
                  <el-pagination
                    v-model:current-page="testPage.current"
                    v-model:page-size="testPage.size"
                    :total="testPage.total"
                    layout="total, prev, pager, next, sizes"
                    :page-sizes="[10, 20, 50]"
                    @current-change="loadFomoTests"
                    @size-change="loadFomoTests(1)"
                  />
                </div>
              </el-card>
            </el-tab-pane>

            <el-tab-pane label="聊天记录" name="chats">
              <el-card class="table-card" shadow="never">
                <div class="table-toolbar">
                  <div>
                    <div class="section-title">AI聊天记录</div>
                    <div class="section-desc">查看用户与 AI 的问答内容。</div>
                  </div>
                </div>
                <el-table :data="chats" border :size="denseMode ? 'small' : 'default'" style="width: 100%">
                  <el-table-column prop="id" label="ID" width="80" />
                  <el-table-column prop="username" label="用户" width="130" />
                  <el-table-column prop="question" label="问题" min-width="240" show-overflow-tooltip />
                  <el-table-column prop="answer" label="回复" min-width="260" show-overflow-tooltip />
                  <el-table-column prop="createdTime" label="时间" width="180">
                    <template #default="{ row }">{{ formatDate(row.createdTime) }}</template>
                  </el-table-column>
                </el-table>
                <div class="pager">
                  <el-pagination
                    v-model:current-page="chatPage.current"
                    v-model:page-size="chatPage.size"
                    :total="chatPage.total"
                    layout="total, prev, pager, next, sizes"
                    :page-sizes="[10, 20, 50]"
                    @current-change="loadChats"
                    @size-change="loadChats(1)"
                  />
                </div>
              </el-card>
            </el-tab-pane>

            <el-tab-pane label="推荐管理" name="recs">
              <el-card class="table-card" shadow="never">
                <div class="table-toolbar">
                  <div>
                    <div class="section-title">推荐内容管理</div>
                    <div class="section-desc">可新增、编辑和删除推荐内容。</div>
                  </div>
                </div>
                <el-form :model="form" class="rec-form" label-width="78px">
                  <el-row :gutter="12">
                    <el-col :xs="24" :md="6">
                      <el-form-item label="用户ID">
                        <el-input v-model="form.userId" />
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :md="6">
                      <el-form-item label="类型">
                        <el-input v-model="form.type" />
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :md="10">
                      <el-form-item label="内容">
                        <el-input v-model="form.content" />
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :md="2">
                      <el-form-item label-width="0">
                        <el-button type="primary" @click="saveRec">保存</el-button>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
                <el-table :data="recommendations" border :size="denseMode ? 'small' : 'default'" style="width: 100%">
                  <el-table-column prop="id" label="ID" width="80" />
                  <el-table-column prop="username" label="用户" width="130" />
                  <el-table-column prop="type" label="类型" width="130" />
                  <el-table-column prop="content" label="内容" min-width="280" show-overflow-tooltip />
                  <el-table-column label="操作" width="110">
                    <template #default="{ row }">
                      <el-button type="danger" link @click="removeRec(row.id)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div class="pager">
                  <el-pagination
                    v-model:current-page="recPage.current"
                    v-model:page-size="recPage.size"
                    :total="recPage.total"
                    layout="total, prev, pager, next, sizes"
                    :page-sizes="[10, 20, 50]"
                    @current-change="loadRecommendations"
                    @size-change="loadRecommendations(1)"
                  />
                </div>
              </el-card>
            </el-tab-pane>
          </el-tabs>
        </section>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import CountUpCard from '../components/visualization/CountUpCard.vue'
import ChartPanel from '../components/visualization/ChartPanel.vue'
import {
  adminChatsApi,
  adminDashboardApi,
  adminFomoTestsApi,
  adminRecommendationsApi,
  adminUsersApi,
  deleteAdminRecommendationApi,
  deleteAdminUserApi,
  saveAdminRecommendationApi
} from '../api/admin'
import { createBarOption, createDonutOption, createHorizontalBarOption, createLineOption, createPieOption } from '../admin/chartOptions'

const activeTab = ref('overview')
const denseMode = ref(false)
const ready = ref(false)

const loading = reactive({
  dashboard: false,
  users: false,
  tests: false,
  chats: false,
  recommendations: false
})

const dashboard = reactive({
  summary: {
    totalUsers: 0,
    totalFomoTests: 0,
    totalAiChats: 0,
    totalRecommendations: 0,
    highAnxietyCount: 0,
    averageScore: 0
  },
  anxietyDistribution: [],
  registrationTrend: [],
  aiUsageFrequency: [],
  majorDistribution: [],
  recommendationTypeDistribution: [],
  recentUsers: [],
  recentFomoTests: [],
  recentChats: [],
  recentRecommendations: []
})

const users = ref([])
const fomoTests = ref([])
const chats = ref([])
const recommendations = ref([])
const userKeyword = ref('')

const userPage = reactive({ current: 1, size: 10, total: 0 })
const testPage = reactive({ current: 1, size: 10, total: 0 })
const chatPage = reactive({ current: 1, size: 10, total: 0 })
const recPage = reactive({ current: 1, size: 10, total: 0 })

const form = reactive({
  userId: '',
  type: '',
  content: ''
})

const chartOptions = computed(() => ({
  anxiety: createDonutOption(
    dashboard.anxietyDistribution.map((item) => ({ name: item.label, value: item.value })),
    'FOMO焦虑等级分布环形图'
  ),
  registration: createLineOption(dashboard.registrationTrend, '注册人数'),
  aiUsage: createBarOption(dashboard.aiUsageFrequency, '测评人数'),
  major: createHorizontalBarOption(dashboard.majorDistribution, '用户数'),
  recommendationType: createPieOption(
    dashboard.recommendationTypeDistribution.map((item) => ({ name: item.label, value: item.value })),
    '推荐类型占比饼图'
  )
}))

const displayUsers = computed(() => users.value)

let userSearchTimer
watch(userKeyword, () => {
  clearTimeout(userSearchTimer)
  userSearchTimer = setTimeout(() => {
    userPage.current = 1
    loadUsers(1)
  }, 300)
})

async function reloadAll() {
  await Promise.all([
    loadDashboard(),
    loadUsers(1),
    loadFomoTests(1),
    loadChats(1),
    loadRecommendations(1)
  ])
  ElMessage.success('后台数据已刷新')
}

function formatDate(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN', { hour12: false })
}

function syncDashboard(payload) {
  dashboard.summary = {
    ...dashboard.summary,
    ...(payload.summary || {})
  }
  dashboard.anxietyDistribution = payload.anxietyDistribution || []
  dashboard.registrationTrend = payload.registrationTrend || []
  dashboard.aiUsageFrequency = payload.aiUsageFrequency || []
  dashboard.majorDistribution = payload.majorDistribution || []
  dashboard.recommendationTypeDistribution = payload.recommendationTypeDistribution || []
  dashboard.recentUsers = payload.recentUsers || []
  dashboard.recentFomoTests = payload.recentFomoTests || []
  dashboard.recentChats = payload.recentChats || []
  dashboard.recentRecommendations = payload.recentRecommendations || []
}

async function loadDashboard() {
  loading.dashboard = true
  try {
    const res = await adminDashboardApi()
    syncDashboard(res.data || {})
    ready.value = true
  } finally {
    loading.dashboard = false
  }
}

async function loadUsers(page = userPage.current) {
  loading.users = true
  try {
    const res = await adminUsersApi(page, userPage.size, userKeyword.value)
    const data = res.data || {}
    users.value = data.records || []
    userPage.current = data.current || page
    userPage.size = data.size || userPage.size
    userPage.total = data.total || 0
  } finally {
    loading.users = false
  }
}

async function loadFomoTests(page = testPage.current) {
  loading.tests = true
  try {
    const res = await adminFomoTestsApi(page, testPage.size)
    const data = res.data || {}
    fomoTests.value = mapFomoTests(data.records || [])
    testPage.current = data.current || page
    testPage.size = data.size || testPage.size
    testPage.total = data.total || 0
  } finally {
    loading.tests = false
  }
}

async function loadChats(page = chatPage.current) {
  loading.chats = true
  try {
    const res = await adminChatsApi(page, chatPage.size)
    const data = res.data || {}
    chats.value = mapChats(data.records || [])
    chatPage.current = data.current || page
    chatPage.size = data.size || chatPage.size
    chatPage.total = data.total || 0
  } finally {
    loading.chats = false
  }
}

async function loadRecommendations(page = recPage.current) {
  loading.recommendations = true
  try {
    const res = await adminRecommendationsApi(page, recPage.size)
    const data = res.data || {}
    recommendations.value = mapRecommendations(data.records || [])
    recPage.current = data.current || page
    recPage.size = data.size || recPage.size
    recPage.total = data.total || 0
  } finally {
    loading.recommendations = false
  }
}

function mapFomoTests(records) {
  return records.map((item) => ({
    ...item,
    username: item.username || '-'
  }))
}

function mapChats(records) {
  return records.map((item) => ({
    ...item,
    username: item.username || '-'
  }))
}

function mapRecommendations(records) {
  return records.map((item) => ({
    ...item,
    username: item.username || '-'
  }))
}

async function removeUser(id) {
  await ElMessageBox.confirm('确认删除该用户吗？', '提示', { type: 'warning' })
  await deleteAdminUserApi(id)
  ElMessage.success('已删除')
  await loadUsers(userPage.current)
  await loadDashboard()
}

async function saveRec() {
  await saveAdminRecommendationApi({
    userId: Number(form.userId),
    type: form.type,
    content: form.content
  })
  ElMessage.success('已保存')
  form.userId = ''
  form.type = ''
  form.content = ''
  await loadRecommendations(recPage.current)
  await loadDashboard()
}

async function removeRec(id) {
  await ElMessageBox.confirm('确认删除该推荐内容吗？', '提示', { type: 'warning' })
  await deleteAdminRecommendationApi(id)
  ElMessage.success('已删除')
  await loadRecommendations(recPage.current)
  await loadDashboard()
}

function handleChartClick(type, payload) {
  const label = payload?.name || '-'
  const value = Array.isArray(payload?.value) ? payload.value.join(' / ') : payload?.value ?? '-'
  ElMessage.info(`${type}：${label}，数值 ${value}`)
}

onMounted(reloadAll)
</script>

<style scoped>
.admin-page {
  max-width: 1600px;
  margin: 0 auto;
  padding: 4px 0 24px;
  color: #284062;
}

.admin-hero {
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

.admin-title {
  margin: 0;
  color: #213a62;
}

.admin-subtitle {
  max-width: 920px;
  margin-top: 10px;
  color: #647a9d;
  line-height: 1.7;
}

.admin-hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.admin-section {
  margin-bottom: 18px;
  padding: 18px;
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(244, 247, 255, 0.98)),
    rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(114, 145, 214, 0.14);
  box-shadow: 0 16px 36px rgba(83, 104, 145, 0.1);
}

.section-head,
.detail-head,
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
  margin-bottom: 14px;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #233d64;
}

.section-desc {
  margin-top: 6px;
  color: #6d82a2;
  font-size: 12px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  gap: 14px;
}

.chart-grid :deep(.chart-panel:nth-child(1)) {
  grid-column: span 4;
}

.chart-grid :deep(.chart-panel:nth-child(2)) {
  grid-column: span 8;
}

.chart-grid :deep(.chart-panel:nth-child(3)) {
  grid-column: span 6;
}

.chart-grid :deep(.chart-panel:nth-child(4)) {
  grid-column: span 6;
}

.chart-grid :deep(.chart-panel:nth-child(5)) {
  grid-column: span 12;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.detail-card,
.table-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 250, 255, 0.98));
  border: 1px solid rgba(107, 168, 255, 0.14);
  border-radius: 10px;
  color: #25324a;
  box-shadow: 0 10px 24px rgba(67, 97, 139, 0.08);
}

.admin-tabs :deep(.el-tabs__item) {
  color: #61728b;
}

.admin-tabs :deep(.el-tabs__item.is-active) {
  color: #3667b0;
}

.rec-form {
  margin-bottom: 16px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 14px;
}

@media (max-width: 1360px) {
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .chart-grid :deep(.chart-panel:nth-child(1)),
  .chart-grid :deep(.chart-panel:nth-child(2)),
  .chart-grid :deep(.chart-panel:nth-child(3)),
  .chart-grid :deep(.chart-panel:nth-child(4)) {
    grid-column: span 12;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .admin-hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .stat-grid {
    grid-template-columns: 1fr;
  }

  .section-head,
  .detail-head,
  .table-toolbar {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
