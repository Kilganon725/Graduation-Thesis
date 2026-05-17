<template>
  <div class="page-wrap" style="max-width: 860px">
    <div class="page-title">FOMO 测试</div>
    <div class="page-subtitle">四项均为 0-20 分，提交后自动计算总分与焦虑等级。</div>
    <div class="panel">
      <el-form :model="form" label-width="160px">
        <el-form-item label="短视频刷取时间">
          <el-slider v-model="form.shortVideoTime" :min="0" :max="20" show-input />
        </el-form-item>
        <el-form-item label="学习方向频繁切换">
          <el-slider v-model="form.learningSwitch" :min="0" :max="20" show-input />
        </el-form-item>
        <el-form-item label="AI 信息焦虑程度">
          <el-slider v-model="form.anxietyLevel" :min="0" :max="20" show-input />
        </el-form-item>
        <el-form-item label="AI 工具使用频率">
          <el-slider v-model="form.aiUsage" :min="0" :max="20" show-input />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">提交测评</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitFomoApi } from '../api/fomo'

const router = useRouter()
const loading = ref(false)
const form = reactive({ shortVideoTime: 5, learningSwitch: 5, anxietyLevel: 5, aiUsage: 5 })

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
