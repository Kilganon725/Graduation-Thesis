<template>
  <div class="page-wrap" style="max-width: 860px">
    <div class="page-title">FOMO 测试</div>
    <div class="page-subtitle">填写行为频率，系统会自动换算成 0-20 分，再计算总分与焦虑等级。</div>
    <div class="panel">
      <el-form :model="form" label-width="160px">
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
        <el-divider />
        <el-descriptions :column="2" border title="自动换算预览">
          <el-descriptions-item label="短视频得分">{{ preview.shortVideoScore }}</el-descriptions-item>
          <el-descriptions-item label="切换得分">{{ preview.learningSwitchScore }}</el-descriptions-item>
          <el-descriptions-item label="焦虑得分">{{ preview.anxietyScore }}</el-descriptions-item>
          <el-descriptions-item label="AI 使用得分">{{ preview.aiUsageScore }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ preview.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="等级">{{ preview.level }}</el-descriptions-item>
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
const form = reactive({ shortVideoMinutes: 45, learningSwitchTimes: 2, anxietyFrequency: 3, aiUsageTimes: 3 })
const preview = reactive(scorePreview(form))

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
    ElMessage.success(`提交成功，总分 ${res.data.totalScore}`)
    router.push('/result')
  } finally {
    loading.value = false
  }
}
</script>
