<template>
  <div class="page">
    <section class="card header-card">
      <div>
        <h2>用户管理</h2>
      </div>
      <div class="header-actions">
        <SearchInput v-model="searchKeyword" placeholder="搜索用户名" style="width: 220px" />
        <el-button :icon="Refresh" :loading="loading" @click="loadUsers">刷新</el-button>
      </div>
    </section>

    <TableShell :data="pagedUsers" :loading="loading">
      <el-table-column prop="username" label="用户名" min-width="140" />
      <el-table-column label="状态" min-width="200">
        <template #default="{ row }">
          <el-space>
            <el-tag size="small">{{ row.role }}</el-tag>
            <el-tag size="small" :type="row.active ? 'success' : 'danger'">
              {{ row.active ? '启用' : '封禁' }}
            </el-tag>
          </el-space>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            size="small"
            :type="row.active ? 'danger' : 'success'"
            @click="toggleActive(row)"
          >
            {{ row.active ? '封禁' : '解冻' }}
          </el-button>
          <el-button size="small" @click="openRoleDialog(row)">更改角色</el-button>
        </template>
      </el-table-column>
      <template #footer>
        <Pagination :total="filteredUsers.length" :page-size="pageSize" v-model:current-page="currentPage" />
      </template>
    </TableShell>

    <el-dialog v-model="roleDialogVisible" title="更改角色" width="360px">
      <el-form label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="roleForm.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="roleForm.role" placeholder="选择角色" style="width: 100%">
            <el-option v-for="r in roles" :key="r" :label="r" :value="r" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRoleChange">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import http from '@/net/index.js'
import Pagination from '@/components/Pagination.vue'
import TableShell from '@/components/TableShell.vue'
import SearchInput from '@/components/SearchInput.vue'

const roles = ['admin', 'teacher', 'student']
const users = ref([])
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)
const searchKeyword = ref('')
const roleDialogVisible = ref(false)
const roleForm = ref({ id: null, username: '', role: 'student' })

const filteredUsers = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (!keyword) return users.value
  return users.value.filter((u) => u.username.toLowerCase().includes(keyword))
})

const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredUsers.value.slice(start, start + pageSize)
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

const updateRole = async (payload) => {
  try {
    await http.put('/users/admin/update', {
      userId: payload.id,
      role: payload.role
    })
    ElMessage.success(`已将 ${payload.username} 设置为 ${payload.role}`)
  } catch (e) {
    ElMessage.error('更新角色失败')
    await loadUsers()
  }
}

const toggleActive = async (row) => {
  try {
    await http.put('/users/admin/update', {
      userId: row.id,
      active: row.active ? 0 : 1
    })
    row.active = !row.active
    ElMessage.success(`${row.username} 状态已${row.active ? '启用' : '封禁'}`)
  } catch (e) {
    ElMessage.error('更新状态失败')
    await loadUsers()
  }
}

const openRoleDialog = (row) => {
  roleForm.value = { id: row.id, username: row.username, role: row.role }
  roleDialogVisible.value = true
}

const submitRoleChange = async () => {
  await updateRole(roleForm.value)
  roleDialogVisible.value = false
  await loadUsers()
}

watch(searchKeyword, () => {
  currentPage.value = 1
})

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

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-m);
}

.muted {
  color: var(--color-text-tertiary);
  margin: 4px 0 0;
}

.table-card {
  padding: var(--spacing-m);
}
</style>
