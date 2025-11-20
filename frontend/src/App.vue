<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const API_BASE = '/api'
const storedToken = localStorage.getItem('token')
const token = ref(storedToken || '')
const user = ref(null)
const statusMessage = ref('')

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', password: '' })
const changePasswordForm = reactive({ oldPassword: '', newPassword: '' })

const courses = ref([])
const newCourseTitle = ref('')
const selectedCourseId = ref('')

const assignments = ref([])
const newAssignment = reactive({ title: '', content: '', deadline: '' })
const selectedAssignment = ref(null)
const submissionContent = ref('')
const mySubmission = ref(null)
const submissionList = ref([])

const isTeacher = computed(() => user.value?.role === 'teacher')
const isStudent = computed(() => user.value?.role === 'student')

const resetStatus = () => {
  statusMessage.value = ''
}

const apiRequest = async (path, options = {}) => {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) }
  if (token.value) {
    headers.Authorization = `Bearer ${token.value}`
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  })

  const data = await response.json().catch(() => ({}))
  if (!response.ok || (data.code && data.code !== 200)) {
    throw new Error(data.message || '请求失败')
  }
  return data.data ?? data
}

const loadCurrentUser = async () => {
  if (!token.value) return
  try {
    user.value = await apiRequest('/users/me', { method: 'GET' })
  } catch (error) {
    statusMessage.value = error.message
  }
}

const login = async () => {
  resetStatus()
  try {
    const data = await apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify(loginForm),
    })
    token.value = data.token
    localStorage.setItem('token', data.token)
    user.value = data.user
    statusMessage.value = '登录成功'
    await loadCourses()
  } catch (error) {
    statusMessage.value = error.message
  }
}

const register = async () => {
  resetStatus()
  try {
    await apiRequest('/auth/register', {
      method: 'POST',
      body: JSON.stringify(registerForm),
    })
    statusMessage.value = '注册成功，请登录'
    registerForm.username = ''
    registerForm.password = ''
  } catch (error) {
    statusMessage.value = error.message
  }
}

const logout = async () => {
  resetStatus()
  try {
    await apiRequest('/auth/logout', { method: 'POST' })
  } catch (error) {
    // 即使退出失败也清除本地信息
    console.warn(error)
  }
  token.value = ''
  user.value = null
  localStorage.removeItem('token')
  assignments.value = []
  selectedCourseId.value = ''
  selectedAssignment.value = null
  mySubmission.value = null
  submissionList.value = []
  statusMessage.value = '已退出登录'
}

const changePassword = async () => {
  resetStatus()
  try {
    await apiRequest('/users/changePassword', {
      method: 'PUT',
      body: JSON.stringify(changePasswordForm),
    })
    statusMessage.value = '密码修改成功'
    changePasswordForm.oldPassword = ''
    changePasswordForm.newPassword = ''
  } catch (error) {
    statusMessage.value = error.message
  }
}

const loadCourses = async () => {
  if (!token.value) return
  try {
    courses.value = await apiRequest('/courses/get', { method: 'GET' })
  } catch (error) {
    statusMessage.value = error.message
  }
}

const createCourse = async () => {
  resetStatus()
  if (!newCourseTitle.value.trim()) return
  try {
    const created = await apiRequest('/courses/create', {
      method: 'POST',
      body: JSON.stringify({ title: newCourseTitle.value }),
    })
    courses.value.push(created)
    newCourseTitle.value = ''
    statusMessage.value = '课程创建成功'
  } catch (error) {
    statusMessage.value = error.message
  }
}

const selectCourse = async (courseId) => {
  selectedCourseId.value = courseId
  selectedAssignment.value = null
  assignments.value = []
  mySubmission.value = null
  submissionList.value = []
  submissionContent.value = ''
  await loadAssignments(courseId)
}

const loadAssignments = async (courseId) => {
  if (!token.value || !courseId) return
  try {
    assignments.value = await apiRequest(`/assignments/list?courseId=${courseId}`, {
      method: 'GET',
    })
  } catch (error) {
    statusMessage.value = error.message
  }
}

