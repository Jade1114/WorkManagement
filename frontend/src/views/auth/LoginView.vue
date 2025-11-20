<template>
    <div class="login-container">
        <div class="login-box">
            <!-- 标题 -->
            <h1>📚 作业管理系统</h1>

            <!-- 登录表单 -->
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" @keyup.enter="handleLogin">
                <!-- 用户名输入框 -->
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名" clearable />
                </el-form-item>

                <!-- 密码输入框 -->
                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" clearable show-password />
                </el-form-item>

                <!-- 登录按钮 -->
                <el-form-item>
                    <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
                        登录
                    </el-button>
                </el-form-item>

                <!-- 注册链接 -->
                <el-form-item>
                    <el-button @click="goToRegister" style="width: 100%" text>
                        还没有账号？点击注册
                    </el-button>
                </el-form-item>
            </el-form>

            <!-- 测试账号提示 -->
            <div class="test-accounts">
                <p><strong>测试账号：</strong></p>
                <p>• 教师: teacher001 / 密码: 123456</p>
                <p>• 学生: 20240101 / 密码: 123456</p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

// ============ 1️⃣ 基本变量 ============
const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

// ============ 2️⃣ 表单数据 ============
const form = reactive({
    username: '',
    password: '',
})

// ============ 3️⃣ 表单校验规则 ============
const rules = {
    // 用户名校验
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
    ],
    // 密码校验
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
    ],
}

// ============ 4️⃣ 登录方法 ============
const handleLogin = async () => {
    // 首先验证表单
    if (!formRef.value) return

    // 调用 Element Plus 表单的验证方法
    // validate() 返回一个 Promise
    // 如果校验通过返回 true，失败返回 false
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) {
        // 校验失败，validate() 已经显示错误信息了
        return
    }

    loading.value = true
    try {
        // 调用 store 的登录方法
        // authStore.login 是异步的，所以要 await
        await authStore.login(form.username, form.password)

        // 登录成功
        ElMessage.success('登录成功！')
    } catch (error) {
        // 登录失败
        // error.message 来自后端返回的错误信息
        ElMessage.error(error.message || '登录失败')
    } finally {
        // 无论成功失败都要关闭加载状态
        loading.value = false
    }
}

// ============ 5️⃣ 注册链接 ============
const goToRegister = () => {
    router.push('/register')
}
</script>

<style scoped>
/* ===== 登录容器 ===== */
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* ===== 登录框 ===== */
.login-box {
    width: 100%;
    max-width: 400px;
    padding: 40px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* ===== 标题 ===== */
.login-box h1 {
    text-align: center;
    margin-bottom: 30px;
    font-size: 28px;
    color: #333;
    font-weight: bold;
}

/* ===== 测试账号提示框 ===== */
.test-accounts {
    margin-top: 20px;
    padding: 15px;
    background-color: #f0f9ff;
    border-radius: 4px;
    border-left: 4px solid #409eff;
    font-size: 12px;
    color: #666;
}

.test-accounts p {
    margin: 5px 0;
}
</style>