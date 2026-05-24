<template>
  <div class="page-wrap" style="max-width: 860px">
    <div class="page-title">FOMO 测试</div>
    <div class="page-subtitle">填写行为频率与补充画像，系统会自动换算成 0-20 分，再计算总分、焦虑等级，并生成干预计划。</div>
    <div class="panel">
      <el-form :model="form" label-width="160px">
        <el-alert
          title="说明：下列“补充画像”不参与总分计算，仅用于生成更准确的干预建议与结果分析。"
          type="info"
          :closable="false"
          style="margin-bottom: 16px"
        />
        <el-form-item label="每天刷短视频时长">
          <el-input-number v-model="form.shortVideoMinutes" :min="0" :max="240" :step="5" />
          <span style="margin-left: 8px; color: #6b7280">分钟</span>
        </el-form-item>
        <el-form-item label="每周切换学习方向">
          <el-input-number v-model="form.learningSwitchTimes" :min="0" :max="20" :step="1" />
          <span style="margin-left: 8px; color: #6b7280">次</span>
        </el-form-item>
        <el-form-item label="对AI信息感到焦虑">
          <el-select v-model="form.anxietyFrequency" style="width: 240px">
            <el-option label="几乎没有" :value="1" />
            <el-option label="偶尔" :value="2" />
            <el-option label="有时" :value="3" />
            <el-option label="经常" :value="4" />
            <el-option label="总是" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="每天使用AI工具">
          <el-input-number v-model="form.aiUsageTimes" :min="0" :max="50" :step="1" />
          <span style="margin-left: 8px; color: #6b7280">次</span>
        </el-form-item>
        <el-divider content-position="left">补充画像</el-divider>
        <el-form-item label="每日睡眠时长">
          <el-input-number v-model="form.sleepHours" :min="0" :max="16" :step="1" />
          <span style="margin-left: 8px; color: #6b7280">小时</span>
        </el-form-item>
        <el-form-item label="学习专注程度">
          <el-select v-model="form.focusLevel" style="width: 240px">
            <el-option label="很低" :value="1" />
            <el-option label="较低" :value="2" />
            <el-option label="一般" :value="3" />
            <el-option label="较高" :value="4" />
            <el-option label="很高" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="消息通知干扰">
          <el-select v-model="form.notificationFrequency" style="width: 240px">
            <el-option label="几乎没有" :value="1" />
            <el-option label="偶尔" :value="2" />
            <el-option label="有时" :value="3" />
            <el-option label="经常" :value="4" />
            <el-option label="频繁" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="学习目标清晰度">
          <el-select v-model="form.goalClarity" style="width: 240px">
            <el-option label="很模糊" :value="1" />
            <el-option label="较模糊" :value="2" />
            <el-option label="一般" :value="3" />
            <el-option label="较清晰" :value="4" />
            <el-option label="非常清晰" :value="5" />
          </el-select>
        </el-form-item>
        <el-divider />
        <el-descriptions :column="2" border title="自动换算预览">
          <el-descriptions-item label="短视频得分">{{ preview.shortVideoScore }}</el-descriptions-item>
          <el-descriptions-item label="切换得分">{{ preview.learningSwitchScore }}</el-descriptions-item>
          <el-descriptions-item label="焦虑得分">{{ preview.anxietyScore }}</el-descriptions-item>
          <el-descriptions-item label="AI 使用得分">{{ preview.aiUsageScore }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ preview.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="等级">{{ preview.level }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="2" border title="补充画像预览" style="margin-top: 16px">
          <el-descriptions-item label="睡眠时长">{{ form.sleepHours }} 小时</el-descriptions-item>
          <el-descriptions-item label="专注程度">{{ profileLabels.focusLevel }}</el-descriptions-item>
          <el-descriptions-item label="通知干扰">{{ profileLabels.notificationFrequency }}</el-descriptions-item>
          <el-descriptions-item label="目标清晰度">{{ profileLabels.goalClarity }}</el-descriptions-item>
        </el-descriptions>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">提交测评</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitFomoApi } from '../api/fomo'
import { scorePreview } from '../utils/fomoScore'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  shortVideoMinutes: 45,
  learningSwitchTimes: 2,
  anxietyFrequency: 3,
  aiUsageTimes: 3,
  sleepHours: 7,
  focusLevel: 3,
  notificationFrequency: 3,
  goalClarity: 3
})
const preview = reactive(scorePreview(form))
const profileLabels = computed(() => {
  const map = {
    focusLevel: { 1: '很低', 2: '较低', 3: '一般', 4: '较高', 5: '很高' },
    notificationFrequency: { 1: '几乎没有', 2: '偶尔', 3: '有时', 4: '经常', 5: '频繁' },
    goalClarity: { 1: '很模糊', 2: '较模糊', 3: '一般', 4: '较清晰', 5: '非常清晰' }
  }
  return {
    focusLevel: map.focusLevel[form.focusLevel] || '-',
    notificationFrequency: map.notificationFrequency[form.notificationFrequency] || '-',
    goalClarity: map.goalClarity[form.goalClarity] || '-'
  }
})

watch(
  form,
  () => {
    Object.assign(preview, scorePreview(form))
  },
  { deep: true, immediate: true }
)

async function submit() {
  loading.value = true
  try {
    const res = await submitFomoApi(form)
    ElMessage.success(`提交成功，总分 ${res.data.totalScore}，已生成干预计划`)
    router.push('/result')
  } finally {
    loading.value = false
  }
}
</script>
