<template>
  <div class="page-wrap">
    <div class="page-title">数据可视化</div>
    <div class="page-subtitle">展示用户焦虑分布、学习时间趋势、AI 使用频率。</div>
    <div class="grid-2">
      <div class="panel">
        <div style="font-weight: 600; margin-bottom: 8px">用户焦虑分布</div>
        <div ref="pieRef" class="chart-box"></div>
      </div>
      <div class="panel">
        <div style="font-weight: 600; margin-bottom: 8px">学习时间趋势</div>
        <div ref="lineRef" class="chart-box"></div>
      </div>
    </div>
    <div class="panel" style="margin-top: 16px">
      <div style="font-weight: 600; margin-bottom: 8px">AI 使用频率</div>
      <div ref="barRef" class="chart-box"></div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { statsApi } from '../api/stats'

const pieRef = ref()
const lineRef = ref()
const barRef = ref()
let pieChart
let lineChart
let barChart

function initCharts() {
  pieChart = echarts.init(pieRef.value)
  lineChart = echarts.init(lineRef.value)
  barChart = echarts.init(barRef.value)
}

function render(data) {
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: data.anxietyDistribution.map(i => ({ name: i.label, value: i.value }))
    }]
  })

  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.learningTimeTrend.map(i => i.label) },
    yAxis: { type: 'value' },
    series: [{
      type: 'line',
      smooth: true,
      data: data.learningTimeTrend.map(i => i.value)
    }]
  })

  barChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.aiUsageFrequency.map(i => i.label) },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: data.aiUsageFrequency.map(i => i.value)
    }]
  })
}

function resize() {
  pieChart?.resize()
  lineChart?.resize()
  barChart?.resize()
}

onMounted(async () => {
  initCharts()
  const res = await statsApi()
  render(res.data)
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  pieChart?.dispose()
  lineChart?.dispose()
  barChart?.dispose()
})
</script>
