<template>
  <div class="visual-page">
    <div class="visual-hero">
      <div>
        <div class="page-title visual-title">数据可视化平台</div>
        <div class="visual-subtitle">
          面向 AI 时代大学生信息焦虑分析与个性化学习推荐，支持交互图表、趋势分析与详情联动。
        </div>
      </div>
      <div class="visual-hero-actions">
        <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadData">刷新数据</el-button>
        <el-button :icon="DataAnalysis" @click="focusInsight('platform')">平台总览</el-button>
      </div>
    </div>

    <el-skeleton :loading="loading && !ready" animated :rows="8">
      <template #default>
        <section class="visual-section">
          <div class="section-head">
            <div>
              <div class="section-title">核心指标</div>
              <div class="section-desc">实时展示当前测评与推荐概况，支持鼠标悬浮与数值滚动。</div>
            </div>
          </div>
          <div class="kpi-grid">
            <CountUpCard
              v-for="card in visualization.kpis"
              :key="card.label"
              :label="card.label"
              :value="card.value"
              :suffix="card.suffix"
              :delta="card.delta"
              :meta="card.meta"
            />
          </div>
        </section>

        <section class="visual-section">
          <div class="section-head">
            <div>
              <div class="section-title">核心图表区</div>
              <div class="section-desc">焦虑分布、学习趋势、AI 使用与学习画像联动分析。</div>
            </div>
            <el-tag effect="plain" type="info">支持 tooltip / legend / click / dataZoom</el-tag>
          </div>

          <div class="chart-grid chart-grid--primary">
            <ChartPanel
              title="FOMO焦虑等级分布环形图"
              subtitle="不同焦虑等级占比"
              :option="chartOptions.donut"
              @chart-click="handleChartClick('FOMO焦虑等级分布环形图', $event)"
            />
            <ChartPanel
              title="学习时间趋势折线图"
              subtitle="最近统计周期的学习趋势变化"
              :option="chartOptions.line"
              @chart-click="handleChartClick('学习时间趋势折线图', $event)"
            />
            <ChartPanel
              title="AI工具使用频率柱状图"
              subtitle="AI 工具使用强度分布"
              :option="chartOptions.bar"
              @chart-click="handleChartClick('AI工具使用频率柱状图', $event)"
            />
            <ChartPanel
              title="学习方向分布雷达图"
              subtitle="不同学习方向能力画像"
              :option="chartOptions.radar"
              @chart-click="handleChartClick('学习方向分布雷达图', $event)"
            />
          </div>
        </section>

        <section class="visual-section">
          <div class="section-head">
            <div>
              <div class="section-title">底部趋势分析区</div>
              <div class="section-desc">用于答辩展示的关联分析与推荐结构图。</div>
            </div>
            <el-switch v-model="compactMode" inline-prompt active-text="紧凑" inactive-text="标准" />
          </div>

          <div class="chart-grid chart-grid--secondary" :class="{ compact: compactMode }">
            <ChartPanel
              title="用户焦虑评分区间分布柱状图"
              subtitle="不同分数区间的人数分布"
              :option="chartOptions.rangeBar"
              @chart-click="handleChartClick('用户焦虑评分区间分布柱状图', $event)"
            />
            <ChartPanel
              title="学习效率与焦虑程度关系散点图"
              subtitle="效率与焦虑的相关性观察"
              :option="chartOptions.scatter"
              @chart-click="handleChartClick('学习效率与焦虑程度关系散点图', $event)"
            />
            <ChartPanel
              title="推荐类型占比饼图"
              subtitle="当前用户或样本推荐类型统计"
              :option="chartOptions.recommendationPie"
              @chart-click="handleChartClick('推荐类型占比饼图', $event)"
            />
            <el-card class="insight-card" shadow="never">
              <div class="insight-card__head">
                <div class="section-title">点击详情</div>
                <el-tag type="success" effect="plain">交互联动</el-tag>
              </div>
              <div class="insight-main">
                <div class="insight-name">{{ activeInsight.title }}</div>
                <div class="insight-value">{{ activeInsight.name }}</div>
              </div>
              <div class="insight-list">
                <div class="insight-row">
                  <span>图表系列</span>
                  <span>{{ activeInsight.seriesName }}</span>
                </div>
                <div class="insight-row">
                  <span>数据值</span>
                  <span>{{ activeInsight.valueText }}</span>
                </div>
                <div class="insight-row">
                  <span>建议</span>
                  <span>{{ activeInsight.tips }}</span>
                </div>
              </div>
            </el-card>
          </div>
        </section>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { DataAnalysis, Refresh } from '@element-plus/icons-vue'
