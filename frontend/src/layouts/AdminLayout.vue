<template>
  <div class="layout">
    <aside class="sidebar">
      <button class="brand" type="button" @click="goHome">
        <span class="brand-name">AdminPro</span>
        <span class="brand-kicker">工作台 · v2.4</span>
      </button>

      <nav class="menu" aria-label="主导航">
        <section
          v-for="group in filteredMenuGroups"
          :key="group.label"
          class="nav-section"
        >
          <p class="nav-label">{{ group.label }}</p>
          <router-link
            v-for="item in group.items"
            :key="item.path"
            class="nav-item"
            :class="{ 'is-active': activePath === item.path }"
            :to="item.path"
          >
            <el-icon :size="17">
              <component :is="item.icon" />
            </el-icon>
            <span>{{ item.label }}</span>
          </router-link>
        </section>

        <section class="nav-section">
          <p class="nav-label">系统</p>
          <button class="nav-item nav-item--button" type="button" @click="cycleTheme">
            <el-icon :size="17">
              <component :is="themeIcon" />
            </el-icon>
            <span>{{ themeLabel }}</span>
          </button>
          <button class="nav-item nav-item--button" type="button" @click="handleLogout">
            <el-icon :size="17"><SwitchButton /></el-icon>
            <span>退出登录</span>
          </button>
        </section>
      </nav>

      <el-dropdown trigger="click">
        <button class="user-panel" type="button">
          <span class="user-avatar">{{ usernameInitial }}</span>
          <span class="user-copy">
            <strong>{{ username || "未登录用户" }}</strong>
            <span>{{ roleName }}</span>
          </span>
          <el-icon :size="15"><ArrowDown /></el-icon>
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="cycleTheme">{{ themeLabel }}</el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </aside>

    <section class="workspace">
      <header class="top-bar">
        <div class="top-title">
          <p>{{ roleName }} / {{ activeGroupLabel }}</p>
          <h1>{{ pageTitle }}</h1>
        </div>
        <div class="top-actions">
          <SearchInput
            v-model="globalKeyword"
            class="top-search"
            placeholder="搜索功能数据..."
            @keyup.enter="handleGlobalSearch"
          />
          <el-button :icon="Download" @click="handleExport">导出报表</el-button>
          <el-button type="primary" :icon="Plus" @click="handleCreate">新建</el-button>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </section>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  ArrowDown,
  Collection,
  DataLine,
  DocumentChecked,
  Download,
  Files,
  Moon,
  Plus,
  Reading,
  Sunny,
  SwitchButton,
  Tickets,
  User,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/userStore";
import { useThemeStore } from "@/stores/themeStore";
import SearchInput from "@/components/SearchInput.vue";
import http from "@/net/index.js";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const themeStore = useThemeStore();
const globalKeyword = ref("");

const menuGroups = [
  {
    label: "概览",
    items: [
      { label: "数据看板", path: "/dashboard", roles: ["admin", "teacher"], icon: DataLine },
      { label: "未提交作业", path: "/student/home", roles: ["student"], icon: Tickets },
    ],
  },
  {
    label: "管理",
    items: [
      { label: "用户管理", path: "/admin/users", roles: ["admin"], icon: User },
      { label: "学科管理", path: "/courses", roles: ["admin", "teacher"], icon: Reading },
      { label: "作业管理", path: "/assignments/manage", roles: ["admin", "teacher"], icon: Files },
      { label: "学生列表", path: "/teacher/students", roles: ["teacher"], icon: Collection },
      { label: "已提交作业", path: "/student/assignments", roles: ["student"], icon: DocumentChecked },
    ],
  },
];

const filteredMenuGroups = computed(() =>
  menuGroups
    .map((group) => ({
      ...group,
      items: group.items.filter((item) => item.roles.includes(userStore.role)),
    }))
    .filter((group) => group.items.length)
);

const activePath = computed(() => route.path);
const flatMenus = computed(() => filteredMenuGroups.value.flatMap((group) => group.items));
const activeMenu = computed(() => flatMenus.value.find((item) => item.path === activePath.value));
const activeGroupLabel = computed(() => {
  const group = filteredMenuGroups.value.find((item) =>
    item.items.some((menu) => menu.path === activePath.value)
  );
  return group?.label || "系统";
});
const pageTitle = computed(() => route.meta.title || activeMenu.value?.label || "工作台");

const username = computed(() => userStore.username);
const usernameInitial = computed(() =>
  userStore.username ? userStore.username[0].toUpperCase() : "U"
);
const roleName = computed(() => {
  if (userStore.role === "admin") return "超级管理员";
  if (userStore.role === "teacher") return "教师";
  if (userStore.role === "student") return "学生";
  return "访客";
});

const themeLabel = computed(() => {
  if (themeStore.mode === "dark") return "深色模式";
  if (themeStore.mode === "light") return "浅色模式";
  return "跟随系统";
});

const themeIcon = computed(() => {
  if (themeStore.mode === "dark") return Moon;
  if (themeStore.mode === "light") return Sunny;
  return SwitchButton;
});

const cycleTheme = () => themeStore.nextMode();

const goHome = () => {
  if (userStore.role === "admin" || userStore.role === "teacher") {
    router.push("/dashboard");
  } else if (userStore.role === "student") {
    router.push("/student/home");
  } else {
    router.push("/login");
  }
};

const handleGlobalSearch = () => {
  if (!globalKeyword.value.trim()) return;
  ElMessage.info(`正在搜索：${globalKeyword.value.trim()}`);
};

const handleExport = () => {
  ElMessage.success("报表导出任务已创建");
};

const handleCreate = () => {
  if (route.path === "/courses") {
    ElMessage.info("请使用当前页面的新建学科入口");
    return;
  }
  if (route.path === "/assignments/manage") {
    ElMessage.info("请使用当前页面的新建作业入口");
    return;
  }
  ElMessage.info("请在对应业务页创建新内容");
};

const handleLogout = async () => {
  try {
    await http.post("/user/auth/logout");
  } catch (e) {
    // ignore logout error
  } finally {
    userStore.logout();
    router.push("/login");
  }
};
</script>
