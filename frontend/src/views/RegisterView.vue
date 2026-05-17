<template>
  <div class="page-wrap" style="max-width: 520px">
    <div class="page-title">注册</div>
    <div class="panel">
      <el-form :model="form" label-width="80px" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="form.major" placeholder="如：计算机科学与技术" />
        </el-form-item>
        <el-form-item label="目标">
          <el-input v-model="form.learningGoal" placeholder="如：AI 方向就业 / 考研 / 数据分析" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">注册</el-button>
          <el-button @click="router.push('/login')">去登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerApi } from '../api/auth'
import { setAuth } from '../utils/auth'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '', major: '', learningGoal: '' })

async function submit() {
  loading.value = true
  try {
    const res = await registerApi(form)
    setAuth(res.data.token, { userId: res.data.userId, username: res.data.username })
    ElMessage.success('注册成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>
