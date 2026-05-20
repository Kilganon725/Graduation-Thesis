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
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 250, 255, 0.98));
  border: 1px solid rgba(107, 168, 255, 0.14);
  border-radius: 10px;
  color: #25324a;
  transition: transform 0.24s ease, box-shadow 0.24s ease, border-color 0.24s ease;
  box-shadow: 0 10px 24px rgba(67, 97, 139, 0.08);
}

.chart-panel:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 36px rgba(67, 97, 139, 0.12);
  border-color: rgba(107, 168, 255, 0.28);
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
  color: #1f3554;
}

.chart-subtitle {
  margin-top: 6px;
  font-size: 12px;
  color: #7a879d;
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
