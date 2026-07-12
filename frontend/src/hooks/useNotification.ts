import { useState, useEffect, useRef } from 'react';
import api, { ApiResponse } from '../api/axios';

// 알림 데이터 타입
interface Notification {
  id: number;
  senderNickname: string;
  content: string;
  postId: number;
  read: boolean;
  createdAt: string;
}

function useNotification() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const eventSourceRef = useRef<EventSource | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) return;

    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
    const eventSource = new EventSource(`${apiUrl}/api/notifications/subscribe?token=${token}`);
    eventSourceRef.current = eventSource;

    eventSource.addEventListener('connect', (event: MessageEvent) => {
      console.log('SSE 연결됨:', event.data);
    });

    eventSource.addEventListener('notification', (event: MessageEvent) => {
      const newNotification: Notification = JSON.parse(event.data);
      setNotifications((prev) => [newNotification, ...prev]);
    });

    eventSource.onerror = () => {
      console.error('SSE 연결 오류');
    };

    return () => {
      eventSource.close();
    };
  }, []);

  const setInitialNotifications = (list: Notification[]): void => {
    setNotifications(list);
  };

  const markAsRead = async (notificationId: number): Promise<void> => {
    try {
      await api.patch<ApiResponse<void>>(`/api/notifications/${notificationId}/read`);
      setNotifications((prev) => prev.map((n) => (n.id === notificationId ? { ...n, read: true } : n)));
    } catch (err) {
      console.error('읽음 처리 실패', err);
    }
  };

  return { notifications, setInitialNotifications, markAsRead };
}

export default useNotification;
export type { Notification };
