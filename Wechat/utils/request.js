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
        resolve(res.data);
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
