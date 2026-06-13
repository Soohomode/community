import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080' // 백엔드 API의 기본 URL (한곳에서 관리)
});

// 요청마다 토큰 자동 추가
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`; // 토큰이 존재하면 Authorization 헤더에 추가
  }
  return config;
});

export default api;