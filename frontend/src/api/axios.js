import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
});

// 요청 인터셉터 — Access Token 자동 첨부
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 응답 인터셉터 — 401 감지 시 자동 토큰 갱신
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // 401이고 재시도 안 한 요청이면
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // 무한 루프 방지

      const refreshToken = localStorage.getItem('refreshToken');

      if (!refreshToken) {
        // Refresh Token 없으면 로그아웃 처리
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(error);
      }

      try {
        // Refresh Token으로 새 Access Token 요청
        const res = await axios.post(`${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/auth/refresh`, {
          refreshToken,
        });

        const newAccessToken = res.data.data.token;
        const newRefreshToken = res.data.data.refreshToken;

        // 새 토큰 저장
        localStorage.setItem('token', newAccessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        // 원래 요청에 새 Access Token 붙여서 재시도
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh Token도 만료됐으면 로그아웃 처리
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export default api;
