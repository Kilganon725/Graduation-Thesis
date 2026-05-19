<template>
  <el-card class="count-card" shadow="never">
    <div class="count-header">
      <span class="count-label">{{ label }}</span>
      <el-tag v-if="delta !== undefined" :type="deltaType" effect="dark" size="small">
        {{ deltaText }}
      </el-tag>
    </div>
    <div class="count-value">
      <span>{{ displayValue }}</span>
      <span class="count-suffix">{{ suffix }}</span>
    </div>
    <div v-if="meta" class="count-meta">{{ meta }}</div>
  </el-card>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'

const props = defineProps({
  label: { type: String, required: true },
  value: { type: [Number, String], required: true },
  suffix: { type: String, default: '' },
  delta: { type: Number, default: undefined },
  meta: { type: String, default: '' }
})

const animated = ref(0)

function animate(to) {
  const target = Number(to) || 0
  const start = animated.value
  const duration = 900
  const startTime = performance.now()
  const step = (now) => {
    const progress = Math.min((now - startTime) / duration, 1)
    animated.value = start + (target - start) * progress
    if (progress < 1) requestAnimationFrame(step)
  }
  requestAnimationFrame(step)
}

onMounted(() => animate(props.value))
watch(
  () => props.value,
  (val) => animate(val)
)

const displayValue = computed(() => {
  const raw = Number(animated.value)
  return Number.isInteger(Number(props.value)) ? Math.round(raw) : raw.toFixed(1)
})

const deltaType = computed(() => {
  if (props.delta === undefined) return 'info'
  return props.delta >= 0 ? 'success' : 'danger'
})

const deltaText = computed(() => {
  if (props.delta === undefined) return ''
  return `${props.delta >= 0 ? '+' : ''}${props.delta}`
})
</script>

<style scoped>
.count-card {
  background: linear-gradient(180deg, rgba(16, 27, 53, 0.98), rgba(11, 22, 48, 0.96));
  border: 1px solid rgba(118, 150, 204, 0.18);
  color: #eaf2ff;
  border-radius: 10px;
  transition: transform 0.24s ease, box-shadow 0.24s ease, border-color 0.24s ease;
}

.count-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 40px rgba(13, 25, 52, 0.36);
  border-color: rgba(104, 169, 255, 0.32);
}

.count-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.count-label {
  color: #bdd2f5;
  font-size: 13px;
  letter-spacing: 0;
}

.count-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-top: 16px;
  font-size: 30px;
  font-weight: 700;
  color: #f4f8ff;
}

.count-suffix {
  font-size: 14px;
  color: #aac3e5;
}

.count-meta {
  margin-top: 10px;
  color: #96adc8;
  font-size: 12px;
}
</style>
