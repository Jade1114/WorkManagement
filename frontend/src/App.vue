<template>
  <div id="app" class="app-container">
    <!-- 登录/注册页面 -->
    <template v-if="!authStore.isAuthenticated">
      <RouterView />
    </template>

    <!-- 已登录 - 显示布局 -->
    <template v-else>
      <el-container class="layout-container">
        <NavBar />
        <el-container class="main-container">
          <Sidebar />
          <el-main class="main-content">
            <RouterView />
          </el-main>
        </el-container>
      </el-container>
    </template>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import Sidebar from '@/components/common/Sidebar.vue'

const authStore = useAuthStore()
const router = useRouter()

onMounted(() => {
  // 页面加载时恢复登录状态
  authStore.restoreAuth()
})
</script>

<style scoped>
.app-container {
  width: 100%;
  height: 100vh;
}

.layout-container {
  height: 100%;
}

.main-container {
  height: calc(100% - 60px);
}

.main-content {
  padding: 20px;
  background-color: #f5f7fa;
}
</style>