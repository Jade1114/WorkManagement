const api = require('./utils/api');

App({
  onLaunch() {
    this.loginWithWeapp();
  },

  loginWithWeapp() {
    wx.login({
      success: ({ code }) => {
        if (!code) {
          wx.showToast({ title: '获取登录码失败', icon: 'none' });
          return;
        }
        api.weappLogin(code)
          .then((res) => {
            if (res.code === 200 && res.data) {
              const { token, role, username } = res.data;
              wx.setStorageSync('token', token);
              this.globalData.token = token;
              this.globalData.role = role;
              this.globalData.username = username;
            } else {
              wx.showToast({ title: res.message || '登录失败', icon: 'none' });
            }
          })
          .catch(() => {
            wx.showToast({ title: '登录失败，请稍后再试', icon: 'none' });
          });
      },
      fail: () => wx.showToast({ title: 'wx.login 失败', icon: 'none' }),
    });
  },

  globalData: {
    token: null,
    role: null,
    username: null,
  },
});
