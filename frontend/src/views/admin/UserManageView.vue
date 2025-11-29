<template>
  <div class="page">
    <section class="card header-card">
      <div>
        <h2>用户管理</h2>
      </div>
      <el-button :icon="Refresh" :loading="loading" @click="loadUsers">刷新</el-button>
    </section>

    <section class="card table-card">
      <el-table :data="pagedUsers" stripe border style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="role" label="角色" min-width="140">
          <template #default="{ row }">
            <el-select v-model="row.role" placeholder="选择角色" size="small" @change="updateRole(row)">
              <el-option v-for="r in roles" :key="r" :label="r" :value="r" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="active" label="状态" min-width="120">
          <template #default="{ row }">
            <el-switch v-model="row.active" @change="toggleActive(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-popconfirm title="确认冻结该用户？" @confirm="disableUser(row)">
              <template #reference>
                <el-button type="danger" size="small">冻结</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <Pagination :total="users.length" :page-size="pageSize" v-model:current-page="currentPage" />
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import http from '@/net/index.js'
import Pagination from '@/components/Pagination.vue'

const roles = ['admin', 'teacher', 'student']
const users = ref([])
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)

const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return users.value.slice(start, start + pageSize)
})

const loadUsers = async () => {
  loading.value = true
  try {
    const data = await http.get('/users/admin/list')
    users.value = (data || []).map(u => ({
      id: u.id,
      username: u.username,
      role: u.role,
      active: u.active !== false
    }))
  } catch (e) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const updateRole = async (row) => {
  try {
    await http.put('/users/admin/update', {
      userId: row.id,
      role: row.role
    })
    ElMessage.success(`已将 ${row.username} 设置为 ${row.role}`)
  } catch (e) {
    ElMessage.error('更新角色失败')
    await loadUsers()
  }
}

const toggleActive = async (row) => {
  try {
    await http.put('/users/admin/update', {
      userId: row.id,
      active: row.active
    })
    ElMessage.success(`${row.username} 状态已${row.active ? '启用' : '禁用'}`)
  } catch (e) {
    ElMessage.error('更新状态失败')
    await loadUsers()
  }
}

const disableUser = async (row) => {
  row.active = false
  await toggleActive(row)
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.header-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-l);
}

.muted {
  color: var(--color-text-tertiary);
  margin: 4px 0 0;
}

.table-card {
  padding: var(--spacing-m);
}
</style>
