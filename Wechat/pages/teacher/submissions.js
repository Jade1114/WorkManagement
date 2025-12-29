const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    assignmentId: null,
    assignmentTitle: '',
    submissions: [],
    loading: false,
    gradingVisible: false,
    gradingScore: '',
    gradingComment: '',
    gradingTarget: null,
  },

  onLoad(options) {
    const { assignmentId, title } = options;
    this.setData({
      assignmentId: assignmentId ? Number(assignmentId) : null,
      assignmentTitle: title ? decodeURIComponent(title) : '',
    });
  },

  onShow() {
    wx.hideTabBar({ animation: false });
    this.init();
  },

  onReady() {
    wx.hideTabBar({ animation: false });
  },

  onPullDownRefresh() {
    this.init().finally(() => wx.stopPullDownRefresh());
  },

  async init() {
    const hasToken = !!wx.getStorageSync('token');
    if (!hasToken || app.globalData.needBind) {
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
    let role = app.globalData.role;
    try {
      const me = await api.getCurrentUser();
      if (me && me.code === 200 && me.data && me.data.role) {
        role = me.data.role;
        app.globalData.role = role;
        app.globalData.username = me.data.username;
        app.globalData.userId = me.data.id;
      }
    } catch (e) {}
    if (role !== 'teacher' && role !== 'admin') {
      wx.showToast({ title: '仅教师可访问', icon: 'none' });
      wx.showTabBar({ animation: false });
      wx.redirectTo({ url: '/pages/student/index' });
      return;
    }
    if (!this.data.assignmentId) {
      wx.showToast({ title: '缺少作业信息', icon: 'none' });
      return;
    }
    this.setData({ loading: true });
    await this.loadSubmissions();
    this.setData({ loading: false });
  },

  async loadSubmissions() {
    try {
      const res = await api.getSubmissionsByAssignment(this.data.assignmentId);
      if (res.code === 200) {
        this.setData({ submissions: res.data || [] });
      } else {
        wx.showToast({ title: res.message || '提交获取失败', icon: 'none' });
      }
    } catch (e) {
      wx.showToast({ title: '提交获取失败', icon: 'none' });
    }
  },

  openGrade(e) {
    const { id, username, score, comment } = e.currentTarget.dataset;
    const target = {
      id: Number(id),
      username,
      score: score !== undefined && score !== '' ? Number(score) : null,
      comment: comment || '',
    };
    this.setData({
      gradingVisible: true,
      gradingTarget: target,
      gradingScore: target.score != null ? String(target.score) : '',
      gradingComment: target.comment || '',
    });
  },

  closeGrade() {
    this.setData({
      gradingVisible: false,
      gradingTarget: null,
      gradingScore: '',
      gradingComment: '',
    });
  },

  onScoreInput(e) {
    this.setData({ gradingScore: e.detail.value });
  },

  onCommentInput(e) {
    this.setData({ gradingComment: e.detail.value });
  },

  submitGrade() {
    const { gradingTarget, gradingScore, gradingComment } = this.data;
    if (!gradingTarget) return;
    const scoreNum = Number(gradingScore);
    if (Number.isNaN(scoreNum)) {
      wx.showToast({ title: '请输入数值分数', icon: 'none' });
      return;
    }
    api
      .gradeSubmission({
        submissionId: gradingTarget.id,
        score: scoreNum,
        comment: gradingComment,
      })
      .then((res) => {
        if (res.code === 200) {
          wx.showToast({ title: '批改成功', icon: 'success' });
          this.closeGrade();
          this.loadSubmissions();
        } else {
          wx.showToast({ title: res.message || '批改失败', icon: 'none' });
        }
      })
      .catch(() => wx.showToast({ title: '批改失败', icon: 'none' }));
  },
});
