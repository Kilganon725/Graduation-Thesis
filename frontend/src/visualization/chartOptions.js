export function createDonutOption(data) {
  return {
    backgroundColor: 'transparent',
    color: ['#6BA8FF', '#9A8CFF', '#70D8CC', '#82CFFF', '#D39DFF'],
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      textStyle: { color: '#5F6E87' }
    },
    series: [
      {
        name: 'FOMO焦虑等级',
        type: 'pie',
        radius: ['48%', '72%'],
        center: ['50%', '44%'],
        avoidLabelOverlap: true,
        label: {
          color: '#3F4C61',
          formatter: '{b}\n{d}%'
        },
        emphasis: {
          scale: true,
          scaleSize: 10
        },
        itemStyle: {
          borderColor: '#F5F8FD',
          borderWidth: 4
        },
        data
      }
    ]
  }
}

export function createLineOption(data) {
  return {
    backgroundColor: 'transparent',
    color: ['#6BA8FF'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'line' } },
    grid: { left: 42, right: 28, top: 40, bottom: 50 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map((item) => item.label),
      axisLine: { lineStyle: { color: '#D4DEEC' } },
      axisLabel: { color: '#5F6E87' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: 'rgba(148, 170, 203, 0.16)' } },
      axisLabel: { color: '#5F6E87' }
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      {
        type: 'slider',
        start: 0,
        end: 100,
        height: 16,
        bottom: 10,
        borderColor: 'rgba(148,170,203,0.18)',
        fillerColor: 'rgba(107,168,255,0.18)',
        textStyle: { color: '#5F6E87' }
      }
    ],
    series: [
      {
        name: '学习时间趋势',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: data.map((item) => item.value),
        lineStyle: { width: 3, color: '#6BA8FF' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(107,168,255,0.30)' },
              { offset: 1, color: 'rgba(107,168,255,0.04)' }
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
    color: ['#9A8CFF'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 42, right: 24, top: 40, bottom: 44 },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.label),
      axisLine: { lineStyle: { color: '#D4DEEC' } },
      axisLabel: { color: '#5F6E87' }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#5F6E87' },
      splitLine: { lineStyle: { color: 'rgba(148,170,203,0.16)' } }
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      {
        type: 'slider',
        start: 0,
        end: 100,
        height: 16,
        bottom: 8,
        borderColor: 'rgba(148,170,203,0.18)',
        fillerColor: 'rgba(154,140,255,0.18)',
        textStyle: { color: '#5F6E87' }
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
              { offset: 0, color: '#9A8CFF' },
              { offset: 1, color: '#6BA8FF' }
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
      textStyle: { color: '#5F6E87' }
    },
    radar: {
      center: ['50%', '48%'],
      radius: '62%',
      indicator: radar.indicators,
      splitNumber: 4,
      axisName: { color: '#5F6E87' },
      splitLine: { lineStyle: { color: 'rgba(148,170,203,0.16)' } },
      splitArea: {
        areaStyle: {
          color: ['rgba(255,255,255,0.35)', 'rgba(107,168,255,0.03)']
        }
      },
      axisLine: { lineStyle: { color: 'rgba(148,170,203,0.18)' } }
    },
    series: [
      {
        type: 'radar',
        symbol: 'circle',
        symbolSize: 4,
        data: radar.series.map((item, index) => ({
          name: item.name,
          value: item.value,
          lineStyle: { width: 2 + index, shadowBlur: 12, shadowColor: 'rgba(107,168,255,0.18)' },
          areaStyle: {
            color: index === 0 ? 'rgba(107,168,255,0.18)' : 'rgba(154,140,255,0.14)'
          }
        }))
      }
    ]
  }
}

export function createRangeBarOption(data) {
  return {
    backgroundColor: 'transparent',
    color: ['#7BC8F6'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 44, right: 24, top: 36, bottom: 30 },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.label),
      axisLabel: { color: '#5F6E87' },
      axisLine: { lineStyle: { color: '#D4DEEC' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#5F6E87' },
      splitLine: { lineStyle: { color: 'rgba(148,170,203,0.16)' } }
    },
    series: [
      {
        name: '用户焦虑评分区间',
        type: 'bar',
        data: data.map((item) => item.value),
        barMaxWidth: 42,
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: '#7BC8F6'
        },
        emphasis: { focus: 'series' }
      }
    ]
  }
}

export function createScatterOption(data) {
  return {
    backgroundColor: 'transparent',
    color: ['#7BC8F6'],
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
      nameTextStyle: { color: '#5F6E87' },
      axisLabel: { color: '#5F6E87' },
      splitLine: { lineStyle: { color: 'rgba(148,170,203,0.16)' } }
    },
    yAxis: {
      type: 'value',
      name: '焦虑程度',
      nameTextStyle: { color: '#5F6E87' },
      axisLabel: { color: '#5F6E87' },
      splitLine: { lineStyle: { color: 'rgba(148,170,203,0.16)' } }
    },
    dataZoom: [
      { type: 'inside' },
      {
        type: 'slider',
        height: 16,
        bottom: 6,
        borderColor: 'rgba(148,170,203,0.18)',
        fillerColor: 'rgba(123,200,246,0.18)',
        textStyle: { color: '#5F6E87' }
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
    color: ['#6BA8FF', '#9A8CFF', '#7BC8F6', '#70D8CC', '#C9A5FF', '#A9D8FF'],
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      textStyle: { color: '#5F6E87' }
    },
    series: [
      {
        name: '推荐类型占比',
        type: 'pie',
        radius: ['38%', '70%'],
        center: ['50%', '44%'],
        roseType: false,
        label: {
          color: '#3F4C61',
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
