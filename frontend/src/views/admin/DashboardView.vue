<template>
  <div class="page">
    <section class="card header-card">
      <div>
        <h2>仪表盘</h2>
      </div>
      <el-button :icon="Refresh" :loading="loading" @click="manualRefresh"
        >刷新</el-button
      >
    </section>

    <section class="stats">
      <div class="card stat" v-for="item in statCards" :key="item.label">
        <p class="label">{{ item.label }}</p>
        <p class="value">{{ item.value }}</p>
      </div>
    </section>

    <section class="card data-screen">
      <div class="chart" ref="courseChartRef"></div>
      <div class="chart" ref="submissionTrendRef"></div>
      <div class="chart" ref="submissionStatusRef"></div>
    </section>

    <section class="card carousel-card">
      <el-carousel
        height="260px"
        indicator-position="none"
        arrow="never"
        :interval="6000"
      >
        <el-carousel-item>
          <div class="carousel-block">
            <div class="carousel-title">提交作业最多的学生</div>
            <el-table :data="topSubmitters" size="small" border height="180">
              <el-table-column prop="student" label="学生" min-width="120" />
              <el-table-column prop="count" label="提交次数" width="120" />
              <el-table-column
                prop="lastSubmit"
                label="最近提交"
                min-width="140"
              />
            </el-table>
          </div>
        </el-carousel-item>
        <el-carousel-item>
          <div class="carousel-block">
            <div class="carousel-title">最近发布的作业</div>
            <el-table
              :data="recentAssignments"
              size="small"
              border
              height="180"
            >
              <el-table-column prop="title" label="标题" min-width="140" />
              <el-table-column prop="course" label="学科" min-width="120" />
              <el-table-column
                prop="deadline"
                label="截止时间"
                min-width="140"
              />
            </el-table>
          </div>
        </el-carousel-item>
        <el-carousel-item>
          <div class="carousel-block">
            <div class="carousel-title">最近提交</div>
            <el-table
              :data="recentSubmissions"
              size="small"
              border
              height="180"
            >
              <el-table-column prop="student" label="学生" min-width="100" />
              <el-table-column prop="assignment" label="作业" min-width="140" />
              <el-table-column prop="course" label="学科" min-width="120" />
              <el-table-column
                prop="submitTime"
                label="提交时间"
                min-width="140"
              />
            </el-table>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import { Refresh } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import http from "@/net/index.js";
import * as echarts from "echarts";

const loading = ref(false);
const topSubmitters = ref([]);
const statCards = ref([
  { label: "用户数", value: 0 },
  { label: "学科数", value: 0 },
  { label: "作业数", value: 0 },
  { label: "待批改", value: 0 },
]);

const recentAssignments = ref([]);

const recentSubmissions = ref([]);

const dataScreen = ref({
  assignmentsByCourse: [],
  submissionStatus: { graded: 0, pending: 0 },
  submissionsByDate: [],
});

const courseChartRef = ref(null);
const submissionTrendRef = ref(null);
const submissionStatusRef = ref(null);
let courseChart;
let submissionTrendChart;
let submissionStatusChart;
let timer = null;

const loadStats = async () => {
  loading.value = true;
  try {
    const data = await http.get("/education/teachers/me/stats");
    statCards.value = [
      { label: "用户数", value: data.students ? data.students + 1 : 0 },
      { label: "学科数", value: data.courses ?? 0 },
      { label: "作业数", value: data.assignments ?? 0 },
      { label: "待批改", value: data.pendingSubmissions ?? 0 },
    ];
  } catch (e) {
    ElMessage.error("获取统计失败，使用示例数据");
    statCards.value = [
      { label: "用户数", value: 20 },
      { label: "学科数", value: 5 },
      { label: "作业数", value: 18 },
      { label: "待批改", value: 3 },
    ];
  } finally {
    loading.value = false;
  }
};

const loadRecentAssignments = async () => {
  try {
    const data = await http.get("/education/teachers/me/recent/assignments");
    recentAssignments.value = (data || []).map((a) => ({
      title: a.title,
      course:
        a.courseTitle || (a.courseId ? `课程 #${a.courseId}` : "未关联课程"),
      deadline: a.deadline,
      createdAt: a.createdAt,
    }));
  } catch (e) {
    ElMessage.error("获取最近作业失败");
  }
};

const loadRecentSubmissions = async () => {
  try {
    const data = await http.get("/education/teachers/me/recent/submissions");
    recentSubmissions.value = (data || []).map((s) => ({
      student: s.studentName,
      assignment: s.assignmentTitle,
      status: s.graded ? "已批改" : "待批改",
      score: s.score,
      submitTime: s.submitTime,
      course: s.courseTitle,
    }));
  } catch (e) {
    ElMessage.error("获取最近提交失败");
  }
};

const loadTopSubmitters = async () => {
  try {
    const data = await http.get("/education/teachers/me/top-submitters");
    topSubmitters.value = (data || []).slice(0, 3).map((s) => ({
      student: s.studentName || s.username || `学生 #${s.studentId || ""}`,
      count: s.count ?? s.submissions ?? 0,
      lastSubmit: s.lastSubmit || s.submitTime || "--",
    }));
  } catch (e) {
    // 示例数据兜底
    topSubmitters.value = [
      { student: "student001", count: 8, lastSubmit: "今天 09:15" },
      { student: "student002", count: 6, lastSubmit: "昨天 17:40" },
      { student: "student003", count: 5, lastSubmit: "本周 14:20" },
    ];
  }
};

