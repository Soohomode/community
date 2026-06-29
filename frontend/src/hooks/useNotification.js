import { useState, useEffect, useRef } from 'react';
import api from '../api/axios';

function useNotification() {
  const [notifications, setNotifications] = useState([]);
  const eventSourceRef = useRef(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) return; // 로그인 안 했으면 연결 안 함

    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
    const eventSource = new EventSource(`${apiUrl}/api/notifications/subscribe?token=${token}`);
    eventSourceRef.current = eventSource;

    // 연결 확인 이벤트
    eventSource.addEventListener('connect', (event) => {
      console.log('SSE 연결됨:', event.data);
    });

    // 알림 이벤트 수신
    eventSource.addEventListener('notification', (event) => {
      const newNotification = JSON.parse(event.data);
      setNotifications((prev) => [newNotification, ...prev]);
    });

    eventSource.onerror = (error) => {
      console.error('SSE 연결 오류:', error);
    };

    // 컴포넌트 언마운트 시 연결 종료
    return () => {
      eventSource.close();
    };
  }, []);

  // 기존 알림 목록 불러오기 (페이지 로드 시)
  const setInitialNotifications = (list) => {
    setNotifications(list);
  };
  // 알림 읽음 처리
  const markAsRead = async (notificationId) => {
    try {
      await api.patch(`/api/notifications/${notificationId}/read`);
      // 프론트 상태도 즉시 업데이트 (다시 fetch 안 해도 됨)
      setNotifications((prev) => prev.map((n) => (n.id === notificationId ? { ...n, read: true } : n)));
    } catch (err) {
      console.error('읽음 처리 실패', err);
    }
  };

  return { notifications, setInitialNotifications, markAsRead };
}

export default useNotification;