const createAssignment = async () => {
  resetStatus()
  if (!selectedCourseId.value) {
    statusMessage.value = '请先选择课程'
    return
  }
  if (!newAssignment.title.trim()) return
  try {
    const payload = {
      courseId: Number(selectedCourseId.value),
      title: newAssignment.title,
      content: newAssignment.content || undefined,
      deadline: newAssignment.deadline || undefined,
    }
    const created = await apiRequest('/assignments/create', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
    assignments.value.push(created)
    newAssignment.title = ''
    newAssignment.content = ''
    newAssignment.deadline = ''
    statusMessage.value = '作业创建成功'
  } catch (error) {
    statusMessage.value = error.message
  }
}

const selectAssignment = async (assignment) => {
  selectedAssignment.value = assignment
  submissionContent.value = ''
  mySubmission.value = null
  submissionList.value = []
  await fetchSubmissionData(assignment.id)
}

const fetchSubmissionData = async (assignmentId) => {
  if (isStudent.value) {
    try {
      mySubmission.value = await apiRequest(`/submissions/my?assignmentId=${assignmentId}`, {
        method: 'GET',
      })
    } catch (error) {
      mySubmission.value = null
      // 可能尚未提交
    }
  }

  if (isTeacher.value) {
    try {
      submissionList.value = await apiRequest(`/submissions/list?assignmentId=${assignmentId}`, {
        method: 'GET',
      })
    } catch (error) {
      submissionList.value = []
      statusMessage.value = error.message
    }
  }
}

const submitAssignment = async () => {
  resetStatus()
  if (!selectedAssignment.value) return
  try {
    await apiRequest('/submissions/submit', {
      method: 'POST',
      body: JSON.stringify({
        assignmentId: selectedAssignment.value.id,
        content: submissionContent.value,
      }),
    })
    statusMessage.value = '提交成功'
    submissionContent.value = ''
    await fetchSubmissionData(selectedAssignment.value.id)
  } catch (error) {
    statusMessage.value = error.message
  }
}

const gradeSubmission = async (submissionId, score, comment) => {
  resetStatus()
  try {
    await apiRequest('/submissions/grade', {
      method: 'POST',
      body: JSON.stringify({ submissionId, score: Number(score), comment }),
    })
    statusMessage.value = '批改成功'
    if (selectedAssignment.value) {
      await fetchSubmissionData(selectedAssignment.value.id)
    }
  } catch (error) {
    statusMessage.value = error.message
  }
}

onMounted(async () => {
  if (token.value) {
    await loadCurrentUser()
    await loadCourses()
  }
})
</script>

<template>
  <div class="page">
    <header class="page__header">
      <div>
        <h1>课程作业管理平台</h1>
        <p class="page__subtitle">支持学生提交与老师批改的前端界面</p>
      </div>
      <div v-if="user" class="user-info">
        <div class="pill">{{ user.username }} · {{ user.role }}</div>
        <button class="secondary" @click="logout">退出登录</button>
      </div>
    </header>

    <section class="panel">
      <div class="panel__header">
        <h2>账号中心</h2>
        <span class="helper">登录后才能操作课程和作业</span>
      </div>
      <div class="grid two-columns">
        <div>
          <h3>登录</h3>
          <div class="form">
            <label>用户名</label>
            <input v-model="loginForm.username" placeholder="学号或账号" />
            <label>密码</label>
            <input v-model="loginForm.password" type="password" placeholder="密码" />
            <button @click="login">登录</button>
          </div>
        </div>
        <div>
          <h3>学生注册</h3>
          <div class="form">
            <label>用户名</label>
            <input v-model="registerForm.username" placeholder="学号" />
            <label>密码</label>
            <input v-model="registerForm.password" type="password" placeholder="密码" />
            <button class="secondary" @click="register">注册</button>
          </div>
          <div class="spacer"></div>
          <h3>修改密码</h3>
          <div class="form">
            <label>旧密码</label>
            <input v-model="changePasswordForm.oldPassword" type="password" />
            <label>新密码</label>
            <input v-model="changePasswordForm.newPassword" type="password" />
            <button class="secondary" :disabled="!user" @click="changePassword">修改密码</button>
          </div>
        </div>
      </div>
      <p v-if="statusMessage" class="status">{{ statusMessage }}</p>
    </section>

    <section class="panel" v-if="token">
      <div class="panel__header">
        <h2>课程列表</h2>
        <div v-if="isTeacher" class="inline-form">
          <input v-model="newCourseTitle" placeholder="新课程标题" />
          <button @click="createCourse">创建课程</button>
        </div>
      </div>
      <div v-if="courses.length" class="chip-group">
        <button
          v-for="course in courses"
          :key="course.id"
          class="chip"
          :class="{ active: course.id === selectedCourseId }"
          @click="selectCourse(course.id)
          "
        >
          {{ course.title }}
        </button>
      </div>
      <p v-else class="muted">暂无课程，请先创建或联系老师。</p>
    </section>

    <section class="panel" v-if="selectedCourseId">
      <div class="panel__header">
        <div>
          <h2>作业列表</h2>
          <p class="helper">当前课程 ID：{{ selectedCourseId }}</p>
        </div>
        <div v-if="isTeacher" class="inline-form">
          <input v-model="newAssignment.title" placeholder="作业标题" />
          <input v-model="newAssignment.deadline" placeholder="截止时间（可选）" />
          <input v-model="newAssignment.content" placeholder="作业内容（可选）" />
          <button @click="createAssignment">发布作业</button>
        </div>
      </div>

      <div v-if="assignments.length" class="card-list">
        <article
          v-for="assignment in assignments"
          :key="assignment.id"
          class="card"
          @click="selectAssignment(assignment)
          "
        >
          <div class="card__header">
            <h3>{{ assignment.title }}</h3>
            <span class="tag">ID {{ assignment.id }}</span>
          </div>
          <p class="muted">{{ assignment.content || '暂无内容' }}</p>
          <p v-if="assignment.deadline" class="deadline">截止：{{ assignment.deadline }}</p>
        </article>
      </div>
      <p v-else class="muted">该课程还没有作业。</p>
    </section>

    <section class="panel" v-if="selectedAssignment">
      <div class="panel__header">
        <div>
          <h2>作业详情</h2>
          <p class="helper">{{ selectedAssignment.title }}</p>
        </div>
      </div>
      <div class="grid two-columns">
        <div>
          <h3>作业要求</h3>
          <p>{{ selectedAssignment.content || '无具体要求' }}</p>
          <p v-if="selectedAssignment.deadline" class="deadline">截止时间：{{ selectedAssignment.deadline }}</p>

          <div v-if="isStudent" class="form">
            <label>提交内容</label>
            <textarea
              v-model="submissionContent"
              rows="5"
              placeholder="输入作业内容或链接"
            ></textarea>
            <button @click="submitAssignment">提交作业</button>
            <div v-if="mySubmission" class="info-box">
              <p>我的提交：{{ mySubmission.content }}</p>
              <p v-if="mySubmission.graded">分数：{{ mySubmission.score }}，评语：{{ mySubmission.comment }}</p>
              <p v-else class="muted">老师尚未批改</p>
            </div>
          </div>
        </div>
        <div v-if="isTeacher">
          <h3>学生提交</h3>
          <div v-if="submissionList.length" class="submission-list">
            <div v-for="submission in submissionList" :key="submission.id" class="submission-card">
              <div class="card__header">
                <strong>{{ submission.studentUsername }}</strong>
                <span class="tag" :class="{ success: submission.graded }">
                  {{ submission.graded ? '已批改' : '待批改' }}
                </span>
              </div>
              <p class="muted">{{ submission.content }}</p>
              <div class="form inline">
                <label>分数</label>
                <input type="number" min="0" max="100" v-model.number="submission.score" />
                <label>评语</label>
                <input v-model="submission.comment" placeholder="评语" />
                <button @click="gradeSubmission(submission.id, submission.score, submission.comment)">
                  保存评分
                </button>
              </div>
            </div>
          </div>
          <p v-else class="muted">暂无提交记录。</p>
        </div>
      </div>
    </section>
  </div>
</template>
