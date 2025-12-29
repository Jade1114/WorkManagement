const BASE_URL = "http://localhost:8080";

function request({ url, method = "GET", data = {}, auth = true }) {
  return new Promise((resolve, reject) => {
    const headers = { "Content-Type": "application/json" };
    if (auth) {
      const token = wx.getStorageSync("token");
      if (token) {
        headers["Authorization"] = `Bearer ${token}`;
      }
    }
    wx.request({
      url: `${BASE_URL}${url}`,
      method,
      data,
      header: headers,
      timeout: 8000,
      success(res) {
        const payload = res.data;
        // 401 统一处理：清理 token 并跳转登录
        if (res.statusCode === 401 || (payload && payload.code === 401)) {
          wx.removeStorageSync("token");
          const app = getApp();
          if (app && app.globalData) {
            app.globalData.token = null;
            app.globalData.needBind = false;
          }
          wx.showToast({ title: "登录已失效，请重新登录", icon: "none" });
          setTimeout(() => {
            wx.redirectTo({ url: "/pages/login/login" });
          }, 300);
          return;
        }
        resolve(payload);
      },
      fail(err) {
        reject(err);
      },
    });
  });
}

module.exports = {
  request,
  BASE_URL,
};
