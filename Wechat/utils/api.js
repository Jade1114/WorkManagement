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

function weappBind(payload) {
  return request({
    url: '/api/auth/weapp/bind',
    method: 'POST',
    data: payload,
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
    url: '/api/submissions/my/list',
    method: 'GET',
  });
}

function createSubmission(payload) {
  return request({
    url: '/api/submissions/submit',
    method: 'POST',
    data: payload,
  });
}

function getTeacherAssignments() {
  return request({
    url: '/api/assignments/all',
    method: 'GET',
  });
}

function getSubmissionsByAssignment(assignmentId) {
  return request({
    url: '/api/submissions/list',
    method: 'GET',
    data: { assignmentId },
  });
}

function gradeSubmission(payload) {
  return request({
    url: '/api/submissions/grade',
    method: 'POST',
    data: payload,
  });
}

function getTeacherStats() {
  return request({
    url: '/api/teacher/stats',
    method: 'GET',
  });
}

function getCourses() {
  return request({
    url: '/api/courses/get',
    method: 'GET',
  });
}

function createAssignment(payload) {
  return request({
    url: '/api/assignments/create',
    method: 'POST',
    data: payload,
  });
}

function getCurrentUser() {
  return request({
    url: '/api/users/me',
    method: 'GET',
  });
}

module.exports = {
  weappLogin,
  weappBind,
  getPendingAssignments,
  getMySubmissions,
  createSubmission,
  getTeacherAssignments,
  getSubmissionsByAssignment,
  gradeSubmission,
  getTeacherStats,
  getCourses,
  createAssignment,
  getCurrentUser,
};
