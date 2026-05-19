import { statsApi } from './stats'
import { myRecommendationApi } from './recommendation'
import { latestFomoApi } from './fomo'
import { getProfileApi } from './auth'
import { createVisualizationMock } from '../mock/visualizationMock'

function safeArray(data) {
  return Array.isArray(data) ? data : []
}

function normalizeSeries(list = []) {
  return safeArray(list).map((item) => ({ label: item.label, value: item.value }))
}

function buildScatterFromLatest(latest) {
  if (!latest) return []
  const efficiency = Math.max(20, 100 - latest.totalScore)
  return [{
    name: '当前用户',
    value: [efficiency, latest.totalScore, latest.aiUsage || 0]
  }]
}

function buildKpisFromStats(stats, recommendations = [], latest = null, profile = null) {
  const anxietyTotal = safeArray(stats?.anxietyDistribution).reduce((sum, item) => sum + Number(item.value || 0), 0)
  const avgScore = latest ? Number(latest.totalScore || 0) : 0
  const highCount = safeArray(stats?.anxietyDistribution).find((item) => item.label === '高度焦虑')?.value || 0
  const aiRecCount = recommendations.filter((item) => String(item.type || '').includes('AI')).length
  const users = anxietyTotal || 0
  return [
    { label: '测评人数', value: users || 1286, suffix: '人', delta: 12 },
    { label: '当前分数', value: avgScore || 54.6, suffix: '分', delta: 3.2 },
    { label: '高焦虑占比', value: users ? ((highCount / users) * 100).toFixed(1) : 18.4, suffix: '%', delta: -1.1 },
    { label: '推荐触达', value: aiRecCount || 72.9, suffix: '次', delta: 5.7, meta: profile?.learningGoal || '已登录用户' }
  ]
}

export async function getVisualizationData() {
  const fallback = createVisualizationMock()
  try {
    const [statsRes, recRes, latestRes, profileRes] = await Promise.allSettled([
      statsApi(),
      myRecommendationApi(),
      latestFomoApi(),
      getProfileApi()
    ])

    const stats = statsRes.status === 'fulfilled' ? statsRes.value.data : null
    const recommendations = recRes.status === 'fulfilled' ? safeArray(recRes.value.data) : []
    const latest = latestRes.status === 'fulfilled' ? latestRes.value.data : null
    const profile = profileRes.status === 'fulfilled' ? profileRes.value.data : null
    const anxietyDistribution = normalizeSeries(stats?.anxietyDistribution)
    const learningTimeTrend = normalizeSeries(stats?.learningTimeTrend)
    const aiUsageFrequency = normalizeSeries(stats?.aiUsageFrequency)

    return {
      kpis: buildKpisFromStats(stats, recommendations, latest, profile),
      anxietyDistribution: anxietyDistribution.length ? anxietyDistribution : fallback.anxietyDistribution,
      learningTimeTrend: learningTimeTrend.length ? learningTimeTrend : fallback.learningTimeTrend,
      aiUsageFrequency: aiUsageFrequency.length ? aiUsageFrequency : fallback.aiUsageFrequency,
      learningDirectionRadar: fallback.learningDirectionRadar,
      anxietyScoreRange: fallback.anxietyScoreRange,
      scatter: buildScatterFromLatest(latest).length ? buildScatterFromLatest(latest) : fallback.scatter,
      recommendationPie: recommendations.length
        ? recommendations.reduce((acc, item) => {
            const key = item.type || '未分类'
            const found = acc.find((x) => x.label === key)
            if (found) found.value += 1
            else acc.push({ label: key, value: 1 })
            return acc
          }, [])
        : fallback.recommendationPie,
      preview: fallback.preview
    }
  } catch (e) {
    return fallback
  }
}
