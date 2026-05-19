export function createDonutOption(data) {
  return {
    backgroundColor: 'transparent',
    color: ['#4F8CFF', '#8A7CFF', '#5DD8C8', '#4CC9F0', '#C084FC'],
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      textStyle: { color: '#D7E6FF' }
    },
    series: [
      {
        name: 'FOMO焦虑等级',
        type: 'pie',
        radius: ['48%', '72%'],
        center: ['50%', '44%'],
        avoidLabelOverlap: true,
        label: {
          color: '#EAF2FF',
          formatter: '{b}\n{d}%'
        },
        emphasis: {
          scale: true,
          scaleSize: 10
        },
        itemStyle: {
          borderColor: '#0B1630',
          borderWidth: 3
        },
        data
      }
    ]
  }
}

export function createLineOption(data) {
  return {
    backgroundColor: 'transparent',
    color: ['#68A9FF'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'line' } },
    grid: { left: 42, right: 28, top: 40, bottom: 50 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map((item) => item.label),
      axisLine: { lineStyle: { color: '#43618B' } },
      axisLabel: { color: '#B8C7DD' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: 'rgba(133, 158, 194, 0.18)' } },
      axisLabel: { color: '#B8C7DD' }
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      {
        type: 'slider',
        start: 0,
        end: 100,
        height: 16,
        bottom: 10,
        borderColor: 'rgba(255,255,255,0.12)',
        fillerColor: 'rgba(104,169,255,0.24)',
        textStyle: { color: '#B8C7DD' }
      }
    ],
    series: [
      {
        name: '学习时间趋势',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: data.map((item) => item.value),
        lineStyle: { width: 3, color: '#68A9FF' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(104,169,255,0.40)' },
              { offset: 1, color: 'rgba(104,169,255,0.05)' }
            ]
          }
        },
        emphasis: { focus: 'series' }
      }
    ]
  }
}

export function createBarOption(data, title = 'AI工具使用频率') {
  return {
    backgroundColor: 'transparent',
    color: ['#8A7CFF'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 42, right: 24, top: 40, bottom: 44 },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.label),
      axisLine: { lineStyle: { color: '#43618B' } },
      axisLabel: { color: '#B8C7DD' }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#B8C7DD' },
      splitLine: { lineStyle: { color: 'rgba(133, 158, 194, 0.18)' } }
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      {
        type: 'slider',
        start: 0,
        end: 100,
        height: 16,
        bottom: 8,
        borderColor: 'rgba(255,255,255,0.12)',
        fillerColor: 'rgba(138,124,255,0.24)',
        textStyle: { color: '#B8C7DD' }
      }
    ],
    series: [
      {
        name: title,
        type: 'bar',
        barMaxWidth: 34,
        data: data.map((item) => item.value),
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#8A7CFF' },
              { offset: 1, color: '#4F8CFF' }
            ]
          }
        },
        emphasis: { focus: 'series' }
      }
    ]
  }
}

export function createRadarOption(radar) {
  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      textStyle: { color: '#D7E6FF' }
    },
    radar: {
      center: ['50%', '48%'],
      radius: '62%',
      indicator: radar.indicators,
      splitNumber: 4,
      axisName: { color: '#D7E6FF' },
      splitLine: { lineStyle: { color: 'rgba(133,158,194,0.2)' } },
      splitArea: {
        areaStyle: {
          color: ['rgba(255,255,255,0.01)', 'rgba(255,255,255,0.03)']
        }
      },
      axisLine: { lineStyle: { color: 'rgba(133,158,194,0.18)' } }
    },
    series: [
      {
        type: 'radar',
        symbol: 'circle',
        symbolSize: 4,
        data: radar.series.map((item, index) => ({
          name: item.name,
          value: item.value,
          lineStyle: { width: 2 + index, shadowBlur: 12, shadowColor: 'rgba(79,140,255,0.25)' },
          areaStyle: {
            color: index === 0 ? 'rgba(104,169,255,0.18)' : 'rgba(138,124,255,0.16)'
          }
        }))
      }
    ]
  }
}

export function createRangeBarOption(data) {
  return {
    backgroundColor: 'transparent',
    color: ['#4CC9F0'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 44, right: 24, top: 36, bottom: 30 },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.label),
      axisLabel: { color: '#B8C7DD' },
      axisLine: { lineStyle: { color: '#43618B' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#B8C7DD' },
      splitLine: { lineStyle: { color: 'rgba(133,158,194,0.18)' } }
    },
    series: [
      {
        name: '用户焦虑评分区间',
        type: 'bar',
        data: data.map((item) => item.value),
        barMaxWidth: 42,
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: '#4CC9F0'
        },
        emphasis: { focus: 'series' }
      }
    ]
  }
}

export function createScatterOption(data) {
  return {
    backgroundColor: 'transparent',
    color: ['#5DD8C8'],
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        const value = params.value || []
        return `${params.name}<br/>学习效率: ${value[0]}<br/>焦虑程度: ${value[1]}<br/>AI工具频率: ${value[2]}`
      }
    },
    grid: { left: 54, right: 24, top: 38, bottom: 40 },
    xAxis: {
      type: 'value',
      name: '学习效率',
      nameTextStyle: { color: '#B8C7DD' },
      axisLabel: { color: '#B8C7DD' },
      splitLine: { lineStyle: { color: 'rgba(133,158,194,0.18)' } }
    },
    yAxis: {
      type: 'value',
      name: '焦虑程度',
      nameTextStyle: { color: '#B8C7DD' },
      axisLabel: { color: '#B8C7DD' },
      splitLine: { lineStyle: { color: 'rgba(133,158,194,0.18)' } }
    },
    dataZoom: [
      { type: 'inside' },
      {
        type: 'slider',
        height: 16,
        bottom: 6,
        borderColor: 'rgba(255,255,255,0.12)',
        fillerColor: 'rgba(92,216,200,0.24)',
        textStyle: { color: '#B8C7DD' }
      }
    ],
    series: [
      {
        name: '学习效率与焦虑程度',
        type: 'scatter',
        symbolSize: (val) => 10 + val[2] * 0.3,
        emphasis: { focus: 'series' },
        data
      }
    ]
  }
}

export function createPieOption(data) {
  return {
    backgroundColor: 'transparent',
    color: ['#4F8CFF', '#8A7CFF', '#5DD8C8', '#4CC9F0', '#C084FC', '#7DD3FC'],
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      textStyle: { color: '#D7E6FF' }
    },
    series: [
      {
        name: '推荐类型占比',
        type: 'pie',
        radius: ['38%', '70%'],
        center: ['50%', '44%'],
        roseType: false,
        label: {
          color: '#EAF2FF',
          formatter: '{b}\n{d}%'
        },
        emphasis: {
          scale: true,
          scaleSize: 8
        },
        data
      }
    ]
  }
}