const loadDataScreen = async () => {
  try {
    const data = await http.get("/education/teachers/me/data-screen");
    dataScreen.value = data || {
      assignmentsByCourse: [],
      submissionStatus: { graded: 0, pending: 0 },
      submissionsByDate: [],
    };
    updateCharts();
  } catch (e) {
    ElMessage.error("获取数据大屏数据失败");
  }
};

const initCharts = () => {
  if (courseChartRef.value && !courseChart) {
    courseChart = echarts.init(courseChartRef.value);
  }
  if (submissionTrendRef.value && !submissionTrendChart) {
    submissionTrendChart = echarts.init(submissionTrendRef.value);
  }
  if (submissionStatusRef.value && !submissionStatusChart) {
    submissionStatusChart = echarts.init(submissionStatusRef.value);
  }
};

const getTextColor = () => {
  const styles = getComputedStyle(document.documentElement);
  return styles.getPropertyValue("--color-text-primary")?.trim() || "#000";
};

const updateCharts = () => {
  if (!courseChart || !submissionTrendChart || !submissionStatusChart) return;

  const textColor = getTextColor();

  const courses = dataScreen.value.assignmentsByCourse || [];
  courseChart.setOption({
    title: {
      text: "各学科作业数",
      textStyle: { color: textColor, fontSize: 14 },
    },
    backgroundColor: "transparent",
    grid: { left: 40, right: 10, top: 40, bottom: 30 },
    xAxis: {
      type: "category",
      data: courses.map((c) => c.courseTitle),
      axisLabel: { color: textColor },
    },
    yAxis: { type: "value", axisLabel: { color: textColor } },
    series: [
      {
        data: courses.map((c) => c.assignments),
        type: "bar",
        itemStyle: { color: "#5B8FF9" },
        barMaxWidth: 32,
      },
    ],
  });

  const dates = (dataScreen.value.submissionsByDate || []).map((i) => i.date);
  const counts = (dataScreen.value.submissionsByDate || []).map((i) => i.count);
  submissionTrendChart.setOption({
    title: {
      text: "最近7天提交量",
      textStyle: { color: textColor, fontSize: 14 },
    },
    backgroundColor: "transparent",
    grid: { left: 40, right: 20, top: 40, bottom: 30 },
    xAxis: { type: "category", data: dates, axisLabel: { color: textColor } },
    yAxis: { type: "value", axisLabel: { color: textColor } },
    series: [
      {
        data: counts,
        type: "line",
        smooth: true,
        areaStyle: { color: "rgba(91, 143, 249, 0.25)" },
        lineStyle: { color: "#5B8FF9" },
        symbol: "circle",
        symbolSize: 6,
      },
    ],
  });

  const { graded = 0, pending = 0 } = dataScreen.value.submissionStatus || {};
  submissionStatusChart.setOption({
    title: {
      text: "提交状态分布",
      textStyle: { color: textColor, fontSize: 14 },
    },
    backgroundColor: "transparent",
    tooltip: { trigger: "item" },
    legend: { bottom: 0, textStyle: { color: textColor } },
    series: [
      {
        name: "提交状态",
        type: "pie",
        radius: ["35%", "60%"],
        data: [
          { value: pending, name: "待批改" },
          { value: graded, name: "已批改" },
        ],
        label: { color: textColor },
      },
    ],
  });
};

const loadAll = async () => {
  await Promise.all([
    loadStats(),
    loadRecentAssignments(),
    loadRecentSubmissions(),
    loadTopSubmitters(),
    loadDataScreen(),
  ]);
};

const resizeCharts = () => {
  courseChart?.resize();
  submissionTrendChart?.resize();
  submissionStatusChart?.resize();
};

const manualRefresh = () => {
  loadAll();
};

onMounted(async () => {
  await nextTick();
  initCharts();
  await loadAll();
  timer = setInterval(loadAll, 5000);
  window.addEventListener("resize", resizeCharts);
  // 再次更新颜色以防主题切换后首次进入
  updateCharts();
});

onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
  window.removeEventListener("resize", resizeCharts);
  courseChart?.dispose();
  submissionTrendChart?.dispose();
  submissionStatusChart?.dispose();
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

.carousel-card {
  padding: var(--spacing-m);
}

.carousel-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  justify-content: center;
  height: 100%;
  padding: var(--spacing-m);
}

.carousel-block {
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 100%;
  padding: var(--spacing-m);
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.16),
    rgba(255, 255, 255, 0.06)
  );
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 14px;
  box-shadow: var(--shadow);
}

.carousel-title {
  font-weight: 700;
  font-size: 18px;
}

.carousel-desc {
  color: var(--color-text-secondary);
}

.carousel-meta {
  color: var(--color-text-tertiary);
  font-size: 12px;
}

.muted {
  color: var(--color-text-tertiary);
  margin: 4px 0 0;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: var(--spacing-m);
}

.stat {
  padding: var(--spacing-m);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.stat:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-strong, 0 8px 24px rgba(0, 0, 0, 0.12));
}

.label {
  color: var(--color-text-tertiary);
  margin: 0 0 6px;
}

.value {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

.data-screen {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--spacing-m);
  padding: var(--spacing-m);
  min-height: 320px;
}

.chart {
  width: 100%;
  height: 320px;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.06),
    rgba(255, 255, 255, 0.02)
  );
  border-radius: 16px;
}
</style>

