<template>
  <el-card class="chart-panel" shadow="never" :body-style="{ padding: '0px' }">
    <div class="chart-panel__header">
      <div>
        <div class="chart-title">{{ title }}</div>
        <div v-if="subtitle" class="chart-subtitle">{{ subtitle }}</div>
      </div>
      <div class="chart-panel__actions">
        <el-button size="small" text @click="$emit('refresh')">刷新</el-button>
        <el-button size="small" text @click="$emit('detail')">详情</el-button>
      </div>
    </div>
    <div ref="chartRef" class="chart-panel__body"></div>
  </el-card>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  option: { type: Object, required: true }
})

const emit = defineEmits(['refresh', 'detail', 'chart-click'])

const chartRef = ref(null)
let chart
let resizeHandler

function render() {
  if (!chart) return
  chart.setOption(props.option, true)
}

onMounted(() => {
  chart = echarts.init(chartRef.value, null, { renderer: 'canvas' })
  chart.setOption(props.option, true)
  chart.on('click', (params) => {
    const payload = {
      name: params.name,
      value: params.value,
      seriesName: params.seriesName,
      dataIndex: params.dataIndex
    }
    chart.dispatchAction({ type: 'highlight', seriesIndex: params.seriesIndex, dataIndex: params.dataIndex })
    chart.dispatchAction({ type: 'showTip', seriesIndex: params.seriesIndex, dataIndex: params.dataIndex })
    emit('chart-click', payload)
  })
  resizeHandler = () => chart?.resize()
  window.addEventListener('resize', resizeHandler)
})

watch(
  () => props.option,
  () => render(),
  { deep: true }
)

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeHandler)
  chart?.dispose()
})
</script>

<style scoped>
.chart-panel {
  background: linear-gradient(180deg, rgba(16, 27, 53, 0.98), rgba(11, 22, 48, 0.96));
  border: 1px solid rgba(118, 150, 204, 0.18);
  border-radius: 10px;
  color: #eaf2ff;
  transition: transform 0.24s ease, box-shadow 0.24s ease, border-color 0.24s ease;
}

.chart-panel:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 40px rgba(13, 25, 52, 0.28);
  border-color: rgba(104, 169, 255, 0.32);
}

.chart-panel__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 16px 16px 0;
  gap: 12px;
}

.chart-title {
  font-size: 15px;
  font-weight: 700;
  color: #f4f8ff;
}

.chart-subtitle {
  margin-top: 6px;
  font-size: 12px;
  color: #9fb2d2;
}

.chart-panel__actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.chart-panel__body {
  width: 100%;
  height: 360px;
  padding: 10px 10px 14px;
}
</style>
