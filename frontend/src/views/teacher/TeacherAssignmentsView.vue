<script setup>
import { onMounted, ref, computed, watch } from "vue";
import AssignmentTable from "@/components/AssignmentTable.vue";
import Pagination from "@/components/Pagination.vue";
import http from "@/net/index.js";
import { ElMessage } from "element-plus";
import { Refresh } from "@element-plus/icons-vue";
import SearchInput from "@/components/SearchInput.vue";

const activeTab = ref("submissions"); // submissions | published
const submissions = ref([]);
const published = ref([]);
const loading = ref(false);
const submissionsPage = ref(1);
const submissionsPageSize = ref(10);
const publishedPage = ref(1);
const publishedPageSize = ref(10);
const createVisible = ref(false);
const gradeVisible = ref(false);
const searchKeyword = ref("");

const courses = ref([]);
const createForm = ref({
  title: "",
  content: "",
  courseId: null,
  deadline: "",
});
const gradeForm = ref({
  submissionId: null,
  assignmentTitle: "",
  assignmentContent: "",
  studentName: "",
  submitContent: "",
  score: null,
  comment: "",
});

const filteredSubmissions = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase();
  if (!keyword) return submissions.value;
  return submissions.value.filter((s) => {
    const text = `${s.title} ${s.subject || ""} ${
      s.student || ""
    }`.toLowerCase();
    return text.includes(keyword);
  });
});

const filteredPublished = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase();
  if (!keyword) return published.value;
  return published.value.filter((p) => {
    const text = `${p.title} ${p.subject || ""} ${
      p.content || ""
    }`.toLowerCase();
    return text.includes(keyword);
  });
});

const pagedSubmissions = computed(() => {
  const start = (submissionsPage.value - 1) * submissionsPageSize.value;
  return filteredSubmissions.value.slice(
    start,
    start + submissionsPageSize.value
  );
});

const totalSubmissions = computed(() => filteredSubmissions.value.length);

const pagedPublished = computed(() => {
  const start = (publishedPage.value - 1) * publishedPageSize.value;
  return filteredPublished.value.slice(start, start + publishedPageSize.value);
});

const totalPublished = computed(() => filteredPublished.value.length);

const loadSubmissions = async () => {
  loading.value = true;
  try {
    const data = await http.get("/education/submissions");
    submissions.value = data.map((item) => ({
      id: item.submissionId,
      title: item.assignmentTitle,
      subject:
        item.courseTitle ||
        (item.courseId ? `课程 #${item.courseId}` : "未关联课程"),
      assignmentContent: item.assignmentContent,
      student: item.studentName,
      submitTime: item.submitTime || "--",
      status: item.graded ? "已评分" : "待评分",
      score: item.score,
      submitContent: item.submitContent,
    }));
  } catch (e) {
    ElMessage.error("获取提交列表失败");
  } finally {
    loading.value = false;
  }
};

const loadPublished = async () => {
  loading.value = true;
  try {
    const data = await http.get("/education/assignments");
    published.value = data.map((a) => ({
      id: a.id,
      title: a.title,
      subject:
        a.courseTitle || (a.courseId ? `课程 #${a.courseId}` : "未关联课程"),
      content: a.content,
      deadline: a.deadline || "--",
    }));
  } catch (e) {
    ElMessage.error("获取发布列表失败");
  } finally {
    loading.value = false;
  }
};

const loadData = async () => {
  if (activeTab.value === "submissions") {
    await loadSubmissions();
  } else {
    await loadPublished();
  }
};

const onTabChange = (name) => {
  activeTab.value = name;
  if (name === "submissions") {
    submissionsPage.value = 1;
  } else {
    publishedPage.value = 1;
  }
  loadData();
};

const openCreate = () => {
  createVisible.value = true;
  if (!courses.value.length) {
    http
      .get("/education/courses")
      .then((data) => {
        courses.value = data;
      })
      .catch(() => ElMessage.error("获取学科失败"));
  }
};

const submitCreate = async () => {
  try {
    await http.post("/education/assignments", createForm.value);
    ElMessage.success("创建成功");
    createVisible.value = false;
    await loadPublished();
  } catch (e) {
    ElMessage.error("创建失败");
  }
};

