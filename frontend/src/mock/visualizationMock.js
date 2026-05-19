import { scorePreview } from '../utils/fomoScore'

function range(n, fn) {
  return Array.from({ length: n }, (_, i) => fn(i))
}

export function createVisualizationMock(payload = {}) {
  const anxietyDistribution = payload.anxietyDistribution?.length
    ? payload.anxietyDistribution
    : [
        { label: '正常', value: 12 },
        { label: '轻度焦虑', value: 18 },
        { label: '中度焦虑', value: 8 },
        { label: '高度焦虑', value: 4 }
      ]

  const learningTimeTrend = payload.learningTimeTrend?.length
    ? payload.learningTimeTrend
    : range(7, (i) => ({
        label: `D${i + 1}`,
        value: [42, 45, 48, 53, 57, 60, 64][i]
      }))

  const aiUsageFrequency = payload.aiUsageFrequency?.length
    ? payload.aiUsageFrequency
    : [
        { label: '低', value: 6 },
        { label: '中', value: 16 },
        { label: '高', value: 11 },
        { label: '很高', value: 5 }
      ]

  const learningDirectionRadar = payload.learningDirectionRadar?.length
    ? payload.learningDirectionRadar
    : {
        indicators: [
          { name: 'Python', max: 100 },
          { name: '数据分析', max: 100 },
          { name: '前端开发', max: 100 },
          { name: '算法基础', max: 100 },
          { name: 'AI工具', max: 100 },
          { name: '项目实战', max: 100 }
        ],
        series: [
          {
            name: '计算机科学',
            value: [82, 76, 68, 70, 73, 81]
          },
          {
            name: 'AI方向',
            value: [92, 89, 58, 77, 94, 88]
          }
        ]
      }

  const anxietyScoreRange = payload.anxietyScoreRange?.length
    ? payload.anxietyScoreRange
    : [
        { label: '0-30', value: 9 },
        { label: '31-60', value: 17 },
        { label: '61-80', value: 10 },
        { label: '81-100', value: 3 }
      ]

  const scatter = payload.scatter?.length
    ? payload.scatter
    : [
        { name: '样本A', value: [62, 38, 55] },
        { name: '样本B', value: [55, 48, 63] },
        { name: '样本C', value: [78, 22, 34] },
        { name: '样本D', value: [43, 72, 79] },
        { name: '样本E', value: [68, 35, 46] },
        { name: '样本F', value: [88, 18, 21] }
      ]

  const recommendationPie = payload.recommendationPie?.length
    ? payload.recommendationPie
    : [
        { label: 'AI路线', value: 36 },
        { label: '聚焦策略', value: 28 },
        { label: '基础路径', value: 18 },
        { label: '均衡路径', value: 10 },
        { label: 'default', value: 8 }
      ]

  const preview = scorePreview({
    shortVideoMinutes: 95,
    learningSwitchTimes: 4,
    anxietyFrequency: 4,
    aiUsageTimes: 6
  })

  return {
    kpis: payload.kpis?.length
      ? payload.kpis
      : [
          { label: '测评人数', value: 1286, suffix: '人', delta: 12 },
          { label: '平均总分', value: 54.6, suffix: '分', delta: 3.2 },
          { label: '高焦虑占比', value: 18.4, suffix: '%', delta: -1.1 },
          { label: 'AI使用率', value: 72.9, suffix: '%', delta: 5.7 }
        ],
    anxietyDistribution,
    learningTimeTrend,
    aiUsageFrequency,
    learningDirectionRadar,
    anxietyScoreRange,
    scatter,
    recommendationPie,
    preview
  }
}
