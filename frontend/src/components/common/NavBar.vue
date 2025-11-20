<template>
    <el-header class="navbar">
        <div class="navbar-left">
            <h1>📚 作业管理系统</h1>
        </div>
        <div class="navbar-right">
            <span class="user-info">{{ authStore.user?.username }} ({{ roleText }})</span>
            <el-dropdown @command="handleCommand">
                <el-button type="primary" text>
                    {{ authStore.user?.username }}
                    <el-icon class="el-icon--right">
                        <arrow-down />
                    </el-icon>
                </el-button>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </el-header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

// 计算显示的角色文本
const roleText = computed(() => {
    return authStore.user?.role === 'teacher' ? '教师' : '学生'
})

// 下拉菜单命令处理
const handleCommand = async (command) => {
    if (command === 'logout') {
        // 确认退出
        try {
            await authStore.logout()
            ElMessage.success('退出成功')
            router.push('/login')
        } catch (error) {
            ElMessage.error('退出失败')
        }
    }
}
</script>

<style scoped>
.navbar {
    display: flex !important;
    justify-content: space-between;
    align-items: center;
    background-color: #409eff;
    color: white;
    padding: 0 20px !important;
    height: 60px !important;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.navbar-left h1 {
    margin: 0;
    font-size: 24px;
    color: white;
    font-weight: bold;
}

.navbar-right {
    display: flex;
    align-items: center;
    gap: 15px;
}

.user-info {
    color: white;
    font-size: 14px;
}
</style>