const openGrade = (row) => {
  gradeForm.value = {
    submissionId: row.id,
    assignmentTitle: row.title,
    assignmentContent: row.assignmentContent || "",
    studentName: row.student,
    submitContent: row.submitContent || "",
    score: row.score,
    comment: "",
  };
  gradeVisible.value = true;
};

const submitGrade = async () => {
  try {
    await http.patch(`/education/submissions/${gradeForm.value.submissionId}`, {
      score: gradeForm.value.score,
      comment: gradeForm.value.comment,
    });
    ElMessage.success("评分成功");
    gradeVisible.value = false;
    await loadSubmissions();
  } catch (e) {
    ElMessage.error("评分失败");
  }
};

watch(searchKeyword, () => {
  if (activeTab.value === "submissions") {
    submissionsPage.value = 1;
  } else {
    publishedPage.value = 1;
  }
});

onMounted(() => {
  loadData();
});
</script>

<template>
  <div class="page">
    <section class="card header-card">
      <div>
        <h2>作业列表</h2>
      </div>
      <el-space>
        <SearchInput
          v-model="searchKeyword"
          placeholder="搜索标题/学科/学生"
          style="width: 240px"
        />
        <el-button :icon="Refresh" :loading="loading" @click="loadData"
          >刷新</el-button
        >
        <el-button type="primary" @click="openCreate">新建作业</el-button>
        <el-radio-group v-model="activeTab" size="large" @change="onTabChange">
          <el-radio-button label="submissions">待批改提交</el-radio-button>
          <el-radio-button label="published">已发布作业</el-radio-button>
        </el-radio-group>
      </el-space>
    </section>

    <section>
      <AssignmentTable
        v-if="activeTab === 'submissions'"
        :assignments="pagedSubmissions"
        :loading="loading"
        :show-student="true"
        :row-key="(row) => row.id"
      >
        <template #actions="{ row }">
          <el-button size="small" type="primary" @click="openGrade(row)"
            >评分</el-button
          >
        </template>
        <template #footer>
          <Pagination
            :total="totalSubmissions"
            v-model:current-page="submissionsPage"
            v-model:page-size="submissionsPageSize"
          />
        </template>
      </AssignmentTable>
      <AssignmentTable
        v-else
        :assignments="pagedPublished"
        :loading="loading"
        :show-student="false"
        :show-status="false"
        :show-score="false"
        :row-key="(row) => row.id"
      >
        <template #extra-columns>
          <el-table-column prop="content" label="作业内容" min-width="220" />
          <el-table-column prop="deadline" label="截止时间" min-width="180" />
        </template>
        <template #footer>
          <Pagination
            :total="totalPublished"
            v-model:current-page="publishedPage"
            v-model:page-size="publishedPageSize"
          />
        </template>
      </AssignmentTable>
    </section>

    <el-dialog v-model="createVisible" title="新建作业" width="520px">
      <el-form label-position="top" :model="createForm">
        <el-form-item label="标题">
          <el-input v-model="createForm.title" placeholder="输入标题" />
        </el-form-item>
        <el-form-item label="作业内容">
          <el-input
            type="textarea"
            :rows="4"
            v-model="createForm.content"
            placeholder="输入作业内容"
          />
        </el-form-item>
        <el-form-item label="所属学科">
          <el-select
            v-model="createForm.courseId"
            placeholder="请选择学科"
            style="width: 100%"
          >
            <el-option
              v-for="c in courses"
              :key="c.id"
              :label="c.title"
              :value="c.id"
            />
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
          <el-input
            type="textarea"
            :rows="3"
            v-model="gradeForm.assignmentContent"
            disabled
          />
        </el-form-item>
        <el-form-item label="学生答案">
          <el-input
            type="textarea"
            :rows="4"
            v-model="gradeForm.submitContent"
            disabled
          />
        </el-form-item>
        <el-form-item label="老师评价">
          <el-input
            type="textarea"
            :rows="3"
            v-model="gradeForm.comment"
            placeholder="填写评价"
          />
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
