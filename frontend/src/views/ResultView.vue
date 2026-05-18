<template>
  <div class="page-wrap">
    <div class="page-title">结果分析</div>
    <div class="grid-2">
      <div class="panel">
        <div class="page-title" style="font-size: 18px">最新测评结果</div>
        <template v-if="result">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="短视频时长(分钟/天)">{{ result.shortVideoMinutes }}</el-descriptions-item>
            <el-descriptions-item label="学习方向切换(次/周)">{{ result.learningSwitchTimes }}</el-descriptions-item>
            <el-descriptions-item label="AI焦虑频率">{{ anxietyLabel }}</el-descriptions-item>
            <el-descriptions-item label="AI工具使用(次/天)">{{ result.aiUsageTimes }}</el-descriptions-item>
            <el-descriptions-item label="短视频换算分">{{ result.shortVideoTime }}</el-descriptions-item>
            <el-descriptions-item label="学习切换换算分">{{ result.learningSwitch }}</el-descriptions-item>
            <el-descriptions-item label="AI焦虑换算分">{{ result.anxietyLevelScore }}</el-descriptions-item>
            <el-descriptions-item label="AI使用换算分">{{ result.aiUsage }}</el-descriptions-item>
            <el-descriptions-item label="总分">{{ result.totalScore }}</el-descriptions-item>
            <el-descriptions-item label="焦虑等级">{{ result.anxietyLevel }}</el-descriptions-item>
          </el-descriptions>
        </template>
        <template v-else>
          <el-empty description="暂无结果" />
        </template>
      </div>
      <div class="panel">
        <div class="page-title" style="font-size: 18px">解读</div>
        <el-alert
          v-if="result"
          :title="`你的焦虑等级为 ${result.anxietyLevel}`"
          :type="levelType"
          :closable="false"
        />
        <div style="margin-top: 12px; color: #4b5563; line-height: 1.8">
          分数越高，表示短视频干扰、方向切换、AI 焦虑和工具依赖越明显。建议结合推荐页生成的路径进行调整。
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { latestFomoApi } from '../api/fomo'

const result = ref(null)
const anxietyLabel = computed(() => {
  if (!result.value) return '-'
  const map = {
    1: '几乎没有',
    2: '偶尔',
    3: '有时',
    4: '经常',
    5: '总是'
  }
  return map[result.value.anxietyFrequency] || '-'
})
const levelType = computed(() => {
  if (!result.value) return 'info'
  if (result.value.anxietyLevel === '正常') return 'success'
  if (result.value.anxietyLevel === '轻度焦虑') return 'warning'
  return 'error'
})

onMounted(async () => {
  try {
    result.value = (await latestFomoApi()).data
  } catch (e) {
    result.value = null
  }
})
</script>