import CountUpCard from '../components/visualization/CountUpCard.vue'
import ChartPanel from '../components/visualization/ChartPanel.vue'
import { getVisualizationData } from '../api/visualization'
import {
  createBarOption,
  createDonutOption,
  createLineOption,
  createPieOption,
  createRadarOption,
  createRangeBarOption,
  createScatterOption
} from '../visualization/chartOptions'

const loading = ref(false)
const ready = ref(false)
const compactMode = ref(false)
const visualization = reactive({
  kpis: [],
  anxietyDistribution: [],
  learningTimeTrend: [],
  aiUsageFrequency: [],
  learningDirectionRadar: { indicators: [], series: [] },
  anxietyScoreRange: [],
  scatter: [],
  recommendationPie: [],
  preview: {}
})

const activeInsight = reactive({
  title: '平台总览',
  name: '请选择任一图表数据点查看详情',
  seriesName: '系统默认',
  valueText: '-',
  tips: '点击图表中的柱、点、扇区或折线点，会在这里展示对应信息。'
})

const chartOptions = computed(() => ({
  donut: createDonutOption(
    visualization.anxietyDistribution.map((item) => ({ name: item.label, value: item.value }))
  ),
  line: createLineOption(visualization.learningTimeTrend),
  bar: createBarOption(visualization.aiUsageFrequency, 'AI工具使用频率'),
  radar: createRadarOption(visualization.learningDirectionRadar),
  rangeBar: createRangeBarOption(visualization.anxietyScoreRange),
  scatter: createScatterOption(visualization.scatter),
  recommendationPie: createPieOption(
    visualization.recommendationPie.map((item) => ({ name: item.label, value: item.value }))
  )
}))

function formatValue(value) {
  if (Array.isArray(value)) return value.join(' / ')
  if (value === null || value === undefined) return '-'
  if (typeof value === 'object') return JSON.stringify(value)
  return String(value)
}

function focusInsight(title) {
  activeInsight.title = title === 'platform' ? '平台总览' : title
  activeInsight.name = title === 'platform' ? '整体数据概览已激活' : title
  activeInsight.seriesName = '系统'
  activeInsight.valueText = '-'
  activeInsight.tips = '建议通过图表点击继续查看各个维度的细节。'
}

function handleChartClick(title, payload) {
  activeInsight.title = title
  activeInsight.name = payload?.name || '未命中数据点'
  activeInsight.seriesName = payload?.seriesName || '-'
  activeInsight.valueText = formatValue(payload?.value)

  if (title.includes('散点')) {
    activeInsight.tips = '散点图用于观察学习效率与焦虑程度的相关性，点越靠右表示效率越高。'
  } else if (title.includes('环形图')) {
    activeInsight.tips = '环形图用于观察焦虑结构，适合展示整体分布比例。'
  } else if (title.includes('推荐类型')) {
    activeInsight.tips = '推荐类型占比可用于判断当前系统的推荐偏好。'
  } else {
    activeInsight.tips = '点击后可在此查看系列名称与数值明细。'
  }
}

