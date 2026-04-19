<template>
  <div class="page">
    <section class="card header-card">
      <div>
        <h2>学科管理</h2>
      </div>
      <el-space>
        <SearchInput
          v-model="searchKeyword"
          placeholder="搜索学科名称"
          style="width: 220px"
        />
        <el-button :icon="Refresh" :loading="loading" @click="loadSubjects"
          >刷新</el-button
        >
        <el-button type="primary" @click="openCreate">新建学科</el-button>
      </el-space>
    </section>

    <TableShell :data="pagedSubjects" :loading="loading">
      <el-table-column prop="name" label="学科名称" min-width="160" />
      <el-table-column
        prop="assignmentCount"
        label="已发布作业数量"
        min-width="180"
      />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openEdit(row)"
            >编辑</el-button
          >
          <el-popconfirm title="确认删除该学科？" @confirm="removeCourse(row)">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
      <template #footer>
        <Pagination
          :total="filteredSubjects.length"
          :page-size="pageSize"
          v-model:current-page="currentPage"
        />
      </template>
    </TableShell>

    <el-dialog v-model="createVisible" title="新建学科" width="420px">
      <el-form :model="createForm" label-position="top">
        <el-form-item label="学科名称">
          <el-input v-model="createForm.title" placeholder="请输入学科名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" title="编辑学科" width="420px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="学科名称">
          <el-input v-model="editForm.title" placeholder="请输入学科名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import http from "@/net/index.js";
import { ElMessage } from "element-plus";
import { Refresh } from "@element-plus/icons-vue";
import Pagination from "@/components/Pagination.vue";
import TableShell from "@/components/TableShell.vue";
import SearchInput from "@/components/SearchInput.vue";

const subjects = ref([]);
const loading = ref(false);
const createVisible = ref(false);
const createForm = ref({ title: "" });
const editVisible = ref(false);
const editForm = ref({ id: null, title: "" });
const currentPage = ref(1);
const pageSize = 10;
const searchKeyword = ref("");

const filteredSubjects = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase();
  if (!keyword) return subjects.value;
  return subjects.value.filter((s) => s.name.toLowerCase().includes(keyword));
});

const pagedSubjects = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredSubjects.value.slice(start, start + pageSize);
});

const loadSubjects = async () => {
  loading.value = true;
  try {
    const data = await http.get("/education/courses?includeAssignmentCount=true");
    subjects.value = data.map((c) => ({
      id: c.id,
      name: c.title,
      assignmentCount: c.assignmentCount,
    }));
  } catch (e) {
    ElMessage.error("获取学科失败");
  } finally {
    loading.value = false;
  }
};

const openCreate = () => {
  createForm.value = { title: "" };
  createVisible.value = true;
};

const submitCreate = async () => {
  try {
    await http.post("/education/courses", { title: createForm.value.title });
    ElMessage.success("创建成功");
    createVisible.value = false;
    await loadSubjects();
  } catch (e) {
    ElMessage.error("创建失败");
  }
};

const openEdit = (row) => {
  editForm.value = { id: row.id, title: row.name };
  editVisible.value = true;
};

const submitEdit = async () => {
  try {
    await http.put(`/education/courses/${editForm.value.id}`, {
      title: editForm.value.title,
    });
    ElMessage.success("更新成功");
    editVisible.value = false;
    await loadSubjects();
  } catch (e) {
    ElMessage.error("更新失败");
  }
};

const removeCourse = async (row) => {
  try {
    await http.delete(`/education/courses/${row.id}`);
    ElMessage.success("删除成功");
    await loadSubjects();
  } catch (e) {
    ElMessage.error("删除失败");
  }
};

watch(searchKeyword, () => {
  currentPage.value = 1;
});

onMounted(() => {
  loadSubjects();
});
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
