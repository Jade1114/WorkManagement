const { request } = require('./request');

// 微信登录，获取业务 token
function weappLogin(code) {
  return request({
    url: '/api/auth/weapp/login',
    method: 'POST',
    data: { code },
    auth: false,
  });
}

function getPendingAssignments() {
  return request({
    url: '/api/assignments/pending',
    method: 'GET',
  });
}

function getMySubmissions() {
  return request({
    url: '/api/submissions/my',
    method: 'GET',
  });
}

function createSubmission(payload) {
  return request({
    url: '/api/submissions/create',
    method: 'POST',
    data: payload,
  });
}

module.exports = {
  weappLogin,
  getPendingAssignments,
  getMySubmissions,
  createSubmission,
};
