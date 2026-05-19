<template>
  <div class="sbti-page">
    <div class="sbti-hero">
      <div>
        <div class="page-title sbti-title">SBTI 人格测试</div>
        <div class="sbti-subtitle">
          站内嵌入官方测试页面，完成后可继续回到系统内的 FOMO、推荐和数据可视化模块。
        </div>
      </div>
      <div class="sbti-actions">
        <el-button :icon="Refresh" :loading="loading" @click="reload">刷新嵌入页</el-button>
        <el-button type="primary" :icon="FullScreen" @click="openExternal">新窗口打开</el-button>
      </div>
    </div>

    <el-alert
      title="页面通过 iframe 嵌入 sbti.cc/test，保留在你的站内体验里，不会直接跳转离开当前项目。"
      type="info"
      :closable="false"
      show-icon
      class="sbti-alert"
    />

    <div class="sbti-frame-shell">
      <iframe
        :key="frameKey"
        ref="frameRef"
        class="sbti-frame"
        src="https://sbti.cc/test"
        title="SBTI 人格测试"
        loading="lazy"
        allow="fullscreen; clipboard-read; clipboard-write"
        referrerpolicy="no-referrer-when-downgrade"
        @load="onLoad"
      />

      <div v-if="loading" class="sbti-loading">
        <el-skeleton animated :rows="6" />
        <div class="sbti-loading-text">正在加载 SBTI 测试页面...</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { FullScreen, Refresh } from '@element-plus/icons-vue'

const loading = ref(true)
const frameKey = ref(0)
const frameRef = ref(null)
const externalUrl = 'https://sbti.cc/test'

function onLoad() {
  loading.value = false
}

function reload() {
  loading.value = true
  frameKey.value += 1
}

function openExternal() {
  window.open(externalUrl, '_blank', 'noopener,noreferrer')
  ElMessage.info('已在新窗口打开')
}
</script>

<style scoped>
.sbti-page {
  max-width: 1600px;
  margin: 0 auto;
  padding: 4px 0 20px;
}

.sbti-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18px;
  padding: 18px 20px;
  margin-bottom: 16px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(19, 34, 69, 0.96), rgba(10, 21, 44, 0.98));
  border: 1px solid rgba(118, 150, 204, 0.16);
  color: #eaf2ff;
}

.sbti-title {
  margin: 0;
}

.sbti-subtitle {
  max-width: 880px;
  margin-top: 10px;
  color: #b6c9e6;
  line-height: 1.7;
}

.sbti-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.sbti-alert {
  margin-bottom: 16px;
  border: 1px solid rgba(104, 169, 255, 0.18);
  background: rgba(104, 169, 255, 0.08);
}

.sbti-frame-shell {
  position: relative;
  min-height: calc(100vh - 210px);
  border-radius: 14px;
  overflow: hidden;
  background: linear-gradient(180deg, rgba(15, 26, 52, 0.96), rgba(11, 21, 43, 0.98));
  border: 1px solid rgba(118, 150, 204, 0.16);
  box-shadow: 0 16px 36px rgba(10, 18, 36, 0.14);
}

.sbti-frame {
  display: block;
  width: 100%;
  height: calc(100vh - 210px);
  min-height: 840px;
  border: 0;
  background: #ffffff;
}

.sbti-loading {
  position: absolute;
  inset: 0;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 20px;
  background: rgba(9, 17, 34, 0.72);
  backdrop-filter: blur(4px);
}

.sbti-loading-text {
  margin-top: 16px;
  color: #d7e6ff;
  text-align: center;
}

@media (max-width: 900px) {
  .sbti-hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .sbti-frame {
    min-height: 760px;
  }
}
</style>