async function loadData() {
  loading.value = true
  try {
    const data = await getVisualizationData()
    visualization.kpis = data.kpis || []
    visualization.anxietyDistribution = data.anxietyDistribution || []
    visualization.learningTimeTrend = data.learningTimeTrend || []
    visualization.aiUsageFrequency = data.aiUsageFrequency || []
    visualization.learningDirectionRadar = data.learningDirectionRadar || { indicators: [], series: [] }
    visualization.anxietyScoreRange = data.anxietyScoreRange || []
    visualization.scatter = data.scatter || []
    visualization.recommendationPie = data.recommendationPie || []
    visualization.preview = data.preview || {}
    ready.value = true
    focusInsight('platform')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.visual-page {
  max-width: 1600px;
  margin: 0 auto;
  padding: 4px 0 20px;
  color: #284062;
}

.visual-hero {
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

.visual-title {
  margin: 0;
  color: #213a62;
}

.visual-subtitle {
  max-width: 900px;
  margin-top: 10px;
  color: #647a9d;
  line-height: 1.7;
}

.visual-hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.visual-section {
  margin-bottom: 18px;
  padding: 18px;
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(244, 247, 255, 0.98)),
    rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(114, 145, 214, 0.14);
  box-shadow: 0 16px 36px rgba(83, 104, 145, 0.1);
}

.section-head {
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

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.chart-grid {
  display: grid;
  gap: 14px;
}

.chart-grid--primary {
  grid-template-columns: repeat(12, minmax(0, 1fr));
}

.chart-grid--secondary {
  grid-template-columns: repeat(12, minmax(0, 1fr));
}

.chart-grid--primary :deep(.chart-panel),
.chart-grid--secondary :deep(.chart-panel) {
  min-height: 100%;
}

.chart-grid--primary :deep(.chart-panel:nth-child(1)) {
  grid-column: span 4;
}

.chart-grid--primary :deep(.chart-panel:nth-child(2)) {
  grid-column: span 8;
}

.chart-grid--primary :deep(.chart-panel:nth-child(3)) {
  grid-column: span 6;
}

.chart-grid--primary :deep(.chart-panel:nth-child(4)) {
  grid-column: span 6;
}

.chart-grid--secondary :deep(.chart-panel:nth-child(1)) {
  grid-column: span 4;
}

.chart-grid--secondary :deep(.chart-panel:nth-child(2)) {
  grid-column: span 4;
}

.chart-grid--secondary :deep(.chart-panel:nth-child(3)) {
  grid-column: span 4;
}

.insight-card {
  grid-column: span 12;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(242, 246, 255, 0.98));
  border: 1px solid rgba(114, 145, 214, 0.14);
  color: #234063;
  border-radius: 10px;
}

.insight-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 18px;
}

.insight-main {
  padding: 16px;
  border-radius: 10px;
  background: rgba(112, 149, 255, 0.05);
  border: 1px solid rgba(114, 145, 214, 0.12);
}

.insight-name {
  color: #6c82a5;
  font-size: 13px;
}

.insight-value {
  margin-top: 8px;
  font-size: 20px;
  font-weight: 700;
  color: #223a60;
  word-break: break-word;
}

.insight-list {
  display: grid;
  gap: 10px;
  margin-top: 14px;
}

.insight-row {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding: 12px 14px;
  border-radius: 10px;
  background: rgba(112, 149, 255, 0.04);
  border: 1px solid rgba(114, 145, 214, 0.1);
  color: #34506e;
}

.compact :deep(.chart-panel__body) {
  height: 320px;
}

@media (max-width: 1280px) {
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .chart-grid--primary :deep(.chart-panel:nth-child(1)),
  .chart-grid--primary :deep(.chart-panel:nth-child(2)),
  .chart-grid--primary :deep(.chart-panel:nth-child(3)),
  .chart-grid--primary :deep(.chart-panel:nth-child(4)),
  .chart-grid--secondary :deep(.chart-panel:nth-child(1)),
  .chart-grid--secondary :deep(.chart-panel:nth-child(2)),
  .chart-grid--secondary :deep(.chart-panel:nth-child(3)) {
    grid-column: span 12;
  }
}

@media (max-width: 900px) {
  .visual-hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .kpi-grid {
    grid-template-columns: 1fr;
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
