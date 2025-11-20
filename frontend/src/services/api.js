import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等认证信息
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

// 任务相关API
export const taskAPI = {
  // 获取任务列表
  getTasks: (params = {}) => {
    return api.get('/tasks', { params });
  },

  // 获取任务详情
  getTask: (id) => {
    return api.get(`/tasks/${id}`);
  },

  // 创建任务
  createTask: (data) => {
    return api.post('/tasks', data);
  },

  // 更新任务
  updateTask: (id, data) => {
    return api.put(`/tasks/${id}`, data);
  },

  // 删除任务
  deleteTask: (id) => {
    return api.delete(`/tasks/${id}`);
  },

  // 切换任务状态
  toggleTaskStatus: (id) => {
    return api.patch(`/tasks/${id}/toggle`);
  },

  // 获取任务统计
  getStatistics: () => {
    return api.get('/tasks/statistics');
  },

  // 获取逾期任务
  getOverdueTasks: () => {
    return api.get('/tasks/overdue');
  },
};

// 提醒相关API
export const reminderAPI = {
  // 获取当前提醒
  getCurrentReminders: () => {
    return api.get('/reminders/current');
  },
};

export default api;