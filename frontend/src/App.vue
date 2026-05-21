<template>
  <el-container class="app-shell">
    <el-header class="app-header">
      <div class="brand" @click="router.push('/')">
        AI时代大学生信息焦虑分析与个性化学习推荐系统
      </div>
      <el-menu
        mode="horizontal"
        :ellipsis="false"
        class="nav-menu"
        :default-active="activePath"
      >
        <el-menu-item index="/" @click="router.push('/')">首页</el-menu-item>
        <el-menu-item v-if="isAuthed" index="/fomo" @click="router.push('/fomo')">FOMO测试</el-menu-item>
        <el-menu-item v-if="isAuthed" index="/result" @click="router.push('/result')">结果分析</el-menu-item>
        <el-menu-item v-if="isAuthed" index="/recommendation" @click="router.push('/recommendation')">学习推荐</el-menu-item>
        <el-menu-item v-if="isAuthed" index="/chat" @click="router.push('/chat')">AI聊天</el-menu-item>
        <el-menu-item v-if="isAuthed" index="/visualization" @click="router.push('/visualization')">数据可视化</el-menu-item>
        <el-menu-item index="/sbti" @click="router.push('/sbti')">SBTI人格测试</el-menu-item>
        <el-menu-item v-if="isAdmin" index="/admin" @click="router.push('/admin')">后台管理</el-menu-item>
      </el-menu>
      <div class="header-actions">
        <template v-if="isAuthed">
          <span class="user-text">{{ username }}</span>
          <el-button size="small" @click="logout">退出</el-button>
        </template>
        <template v-else>
          <el-button size="small" @click="router.push('/login')">登录</el-button>
          <el-button size="small" type="primary" @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main class="app-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed, ref, watchEffect } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { authState, clearAuth } from './utils/auth'

const router = useRouter()
const route = useRoute()
const activePath = ref(route.path)

watchEffect(() => {
  activePath.value = route.path
})

const isAuthed = computed(() => !!authState.value.token)
const username = computed(() => authState.value.user?.username || '未登录')
const isAdmin = computed(() => authState.value.user?.username === 'admin')

function logout() {
  clearAuth()
  router.push('/login')
}
</script>
