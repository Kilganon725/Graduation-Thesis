const palette = ['#8ea8ff', '#86d3ff', '#b8a5ff', '#8fe3c7', '#a0b5ff', '#f4b6d0']

const commonTooltip = {
  trigger: 'axis',
  backgroundColor: 'rgba(255,255,255,0.96)',
  borderColor: 'rgba(102, 134, 213, 0.18)',
  textStyle: { color: '#233a60' }
}

function axisStyle() {
  return {
    axisLine: { lineStyle: { color: '#d8e2f1' } },
    axisTick: { lineStyle: { color: '#d8e2f1' } },
    axisLabel: { color: '#647896' },
    splitLine: { lineStyle: { color: 'rgba(115, 137, 176, 0.12)' } }
  }
}

export function createDonutOption(data = [], title = '') {
  return {
    color: palette,
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.96)',
      borderColor: 'rgba(102, 134, 213, 0.18)',
      textStyle: { color: '#233a60' }
    },
    legend: {
      bottom: 6,
      left: 'center',
      textStyle: { color: '#5c6f8f' }
    },
    series: [
      {
        name: title,
        type: 'pie',
        radius: ['46%', '72%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          color: '#32506f',
          formatter: '{b}\n{d}%'
        },
        emphasis: {
          scale: true,
          scaleSize: 8
        },
        data,
        animationDuration: 1000,
        animationEasing: 'cubicOut'
      }
    ]
  }
}

export function createLineOption(data = [], seriesName = '趋势') {
  return {
    color: ['#769cff'],
    tooltip: {
      ...commonTooltip,
      trigger: 'axis'
    },
    grid: { left: 42, right: 18, top: 30, bottom: 44 },
    legend: {
      top: 6,
      right: 10,
      textStyle: { color: '#5f7395' }
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map((item) => item.label),
      ...axisStyle()
    },
    yAxis: {
      type: 'value',
      ...axisStyle()
    },
    dataZoom: [
      { type: 'inside', throttle: 50 },
      { type: 'slider', height: 18, bottom: 8, borderColor: 'rgba(0,0,0,0)', fillerColor: 'rgba(134, 163, 255, 0.2)' }
    ],
    series: [
      {
        name: seriesName,
        type: 'line',
        smooth: true,
        data: data.map((item) => item.value),
        symbolSize: 9,
        showSymbol: false,
        areaStyle: { color: 'rgba(118, 156, 255, 0.14)' },
        lineStyle: { width: 3 },
        emphasis: { focus: 'series' },
        animationDuration: 1200,
        animationEasing: 'cubicOut'
      }
    ]
  }
}

export function createBarOption(data = [], seriesName = '数量') {
  return {
    color: ['#8ca5ff'],
    tooltip: {
      ...commonTooltip,
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: { left: 44, right: 20, top: 30, bottom: 44 },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.label),
      ...axisStyle()
    },
    yAxis: {
      type: 'value',
      ...axisStyle()
    },
    dataZoom: [
      { type: 'inside', throttle: 50 },
      { type: 'slider', height: 18, bottom: 8, borderColor: 'rgba(0,0,0,0)', fillerColor: 'rgba(140, 165, 255, 0.2)' }
    ],
    series: [
      {
        name: seriesName,
        type: 'bar',
        data: data.map((item) => item.value),
        barWidth: '46%',
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          shadowColor: 'rgba(118, 156, 255, 0.24)',
          shadowBlur: 12
        },
        emphasis: { focus: 'series' },
        animationDuration: 1200
      }
    ]
  }
}

export function createHorizontalBarOption(data = [], seriesName = '数量') {
  return {
    color: ['#8fd3ff'],
    tooltip: {
      ...commonTooltip,
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: { left: 88, right: 20, top: 20, bottom: 26 },
    xAxis: {
      type: 'value',
      ...axisStyle()
    },
    yAxis: {
      type: 'category',
      data: data.map((item) => item.label),
      ...axisStyle()
    },
    series: [
      {
        name: seriesName,
        type: 'bar',
        data: data.map((item) => item.value),
        barWidth: '54%',
        itemStyle: { borderRadius: [0, 8, 8, 0] },
        emphasis: { focus: 'series' },
        animationDuration: 1000
      }
    ]
  }
}

export function createPieOption(data = [], title = '') {
  return {
    color: palette,
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.96)',
      borderColor: 'rgba(102, 134, 213, 0.18)',
      textStyle: { color: '#233a60' }
    },
    legend: {
      bottom: 6,
      left: 'center',
      textStyle: { color: '#5c6f8f' }
    },
    series: [
      {
        name: title,
        type: 'pie',
        radius: ['28%', '70%'],
        center: ['50%', '44%'],
        roseType: 'radius',
        label: { color: '#32506f' },
        emphasis: { scale: true, scaleSize: 8 },
        data,
        animationDuration: 1000
      }
    ]
  }
}
