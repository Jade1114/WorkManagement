<script setup>
import { onMounted, ref, computed } from 'vue'
import TeacherAssignmentTable from '@/components/TeacherAssignmentTable.vue'
import Pagination from '@/components/Pagination.vue'
import TeacherNav from '@/components/TeacherNav.vue'
import http from '@/net/index.js'
import { ElMessage } from 'element-plus'

const activeTab = ref('submissions') // submissions | published
const submissions = ref([])
const published = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(5)
const createVisible = ref(false)
const gradeVisible = ref(false)

const courses = ref([])
const createForm = ref({
  title: '',
  content: '',
  courseId: null,
  deadline: '',
})
const gradeForm = ref({
  submissionId: null,
  assignmentTitle: '',
  assignmentContent: '',
  studentName: '',
  submitContent: '',
  score: null,
  comment: '',
})

const pagedSubmissions = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return submissions.value.slice(start, start + pageSize.value)
})

const totalSubmissions = computed(() => submissions.value.length)

const loadSubmissions = async () => {
  loading.value = true
  try {
    const data = await http.get('/submissions/all')
    submissions.value = data.map(item => ({
      id: item.submissionId,
      title: item.assignmentTitle,
      subject: item.courseTitle || (item.courseId ? `课程 #${item.courseId}` : '未关联课程'),
      assignmentContent: item.assignmentContent,
      student: item.studentName,
      submitTime: item.submitTime || '--',
      status: item.graded ? '已评分' : '待评分',
      score: item.score,
      submitContent: item.submitContent,
    }))
  } catch (e) {
    ElMessage.error('获取提交列表失败')
  } finally {
    loading.value = false
  }
}

const loadPublished = async () => {
  loading.value = true
  try {
    const data = await http.get('/assignments/all')
    published.value = data.map(a => ({
      id: a.id,
      title: a.title,
      subject: a.courseTitle || (a.courseId ? `课程 #${a.courseId}` : '未关联课程'),
      content: a.content,
      deadline: a.deadline || '--',
    }))
  } catch (e) {
    ElMessage.error('获取发布列表失败')
  } finally {
    loading.value = false
  }
}

const loadData = async () => {
  if (activeTab.value === 'submissions') {
    await loadSubmissions()
  } else {
    await loadPublished()
  }
}

const onTabChange = (name) => {
  activeTab.value = name
  currentPage.value = 1
  loadData()
}

const openCreate = () => {
  createVisible.value = true
  if (!courses.value.length) {
    http.get('/courses/get')
      .then(data => {
        courses.value = data
      })
      .catch(() => ElMessage.error('获取学科失败'))
  }
}

const submitCreate = async () => {
  try {
    await http.post('/assignments/create', createForm.value)
    ElMessage.success('创建成功')
    createVisible.value = false
    await loadPublished()
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

const openGrade = (row) => {
  gradeForm.value = {
    submissionId: row.id,
    assignmentTitle: row.title,
    assignmentContent: row.assignmentContent || '',
    studentName: row.student,
    submitContent: row.submitContent || '',
    score: row.score,
    comment: '',
  }
  gradeVisible.value = true
}

const submitGrade = async () => {
  try {
    await http.post('/submissions/grade', {
      submissionId: gradeForm.value.submissionId,
      score: gradeForm.value.score,
      comment: gradeForm.value.comment,
    })
    ElMessage.success('评分成功')
    gradeVisible.value = false
    await loadSubmissions()
  } catch (e) {
    ElMessage.error('评分失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="page">
    <TeacherNav />

    <section class="card header-card">
      <div>
        <h2>作业列表</h2>
      </div>
      <el-space>
        <el-button type="primary" @click="openCreate">新建作业</el-button>
        <el-radio-group v-model="activeTab" size="large" @change="onTabChange">
          <el-radio-button label="submissions">待批改提交</el-radio-button>
          <el-radio-button label="published">已发布作业</el-radio-button>
        </el-radio-group>
      </el-space>
    </section>

    <section class="card">
      <TeacherAssignmentTable
        v-if="activeTab === 'submissions'"
        :assignments="pagedSubmissions"
        v-loading="loading"
        :show-student="true"
        @grade="openGrade"
      />
      <el-table
        v-else
        :data="published"
        stripe
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="subject" label="所属学科" min-width="140" />
        <el-table-column prop="content" label="作业内容" min-width="220" />
        <el-table-column prop="deadline" label="截止时间" min-width="180" />
      </el-table>
      <Pagination
        v-if="activeTab === 'submissions'"
        :total="totalSubmissions"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
      />
    </section>

    <el-dialog v-model="createVisible" title="新建作业" width="520px">
      <el-form label-position="top" :model="createForm">
        <el-form-item label="标题">
          <el-input v-model="createForm.title" placeholder="输入标题" />
        </el-form-item>
        <el-form-item label="作业内容">
          <el-input type="textarea" :rows="4" v-model="createForm.content" placeholder="输入作业内容" />
        </el-form-item>
        <el-form-item label="所属学科">
          <el-select v-model="createForm.courseId" placeholder="请选择学科" style="width: 100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.title" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker
            v-model="createForm.deadline"
            type="datetime"
            placeholder="选择截止时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="gradeVisible" title="评分" width="540px">
      <el-form label-position="top">
        <el-form-item label="作业标题">
          <el-input v-model="gradeForm.assignmentTitle" disabled />
        </el-form-item>
        <el-form-item label="作业内容">
          <el-input type="textarea" :rows="3" v-model="gradeForm.assignmentContent" disabled />
        </el-form-item>
        <el-form-item label="学生答案">
          <el-input type="textarea" :rows="4" v-model="gradeForm.submitContent" disabled />
        </el-form-item>
        <el-form-item label="老师评价">
          <el-input type="textarea" :rows="3" v-model="gradeForm.comment" placeholder="填写评价" />
        </el-form-item>
        <el-form-item label="分数">
          <el-input-number v-model="gradeForm.score" :min="0" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="gradeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGrade">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

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
</style>
