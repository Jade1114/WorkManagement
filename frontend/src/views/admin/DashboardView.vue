<template>
  <div class="page">
    <section class="stats">
      <div
        class="card stat"
        v-for="(item, index) in statCards"
        :key="item.label"
        :class="`stat--${index}`"
      >
        <div class="stat-copy">
          <p class="label">{{ item.label }}</p>
          <p class="value">{{ item.value }}</p>
        </div>
        <span class="stat-mark"></span>
      </div>
    </section>

    <section class="data-screen">
      <article class="card chart-card chart-card--wide">
        <div class="section-title">
          <h2>近日提交趋势</h2>
          <p>用变化判断本周节奏</p>
        </div>
        <div class="chart" ref="submissionTrendRef"></div>
      </article>
      <article class="card chart-card">
        <div class="section-title">
          <h2>提交状态分布</h2>
          <p>批改压力是否集中</p>
        </div>
        <div class="chart" ref="submissionStatusRef"></div>
      </article>
      <article class="card chart-card chart-card--course">
        <div class="section-title">
          <h2>各学科作业数</h2>
          <p>观察课程负载</p>
        </div>
        <div class="chart" ref="courseChartRef"></div>
      </article>
    </section>

    <section class="card activity-card">
      <div class="section-toolbar">
        <div class="section-title">
          <h2>最新提交列表</h2>
          <p>明细承接图表判断，方便继续处理。</p>
        </div>
        <div class="section-actions">
          <el-button>筛选</el-button>
          <el-button :icon="Refresh" :loading="loading" @click="manualRefresh">刷新</el-button>
        </div>
      </div>
      <el-table :data="recentSubmissions" border style="width: 100%">
        <el-table-column prop="student" label="学生" min-width="120" />
        <el-table-column prop="assignment" label="作业" min-width="180" />
        <el-table-column prop="course" label="学科" min-width="140" />
        <el-table-column prop="submitTime" label="提交时间" min-width="160" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === '已批改' ? 'success' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
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

const getCssColor = (name, fallback) => {
  const styles = getComputedStyle(document.documentElement);
  return styles.getPropertyValue(name)?.trim() || fallback;
};

const updateCharts = () => {
  if (!courseChart || !submissionTrendChart || !submissionStatusChart) return;

  const textColor = getTextColor();
  const mutedColor = getCssColor("--color-text-tertiary", "#7f847d");
  const primaryColor = getCssColor("--color-primary", "#2f80ed");
  const successColor = getCssColor("--color-success", "#78c257");
  const warningColor = getCssColor("--color-warning", "#f59e0b");

  const courses = dataScreen.value.assignmentsByCourse || [];
  courseChart.setOption({
    title: {
      text: "各学科作业数",
      show: false,
    },
    backgroundColor: "transparent",
    grid: { left: 36, right: 12, top: 16, bottom: 28 },
    xAxis: {
      type: "category",
      data: courses.map((c) => c.courseTitle),
      axisLabel: { color: mutedColor },
      axisLine: { lineStyle: { color: "transparent" } },
      axisTick: { show: false },
    },
    yAxis: {
      type: "value",
      axisLabel: { color: mutedColor },
      splitLine: { lineStyle: { color: "rgba(127, 132, 125, 0.18)" } },
    },
    series: [
      {
        data: courses.map((c) => c.assignments),
        type: "bar",
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: primaryColor },
              { offset: 1, color: successColor },
            ],
          },
        },
        barMaxWidth: 32,
      },
    ],
  });

  const dates = (dataScreen.value.submissionsByDate || []).map((i) => i.date);
  const counts = (dataScreen.value.submissionsByDate || []).map((i) => i.count);
  submissionTrendChart.setOption({
    title: {
      text: "最近7天提交量",
      show: false,
    },
    backgroundColor: "transparent",
    grid: { left: 36, right: 18, top: 16, bottom: 28 },
    xAxis: {
      type: "category",
      data: dates,
      axisLabel: { color: mutedColor },
      axisLine: { lineStyle: { color: "transparent" } },
      axisTick: { show: false },
    },
    yAxis: {
      type: "value",
      axisLabel: { color: mutedColor },
      splitLine: { lineStyle: { color: "rgba(127, 132, 125, 0.18)" } },
    },
    series: [
      {
        data: counts,
        type: "line",
        smooth: true,
        areaStyle: { color: "rgba(47, 128, 237, 0.18)" },
        lineStyle: { color: primaryColor, width: 3 },
        itemStyle: { color: primaryColor },
        symbol: "circle",
        symbolSize: 6,
      },
    ],
  });

  const { graded = 0, pending = 0 } = dataScreen.value.submissionStatus || {};
  submissionStatusChart.setOption({
    title: {
      text: "提交状态分布",
      show: false,
    },
    backgroundColor: "transparent",
    tooltip: { trigger: "item" },
    legend: { bottom: 0, textStyle: { color: mutedColor } },
    series: [
      {
        name: "提交状态",
        type: "pie",
        radius: ["35%", "60%"],
        data: [
          { value: pending, name: "待批改", itemStyle: { color: warningColor } },
          { value: graded, name: "已批改", itemStyle: { color: primaryColor } },
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
  gap: 18px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(160px, 1fr));
  gap: var(--spacing-m);
}

.stat {
  position: relative;
  min-height: 112px;
  overflow: hidden;
  padding: 18px;
  transition: border-color 0.15s ease, background 0.15s ease;
}

.stat:hover {
  border-color: color-mix(in srgb, var(--stat-color, var(--color-primary)) 58%, var(--color-border));
}

.label {
  color: var(--color-text-tertiary);
  margin: 0 0 6px;
}

.value {
  color: var(--color-text-primary);
  font-size: 30px;
  font-weight: 820;
  line-height: 1.05;
  margin: 0;
}

.stat-mark {
  position: absolute;
  right: 16px;
  bottom: 16px;
  width: 40px;
  height: 4px;
  border-radius: var(--radius);
  background: var(--stat-color, var(--color-primary));
}

.stat--0 {
  --stat-color: var(--color-primary);
}

.stat--1 {
  --stat-color: var(--color-success);
}

.stat--2 {
  --stat-color: var(--color-info);
}

.stat--3 {
  --stat-color: var(--color-warning);
}

.data-screen {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(280px, 0.85fr);
  gap: var(--spacing-m);
}

.chart-card {
  display: flex;
  min-height: 300px;
  flex-direction: column;
  padding: 18px;
}

.chart-card--course {
  grid-column: 1 / -1;
  min-height: 260px;
}

.section-title h2 {
  margin: 0;
}

.section-title p {
  margin: 6px 0 0;
  color: var(--color-text-tertiary);
  font-size: 13px;
}

.chart {
  width: 100%;
  min-height: 220px;
  flex: 1;
  background: transparent;
}

.activity-card {
  overflow: hidden;
}

.section-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-m);
  padding: 18px;
  border-bottom: 1px solid var(--color-border);
}

.section-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-s);
}

:deep(.el-table) {
  border: 0;
}

@media (max-width: 1080px) {
  .stats {
    grid-template-columns: repeat(2, minmax(160px, 1fr));
  }

  .data-screen {
    grid-template-columns: 1fr;
  }

  .chart-card--course {
    grid-column: auto;
  }
}

@media (max-width: 640px) {
  .stats {
    grid-template-columns: 1fr;
  }

  .section-toolbar {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
