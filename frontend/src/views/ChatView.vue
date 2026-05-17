<template>
  <div class="page-wrap">
    <div class="toolbar">
      <div>
        <div class="page-title">AI 聊天助手</div>
        <div class="page-subtitle">支持上下文对话，历史消息会被保留。</div>
      </div>
      <el-button @click="loadHistory">刷新历史</el-button>
    </div>
    <div class="chat-list">
      <div
        v-for="(item, idx) in messages"
        :key="idx"
        class="chat-item"
        :class="item.role === 'user' ? 'chat-user' : 'chat-ai'"
      >
        <strong>{{ item.role === 'user' ? '我' : 'AI' }}：</strong>{{ item.content }}
      </div>
    </div>
    <div class="panel" style="margin-top: 16px">
      <el-input
        v-model="question"
        type="textarea"
        :rows="4"
        placeholder="输入你想问 AI 的问题"
      />
      <div style="margin-top: 12px; display:flex; justify-content:flex-end">
        <el-button type="primary" :loading="loading" @click="send">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { chatHistoryApi, sendChatApi } from '../api/chat'

const question = ref('')
const loading = ref(false)
const messages = ref([])

async function loadHistory() {
  const res = await chatHistoryApi(20)
  const history = (res.data || []).reverse()
  messages.value = []
  history.forEach((item) => {
    messages.value.push({ role: 'user', content: item.question })
    messages.value.push({ role: 'assistant', content: item.answer })
  })
}

async function send() {
  if (!question.value.trim()) return
  loading.value = true
  const text = question.value
  messages.value.push({ role: 'user', content: text })
  question.value = ''
  try {
    const res = await sendChatApi({ question: text })
    messages.value.push({ role: 'assistant', content: res.data.answer })
  } catch (e) {
    ElMessage.error('发送失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadHistory)
</script>
