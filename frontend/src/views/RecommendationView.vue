<template>
  <div class="page-wrap">
    <div class="toolbar">
      <div>
        <div class="page-title">学习推荐</div>
        <div class="page-subtitle">按最新测评和学习目标生成推荐路径。</div>
      </div>
      <el-button type="primary" :loading="loading" @click="generate">生成推荐</el-button>
    </div>
    <div class="grid-2">
      <div class="panel">
        <div class="page-title" style="font-size: 18px">我的推荐</div>
        <el-empty v-if="items.length === 0" description="暂无推荐" />
        <el-timeline v-else>
          <el-timeline-item v-for="item in items" :key="item.id" :timestamp="item.type">
            {{ item.content }}
          </el-timeline-item>
        </el-timeline>
      </div>
      <div class="panel">
        <div class="page-title" style="font-size: 18px">规则说明</div>
        <el-space direction="vertical" fill style="width:100%">
          <el-alert title="焦虑高且学习时间低 -> 基础学习路径" type="warning" :closable="false" />
          <el-alert title="学习方向为 AI -> Python + 数据分析路线" type="success" :closable="false" />
          <el-alert title="频繁切换方向 -> 单一方向深度学习" type="info" :closable="false" />
        </el-space>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { generateRecommendationApi, myRecommendationApi } from '../api/recommendation'

const items = ref([])
const loading = ref(false)

async function loadMine() {
  items.value = (await myRecommendationApi()).data || []
}

async function generate() {
  loading.value = true
  try {
    items.value = (await generateRecommendationApi()).data || []
    ElMessage.success('推荐已生成')
  } finally {
    loading.value = false
  }
}

onMounted(loadMine)
</script>
