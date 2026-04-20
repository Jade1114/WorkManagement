<template>
  <div class="page">
    <PageHeader
      eyebrow="Focus Queue"
      title="未提交作业"
      description="只保留下一步需要完成的任务，开始写之前先看清截止时间。"
      variant="student"
      metric-label="待完成"
      :metric-value="totalAssignments"
    >
      <template #actions>
        <SearchInput v-model="searchKeyword" placeholder="搜索标题/学科" style="width: 240px" />
        <el-button
          :icon="Refresh"
          :loading="loading"
          @click="loadPendingAssignments"
          >刷新</el-button
        >
      </template>
    </PageHeader>

    <AssignmentTable
      :assignments="pagedAssignments"
      :loading="loading"
      :row-key="(row) => row.id"
      :show-status="false"
      :show-score="false"
    >
      <template #extra-columns>
        <el-table-column prop="content" label="作业要求" min-width="300" />
        <el-table-column prop="deadline" label="到期时间" min-width="120" />
      </template>
      <template #actions="{ row }">
        <el-tag v-if="row.isExpired" type="info" size="small">已过期</el-tag>
        <el-button
          v-else
          type="primary"
          size="small"
          @click="openSubmit(row)"
          >提交</el-button
        >
      </template>
      <template #footer>
        <Pagination
          :total="totalAssignments"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
        />
      </template>
    </AssignmentTable>

    <el-dialog v-model="submitVisible" title="提交作业" width="520px">
      <p style="margin-bottom: 8px">{{ currentAssignment?.title }}</p>
      <el-input
        v-model="submitContent"
        type="textarea"
        :rows="6"
        placeholder="请输入提交内容"
      />
      <template #footer>
        <el-button @click="submitVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit"
          >提交</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import http from "@/net/index.js";
import AssignmentTable from "@/components/AssignmentTable.vue";
import Pagination from "@/components/Pagination.vue";
import SearchInput from "@/components/SearchInput.vue";
import { ElMessage } from "element-plus";
import { Refresh } from "@element-plus/icons-vue";
import PageHeader from "@/components/PageHeader.vue";

const assignments = ref([]);
const loading = ref(false);
const submitVisible = ref(false);
const submitLoading = ref(false);
const submitContent = ref("");
const currentAssignment = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref("");

const filteredAssignments = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase();
  if (!keyword) return assignments.value;
  return assignments.value.filter((a) => {
    const text = `${a.title} ${a.subject || ""}`.toLowerCase();
    return text.includes(keyword);
  });
});

const pagedAssignments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredAssignments.value.slice(start, start + pageSize.value);
});

const totalAssignments = computed(() => filteredAssignments.value.length);

const loadPendingAssignments = async () => {
  loading.value = true;
  try {
    const data = await http.get("/education/assignments?status=pending");
    const now = Date.now();
    assignments.value = data.map((a) => ({
      id: a.id,
      title: a.title,
      content: a.content,
      subject:
        a.courseTitle || (a.courseId ? `课程 #${a.courseId}` : "未关联课程"),
      deadline: a.deadline,
      isExpired: a.deadline ? new Date(a.deadline).getTime() < now : false,
    }));
  } catch (e) {
    ElMessage.error("获取未提交作业失败");
  } finally {
    loading.value = false;
  }
};

const openSubmit = (row) => {
  currentAssignment.value = row;
  submitContent.value = "";
  submitVisible.value = true;
};

const handleSubmit = async () => {
  if (!currentAssignment.value) return;
  submitLoading.value = true;
  try {
    await http.post("/education/submissions", {
      assignmentId: currentAssignment.value.id,
      content: submitContent.value,
    });
    ElMessage.success("提交成功");
    submitVisible.value = false;
    await loadPendingAssignments();
  } catch (e) {
    ElMessage.error("提交失败");
  } finally {
    submitLoading.value = false;
  }
};

onMounted(() => {
  loadPendingAssignments();
});

watch(searchKeyword, () => {
  currentPage.value = 1;
});
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.muted {
  color: var(--color-text-tertiary);
  margin: 4px 0 0;
}

.quick-actions {
  padding: var(--spacing-l);
}

.actions {
  display: flex;
  gap: var(--spacing-m);
  margin-top: var(--spacing-m);
}
</style>
