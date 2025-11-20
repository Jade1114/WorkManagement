<template>
    <div class="register-container">
        <div class="register-box">
            <!-- 标题 -->
            <h1>注册新账号</h1>

            <!-- 注册表单 -->
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" @keyup.enter="handleRegister">
                <!-- 用户名输入框 -->
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名（学号等）" clearable />
                </el-form-item>

                <!-- 密码输入框 -->
                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" clearable show-password />
                </el-form-item>

                <!-- 确认密码输入框 -->
                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" clearable
                        show-password />
                </el-form-item>

                <!-- 注册按钮 -->
                <el-form-item>
                    <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">
                        注册
                    </el-button>
                </el-form-item>

                <!-- 登录链接 -->
                <el-form-item>
                    <el-button @click="goToLogin" style="width: 100%" text>
                        已有账号？返回登录
                    </el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

// 表单数据
const form = reactive({
    username: '',
    password: '',
    confirmPassword: '',
})

// 自定义密码匹配校验
const validatePasswordMatch = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请输入确认密码'))
    } else if (value !== form.password) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

// 表单校验规则
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
    ],
    confirmPassword: [
        { validator: validatePasswordMatch, trigger: 'blur' },
    ],
}

// 注册方法
const handleRegister = async () => {
    if (!formRef.value) return

    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return

    loading.value = true
    try {
        // 调用注册 API
        await authStore.register(form.username, form.password)
        ElMessage.success('注册成功，请登录')
        // 注册成功后跳转到登录页
        router.push('/login')
    } catch (error) {
        ElMessage.error(error.message || '注册失败')
    } finally {
        loading.value = false
    }
}

// 返回登录
const goToLogin = () => {
    router.push('/login')
}
</script>

<style scoped>
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
    width: 100%;
    max-width: 400px;
    padding: 40px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.register-box h1 {
    text-align: center;
    margin-bottom: 30px;
    font-size: 28px;
    color: #333;
    font-weight: bold;
}
</style>