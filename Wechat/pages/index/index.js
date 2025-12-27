const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    assignments: [],
    loading: false,
    selectedIndex: 0,
    submissionContent: '',
  },

  onLoad() {
    this.init();
  },

  async init() {
    this.setData({ loading: true });
    const hasToken = !!wx.getStorageSync('token');
    if (!hasToken) {
      app.loginWithWeapp();
      // 简单等待登录完成；正式环境可改为 Promise 回调
      await new Promise((r) => setTimeout(r, 800));
    }
    this.loadAssignments();
  },

  loadAssignments() {
    api
      .getPendingAssignments()
      .then((res) => {
        if (res.code === 200) {
          const list = res.data || [];
          this.setData({
            assignments: list,
            selectedIndex: list.length ? 0 : -1,
          });
        } else {
          wx.showToast({ title: res.message || '加载失败', icon: 'none' });
        }
      })
      .catch(() => wx.showToast({ title: '加载失败', icon: 'none' }))
      .finally(() => this.setData({ loading: false }));
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
        } else {
          wx.showToast({ title: res.message || '提交失败', icon: 'none' });
        }
      })
      .catch(() => wx.showToast({ title: '提交失败', icon: 'none' }));
  },
});
