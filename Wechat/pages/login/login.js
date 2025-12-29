const app = getApp();

Page({
  data: {
    loading: false,
    needBind: false,
    bindTicket: null,
    bindUsername: '',
    bindPassword: '',
    binding: false,
  },

  onLoad() {
    const hasToken = !!wx.getStorageSync('token');
    if (hasToken) {
      this.goHome();
      return;
    }
    this.tryLogin();
  },

  tryLogin() {
    this.setData({ loading: true });
    app
      .loginWithWeapp()
      .then((res) => {
        if (res && res.status === 'need_bind') {
          this.setData({
            needBind: true,
            bindTicket: res.bindTicket || app.globalData.bindTicket,
          });
        } else {
          this.goHome();
        }
      })
      .catch((err) => {
        const msg =
          err && err.errMsg && err.errMsg.indexOf('fail') !== -1
            ? '服务器不可用，请检查后端是否启动'
            : '登录失败，请稍后重试';
        wx.showToast({ title: msg, icon: 'none' });
      })
      .finally(() => this.setData({ loading: false }));
  },

  onBindUsernameInput(e) {
    this.setData({ bindUsername: e.detail.value });
  },

  onBindPasswordInput(e) {
    this.setData({ bindPassword: e.detail.value });
  },

  bindAccount() {
    const { bindUsername, bindPassword } = this.data;
    const ticket = this.data.bindTicket || app.globalData.bindTicket;
    if (!ticket) {
      wx.showToast({ title: '绑定票据已失效，请重新登录', icon: 'none' });
      this.tryLogin();
      return;
    }
    if (!bindUsername.trim() || !bindPassword.trim()) {
      wx.showToast({ title: '请输入账号和密码', icon: 'none' });
      return;
    }
    this.setData({ binding: true });
    app
      .loginWithWeapp() // refresh ticket if needed
      .catch(() => {})
      .finally(() => {
        app.globalData.bindTicket = ticket;
        const api = require('../../utils/api');
        api
          .weappBind({ bindTicket: ticket, username: bindUsername, password: bindPassword })
          .then((res) => {
            if (res.code === 200 && res.data) {
              const { token, role, username, userId } = res.data;
              wx.setStorageSync('token', token);
              app.globalData.token = token;
              app.globalData.role = role;
              app.globalData.username = username;
              app.globalData.userId = userId;
              app.globalData.needBind = false;
              app.globalData.bindTicket = null;
              wx.showToast({ title: '绑定成功', icon: 'success' });
              this.goHome();
            } else {
              wx.showToast({ title: res.message || '绑定失败', icon: 'none' });
            }
          })
          .catch(() => wx.showToast({ title: '绑定失败，请稍后重试', icon: 'none' }))
          .finally(() => this.setData({ binding: false }));
      });
  },

  goHome() {
    wx.redirectTo({ url: '/pages/index/index' });
  },
});
