const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    assignments: [],
    loading: false,
    selectedIndex: 0,
    submissionContent: '',
    userInfo: null,
    submissions: [],
    loadingSubmissions: false,
  },

  onLoad() {
    this.init();
  },

  async init() {
    const hasToken = !!wx.getStorageSync('token');
    if (!hasToken || app.globalData.needBind) {
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
    this.setData({ loading: true });
    await Promise.all([this.loadUserInfo(), this.loadAssignments(), this.loadSubmissions()]);
    this.setData({ loading: false });
  },

  async loadUserInfo() {
    try {
      const res = await api.getCurrentUser();
      if (res.code === 200) {
        this.setData({ userInfo: res.data });
      } else {
        wx.showToast({ title: res.message || '用户信息获取失败', icon: 'none' });
      }
    } catch (e) {
      wx.showToast({ title: '用户信息获取失败', icon: 'none' });
    }
  },

  async loadAssignments() {
    try {
      const res = await api.getPendingAssignments();
      if (res.code === 200) {
        const list = res.data || [];
        this.setData({
          assignments: list,
          selectedIndex: list.length ? 0 : -1,
        });
      } else {
        wx.showToast({ title: res.message || '待提交获取失败', icon: 'none' });
      }
    } catch (e) {
      wx.showToast({ title: '待提交获取失败', icon: 'none' });
    }
  },

  onPickerChange(e) {
    this.setData({ selectedIndex: Number(e.detail.value) });
  },

  onInput(e) {
    this.setData({ submissionContent: e.detail.value });
  },

  submitAssignment() {
    const { assignments, selectedIndex, submissionContent } = this.data;
    if (!assignments.length) {
      wx.showToast({ title: '暂无作业可提交', icon: 'none' });
      return;
    }
    if (!submissionContent.trim()) {
      wx.showToast({ title: '请输入作业内容', icon: 'none' });
      return;
    }
    const target = assignments[selectedIndex];
    api
      .createSubmission({ assignmentId: target.id, content: submissionContent })
      .then((res) => {
        if (res.code === 200) {
          wx.showToast({ title: '提交成功', icon: 'success' });
          this.setData({ submissionContent: '' });
          this.loadAssignments();
          this.loadSubmissions();
        } else {
          wx.showToast({ title: res.message || '提交失败', icon: 'none' });
        }
      })
      .catch(() => wx.showToast({ title: '提交失败', icon: 'none' }));
  },

  async loadSubmissions() {
    this.setData({ loadingSubmissions: true });
    try {
      const res = await api.getMySubmissions();
      if (res.code === 200) {
        this.setData({ submissions: res.data || [] });
      } else {
        wx.showToast({ title: res.message || '我的提交获取失败', icon: 'none' });
      }
    } catch (e) {
      wx.showToast({ title: '我的提交获取失败', icon: 'none' });
    } finally {
      this.setData({ loadingSubmissions: false });
    }
  },
});
