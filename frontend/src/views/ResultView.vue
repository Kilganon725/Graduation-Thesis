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
          分数越高，表示短视频干扰、方向切换、AI 焦虑和工具依赖越明显。系统会自动生成干预计划，你执行后可以回到这里查看复测变化。
        </div>
      </div>
    </div>

    <div class="grid-2" style="margin-top: 16px">
      <div class="panel">
        <div class="page-title" style="font-size: 18px">干预计划</div>
        <template v-if="result?.interventionContent">
          <el-space direction="vertical" style="width: 100%" :size="12">
            <el-alert
              :title="result.interventionTitle"
              :type="result.interventionStatus === '已完成' ? 'success' : 'warning'"
              :closable="false"
            />
            <el-descriptions :column="1" border>
              <el-descriptions-item label="计划状态">{{ result.interventionStatus }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDate(result.interventionCreatedTime) }}</el-descriptions-item>
              <el-descriptions-item label="完成时间">{{ formatDate(result.interventionCompletedTime) }}</el-descriptions-item>
            </el-descriptions>
            <el-card shadow="never">
              <div class="plan-list">
                <div v-for="(line, index) in interventionLines" :key="index" class="plan-item">
                  {{ line }}
                </div>
              </div>
            </el-card>
            <el-button
              type="primary"
              :disabled="!result.interventionId || result.interventionStatus === '已完成'"
              :loading="completing"
              @click="completeIntervention"
            >
              {{ result.interventionStatus === '已完成' ? '已完成' : '标记已执行' }}
            </el-button>
          </el-space>
        </template>
        <template v-else>
          <el-empty description="暂无干预计划" />
        </template>
      </div>
      <div class="panel">
        <div class="page-title" style="font-size: 18px">复测对比</div>
        <template v-if="result?.previousTotalScore !== undefined && result?.previousTotalScore !== null">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="上次总分">{{ result.previousTotalScore }}</el-descriptions-item>
            <el-descriptions-item label="本次总分">{{ result.totalScore }}</el-descriptions-item>
            <el-descriptions-item label="分数变化">
              <el-tag :type="deltaType" effect="plain">{{ deltaText }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="上次等级">{{ result.previousAnxietyLevel }}</el-descriptions-item>
            <el-descriptions-item label="本次等级">{{ result.anxietyLevel }}</el-descriptions-item>
            <el-descriptions-item label="上次测评时间">{{ formatDate(result.previousCreatedTime) }}</el-descriptions-item>
            <el-descriptions-item label="本次测评时间">{{ formatDate(result.createdTime) }}</el-descriptions-item>
          </el-descriptions>
          <div style="margin-top: 12px; color: #4b5563; line-height: 1.8">
            {{ comparisonHint }}
          </div>
        </template>
        <template v-else>
          <el-empty description="首次测评后，下一次提交会在这里生成前后对比" />
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { latestFomoApi } from '../api/fomo'
import { completeInterventionApi } from '../api/intervention'
import { ElMessage } from 'element-plus'

const result = ref(null)
const completing = ref(false)
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
const interventionLines = computed(() => {
  if (!result.value?.interventionContent) return []
  return String(result.value.interventionContent)
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)
})
const deltaType = computed(() => {
  const delta = Number(result.value?.scoreDelta || 0)
  if (delta < 0) return 'success'
  if (delta > 0) return 'danger'
  return 'info'
})
const deltaText = computed(() => {
  const delta = Number(result.value?.scoreDelta || 0)
  if (delta < 0) return `较上次下降 ${Math.abs(delta)} 分`
  if (delta > 0) return `较上次上升 ${delta} 分`
  return '与上次持平'
})
const comparisonHint = computed(() => {
  const delta = Number(result.value?.scoreDelta || 0)
  if (delta < 0) return '说明干预与调整开始起效，焦虑总分下降。建议继续执行当前计划，并在一周后复测。'
  if (delta > 0) return '说明当前行为模式仍然在累积焦虑，建议优先执行系统生成的干预计划。'
  return '目前变化不明显，建议结合学习推荐和 AI 学习教练继续微调习惯。'
})

function formatDate(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN', { hour12: false })
}

async function completeIntervention() {
  if (!result.value?.interventionId) return
  completing.value = true
  try {
    await completeInterventionApi(result.value.interventionId)
    ElMessage.success('干预计划已标记完成')
    await loadLatest()
  } finally {
    completing.value = false
  }
}

async function loadLatest() {
  try {
    result.value = (await latestFomoApi()).data
  } catch (e) {
    result.value = null
  }
}

onMounted(async () => {
  await loadLatest()
})
</script>
