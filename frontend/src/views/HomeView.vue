<template>
  <div class="page-wrap">
    <div class="page-title">首页</div>
    <div class="page-subtitle">围绕信息焦虑评估、学习推荐、AI 对话和数据分析的毕业设计系统。</div>

    <div class="grid-3" style="margin-bottom: 16px">
      <div class="panel">
        <div style="font-size: 14px; color: #6b7280">当前用户</div>
        <div style="font-size: 24px; font-weight: 700; margin-top: 8px">{{ profile?.username || '未登录' }}</div>
        <div style="margin-top: 8px; color: #374151">专业：{{ profile?.major || '-' }}</div>
      </div>
      <div class="panel">
        <div style="font-size: 14px; color: #6b7280">学习目标</div>
        <div style="font-size: 16px; font-weight: 600; margin-top: 12px">{{ profile?.learningGoal || '未填写' }}</div>
      </div>
      <div class="panel">
        <div style="font-size: 14px; color: #6b7280">快捷操作</div>
        <div style="display:flex; gap:8px; flex-wrap: wrap; margin-top: 12px">
          <el-button type="primary" @click="router.push('/fomo')">开始测评</el-button>
          <el-button @click="router.push('/recommendation')">查看推荐</el-button>
          <el-button @click="router.push('/chat')">AI 聊天</el-button>
        </div>
      </div>
    </div>

    <div class="grid-2">
      <div class="panel">
        <div class="page-title" style="font-size: 18px">系统功能</div>
        <el-space direction="vertical" fill style="width: 100%">
          <el-alert title="FOMO 测试：根据问卷计算信息焦虑评分" type="info" :closable="false" />
          <el-alert title="学习推荐：按焦虑程度和学习目标生成路径" type="success" :closable="false" />
          <el-alert title="AI 聊天：支持上下文对话和历史记录" type="warning" :closable="false" />
          <el-alert title="数据可视化：展示焦虑分布、学习趋势、AI 使用频率" type="info" :closable="false" />
        </el-space>
      </div>
      <div class="panel">
        <div class="page-title" style="font-size: 18px">当前状态</div>
        <template v-if="score">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="总分">{{ score.totalScore }}</el-descriptions-item>
            <el-descriptions-item label="焦虑等级">{{ score.level }}</el-descriptions-item>
          </el-descriptions>
        </template>
        <template v-else>
          <el-empty description="尚未完成测评" />
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getProfileApi } from '../api/auth'
import { scoreApi } from '../api/fomo'

const router = useRouter()
const profile = ref(null)
const score = ref(null)

onMounted(async () => {
  try {
    profile.value = (await getProfileApi()).data
    score.value = (await scoreApi()).data
  } catch (e) {
    profile.value = null
  }
})
</script>
