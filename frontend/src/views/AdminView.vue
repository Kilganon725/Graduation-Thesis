<template>
  <div class="page-wrap">
    <div class="page-title">管理员模块</div>
    <div class="page-subtitle">用户管理、数据统计和推荐内容管理。</div>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="用户管理" name="users">
        <div class="panel">
          <el-table :data="users" border style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="major" label="专业" />
            <el-table-column prop="learningGoal" label="学习目标" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="danger" link @click="removeUser(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="推荐内容管理" name="recs">
        <div class="panel">
          <el-form :model="form" inline>
            <el-form-item label="用户ID">
              <el-input v-model="form.userId" style="width: 120px" />
            </el-form-item>
            <el-form-item label="类型">
              <el-input v-model="form.type" style="width: 120px" />
            </el-form-item>
            <el-form-item label="内容">
              <el-input v-model="form.content" style="width: 320px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveRec">保存</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="recommendations" border style="width: 100%; margin-top: 16px">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="content" label="内容" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="danger" link @click="removeRec(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminUsersApi, deleteAdminUserApi, adminRecommendationsApi, saveAdminRecommendationApi, deleteAdminRecommendationApi } from '../api/admin'

const activeTab = ref('users')
const users = ref([])
const recommendations = ref([])
const form = reactive({ userId: '', type: '', content: '' })

async function loadUsers() {
  users.value = (await adminUsersApi()).data?.records || []
}

async function loadRecommendations() {
  recommendations.value = (await adminRecommendationsApi()).data?.records || []
}

async function removeUser(id) {
  await deleteAdminUserApi(id)
  ElMessage.success('已删除')
  loadUsers()
}

async function saveRec() {
  await saveAdminRecommendationApi({
    userId: Number(form.userId),
    type: form.type,
    content: form.content
  })
  ElMessage.success('已保存')
  form.userId = ''
  form.type = ''
  form.content = ''
  loadRecommendations()
}

async function removeRec(id) {
  await deleteAdminRecommendationApi(id)
  ElMessage.success('已删除')
  loadRecommendations()
}

onMounted(() => {
  loadUsers()
  loadRecommendations()
})
</script>
