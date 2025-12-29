const api = require('./utils/api');

App({
  onLaunch() {
    this.loginWithWeapp().catch(() => {});
  },

  clearAuthAndRedirect() {
    wx.removeStorageSync('token');
    this.globalData.token = null;
    this.globalData.role = null;
    this.globalData.username = null;
    this.globalData.userId = null;
    this.globalData.needBind = false;
    this.globalData.bindTicket = null;

    const pages = getCurrentPages();
    const currentRoute = pages.length ? pages[pages.length - 1].route : '';
    if (currentRoute !== 'pages/login/login') {
      wx.redirectTo({ url: '/pages/login/login' });
    }
  },

  loginWithWeapp() {
    return new Promise((resolve, reject) => {
      wx.login({
        success: async ({ code }) => {
          if (!code) {
            wx.showToast({ title: '获取登录码失败', icon: 'none' });
            this.clearAuthAndRedirect();
            reject(new Error('no code'));
            return;
          }
          try {
            const res = await api.weappLogin(code);
            if (res.code === 200 && res.data) {
              const data = res.data;
              if (data.needBind) {
                this.globalData.bindTicket = data.bindTicket;
                this.globalData.needBind = true;
                resolve({ status: 'need_bind', bindTicket: data.bindTicket });
                return;
              }
              const { token, role, username, userId } = data;
              wx.setStorageSync('token', token);
              this.globalData.token = token;
              this.globalData.role = role;
              this.globalData.username = username;
              this.globalData.userId = userId;
              this.globalData.needBind = false;
              this.globalData.bindTicket = null;
              if (role === 'teacher' || role === 'admin') {
                wx.hideTabBar({ animation: false });
              } else {
                wx.showTabBar({ animation: false });
              }
              resolve({ status: 'logged_in', token, role, username });
            } else {
              wx.showToast({ title: res.message || '登录失败', icon: 'none' });
              this.clearAuthAndRedirect();
              reject(new Error(res.message || 'login failed'));
            }
          } catch (err) {
            wx.showToast({ title: '登录失败，请稍后重试', icon: 'none' });
            this.clearAuthAndRedirect();
            reject(err);
          }
        },
        fail: () => {
          wx.showToast({ title: 'wx.login 失败', icon: 'none' });
          this.clearAuthAndRedirect();
          reject(new Error('wx.login failed'));
        },
      });
    });
  },

  globalData: {
    token: null,
    role: null,
    username: null,
    userId: null,
    needBind: false,
    bindTicket: null,
  },
});
