import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import pinia from "@/stores";

const layoutChildren = [
  {
    path: "dashboard",
    name: "dashboard",
    component: () => import("@/views/admin/DashboardView.vue"),
    meta: { requiresAuth: true, roles: ["admin", "teacher"] },
  },
  {
    path: "admin/users",
    name: "admin-users",
    component: () => import("@/views/admin/UserManageView.vue"),
    meta: { requiresAuth: true, roles: ["admin"] },
  },
  {
    path: "courses",
    name: "courses",
    component: () => import("@/views/admin/CourseManageView.vue"),
    meta: { requiresAuth: true, roles: ["admin", "teacher"] },
  },
  {
    path: "assignments/manage",
    name: "assignments-manage",
    component: () => import("@/views/teacher/TeacherAssignmentsView.vue"),
    meta: { requiresAuth: true, roles: ["admin", "teacher"] },
  },
  {
    path: "teacher/home",
    name: "teacher-home",
    component: () => import("@/views/teacher/TeacherHomeView.vue"),
    meta: { requiresAuth: true, roles: ["admin", "teacher"] },
  },
  {
    path: "teacher/students",
    name: "teacher-students",
    component: () => import("@/views/teacher/TeacherStudentsView.vue"),
    meta: { requiresAuth: true, roles: ["admin", "teacher"] },
  },
  {
    path: "student/home",
    name: "student-home",
    component: () => import("@/views/student/StudentHomeView.vue"),
    meta: { requiresAuth: true, roles: ["student"] },
  },
  {
    path: "student/assignments",
    name: "student-assignments",
    component: () => import("@/views/student/StudentAssignmentsView.vue"),
    meta: { requiresAuth: true, roles: ["student"] },
  },
];

const routes = [
  {
    path: "/",
    redirect: "/login",
  },
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/welcome/LoginView.vue"),
    meta: { guestOnly: true },
  },
  {
    path: "/register",
    name: "register",
    component: () => import("@/views/welcome/RegisterView.vue"),
    meta: { guestOnly: true },
  },
  {
    path: "/",
    component: () => import("@/layouts/AdminLayout.vue"),
    children: layoutChildren,
  },
  {
    path: "/error",
    name: "error",
    component: () => import("@/views/ErrorView.vue"),
    meta: { guestOnly: true },
  },
  { path: "/:pathMatch(.*)*", redirect: "/" },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

const resolveHomeByRole = (role) => {
  if (role === "admin") return "/dashboard";
  if (role === "teacher") return "/dashboard";
  if (role === "student") return "/student/home";
  return "/login";
};

router.beforeEach((to, from, next) => {
  const userStore = useUserStore(pinia);
  const isLoggedIn = !!userStore.token;
  const isGuestOnly = to.meta.guestOnly;

  if (isGuestOnly && isLoggedIn) {
    next(resolveHomeByRole(userStore.role));
    return;
  }

  if (to.meta.requiresAuth && !isLoggedIn) {
    next("/login");
    return;
  }

  if (
    to.meta.roles &&
    userStore.role &&
    !to.meta.roles.includes(userStore.role)
  ) {
    next("/error");
    return;
  }

  next();
});

export default router;
