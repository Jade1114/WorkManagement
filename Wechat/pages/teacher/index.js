const api = require("../../utils/api");
const app = getApp();

Page({
  data: {
    currentTab: "home",
    stats: null,
    loadingStats: false,
    assignments: [],
    loadingAssignments: false,
    submissionsLoading: false,
    courses: [],
    publishVisible: false,
    publishCourseIndex: 0,
    publishTitle: "",
    publishContent: "",
    publishDeadline: "",
    userInfo: null,
  },

  onShow() {
    wx.hideTabBar({ animation: false });
    this.init();
  },

  onReady() {
    wx.hideTabBar({ animation: false });
  },

  onLoad() {
    wx.hideTabBar({ animation: false });
  },

  onPullDownRefresh() {
    this.init().finally(() => wx.stopPullDownRefresh());
  },

  async init() {
    const hasToken = !!wx.getStorageSync("token");
    if (!hasToken || app.globalData.needBind) {
      wx.redirectTo({ url: "/pages/login/login" });
      return;
    }
    // 再次校验角色，确保进入教师端时权限正确
    let role = app.globalData.role;
    try {
      const me = await api.getCurrentUser();
      if (me && me.code === 200 && me.data && me.data.role) {
        role = me.data.role;
        app.globalData.role = role;
        app.globalData.username = me.data.username;
        app.globalData.userId = me.data.id;
        this.setData({ userInfo: me.data });
      }
    } catch (e) {
      // ignore, fallback to globalData
    }
    if (role !== "teacher" && role !== "admin") {
      wx.showToast({ title: "仅教师可访问", icon: "none" });
      wx.showTabBar({ animation: false });
      wx.redirectTo({ url: "/pages/student/index" });
      return;
    }
    this.loadStats();
    this.loadAssignments();
    this.loadCourses();
  },

  switchTab(e) {
    const { tab } = e.currentTarget.dataset;
    this.setData({ currentTab: tab });
  },

  async loadStats() {
    this.setData({ loadingStats: true });
    try {
      const res = await api.getTeacherStats();
      if (res.code === 200) {
        this.setData({ stats: res.data });
      } else {
        wx.showToast({ title: res.message || "统计获取失败", icon: "none" });
      }
    } catch (e) {
      wx.showToast({ title: "统计获取失败", icon: "none" });
    } finally {
      this.setData({ loadingStats: false });
    }
  },

  async loadAssignments() {
    this.setData({ loadingAssignments: true });
    try {
      const res = await api.getTeacherAssignments();
      if (res.code === 200) {
        this.setData({ assignments: res.data || [] });
      } else {
        wx.showToast({ title: res.message || "作业获取失败", icon: "none" });
      }
    } catch (e) {
      wx.showToast({ title: "作业获取失败", icon: "none" });
    } finally {
      this.setData({ loadingAssignments: false });
    }
  },

  async loadCourses() {
    try {
      const res = await api.getCourses();
      if (res.code === 200) {
        this.setData({ courses: res.data || [] });
      }
    } catch (e) {
      // ignore silently
    }
  },

  openPublish() {
    this.setData({
      publishVisible: true,
      publishCourseIndex: 0,
      publishTitle: "",
      publishContent: "",
      publishDeadline: "",
    });
  },

  closePublish() {
    this.setData({ publishVisible: false });
  },

  onPublishCourseChange(e) {
    this.setData({ publishCourseIndex: Number(e.detail.value) });
  },

  onPublishTitleInput(e) {
    this.setData({ publishTitle: e.detail.value });
  },

  onPublishContentInput(e) {
    this.setData({ publishContent: e.detail.value });
  },

  onPublishDeadlineInput(e) {
    this.setData({ publishDeadline: e.detail.value });
  },

  submitPublish() {
    const {
      courses,
      publishCourseIndex,
      publishTitle,
      publishContent,
      publishDeadline,
    } = this.data;
    if (!courses.length) {
      wx.showToast({ title: "请先创建课程", icon: "none" });
      return;
    }
    if (!publishTitle.trim()) {
      wx.showToast({ title: "请输入作业名称", icon: "none" });
      return;
    }
    const selectedCourse = courses[publishCourseIndex];
    api
      .createAssignment({
        courseId: selectedCourse.id,
        title: publishTitle,
        content: publishContent,
        deadline: publishDeadline || null,
      })
      .then((res) => {
        if (res.code === 200) {
          wx.showToast({ title: "发布成功", icon: "success" });
          this.closePublish();
          this.loadAssignments();
        } else {
          wx.showToast({ title: res.message || "发布失败", icon: "none" });
        }
      })
      .catch(() => wx.showToast({ title: "发布失败", icon: "none" }));
  },

  goSubmissions(e) {
    const { id, title } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/teacher/submissions?assignmentId=${id}&title=${encodeURIComponent(
        title || ""
      )}`,
    });
  },
});
