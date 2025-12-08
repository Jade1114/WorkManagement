<template>
  <div class="layout">
    <header class="top-bar card">
      <div class="brand">
        <div class="brand-name">Work Management</div>
      </div>
      <div class="top-actions">
        <el-dropdown trigger="click">
          <button
            class="pill-button role-pill"
            type="button"
            @mouseenter="hoverLogout = true"
            @mouseleave="hoverLogout = false"
            @focus="hoverLogout = true"
            @blur="hoverLogout = false"
          >
            <span>{{ hoverLogout ? "操作" : pillDisplay }}</span>
            <el-icon :size="16"><ArrowDown /></el-icon>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="openEdit">更改信息</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <div class="body">
      <aside class="sidebar card">
        <el-menu
          router
          :default-active="activePath"
          class="menu"
          background-color="transparent"
        >
          <el-menu-item
            v-for="item in filteredMenus"
            :key="item.path"
            :index="item.path"
          >
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
        <div class="sidebar-actions">
          <button class="pill-button wide" type="button" @click="cycleTheme">
            <el-icon :size="16">
              <component :is="themeIcon" />
            </el-icon>
            <span>{{ themeLabel }}</span>
          </button>
        </div>
      </aside>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>

  <el-dialog v-model="editVisible" title="更改信息" width="360px">
    <el-form label-position="top">
      <el-form-item label="用户名">
        <el-input v-model="editForm.username" placeholder="请输入新的用户名" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editVisible = false">取消</el-button>
      <el-button type="primary" @click="submitEdit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ArrowDown, Moon, Sunny, SwitchButton } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/userStore";
import { useThemeStore } from "@/stores/themeStore";
import http from "@/net/index.js";
import { ElMessage } from "element-plus";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const themeStore = useThemeStore();
const hoverLogout = ref(false);
const editVisible = ref(false);
const editForm = ref({ username: "" });

const menus = [
  { label: "仪表盘", path: "/dashboard", roles: ["admin", "teacher"] },
  { label: "用户管理", path: "/admin/users", roles: ["admin"] },
  { label: "学科管理", path: "/courses", roles: ["admin", "teacher"] },
  {
    label: "作业管理",
    path: "/assignments/manage",
    roles: ["admin", "teacher"],
  },
  { label: "学生列表", path: "/teacher/students", roles: ["teacher"] },
  { label: "未提交作业", path: "/student/home", roles: ["student"] },
  { label: "已提交作业", path: "/student/assignments", roles: ["student"] },
];

const activePath = computed(() => route.path);
const filteredMenus = computed(() =>
  menus.filter((m) => m.roles.includes(userStore.role))
);

const username = computed(() => userStore.username);
const usernameInitial = computed(() =>
  userStore.username ? userStore.username[0].toUpperCase() : "U"
);
const pillDisplay = computed(() => username.value || "退出登录");

const themeLabel = computed(() => {
  if (themeStore.mode === "dark") return "夜间模式";
  if (themeStore.mode === "light") return "日间模式";
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

const handleLogout = async () => {
  try {
    await http.post("/auth/logout");
  } catch (e) {
    // ignore logout error
  } finally {
    userStore.logout();
    router.push("/login");
  }
};

const openEdit = () => {
  editForm.value = { username: userStore.username };
  editVisible.value = true;
};

const submitEdit = async () => {
  try {
    await http.put("/users/admin/update", {
      userId: userStore.user?.userId,
      username: editForm.value.username,
    });
    userStore.user = { ...(userStore.user || {}), username: editForm.value.username };
    ElMessage.success("信息已更新");
    editVisible.value = false;
  } catch (e) {
    ElMessage.error("更新失败");
  }
};
</script>
