import { Link, useNavigate } from 'react-router-dom';
import { useState, useEffect, useRef } from 'react';
import api from '../api/axios';
import useNotification from '../hooks/useNotification';

function Navbar() {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const { notifications, setInitialNotifications, markAsRead } = useNotification();
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    if (!token) return;
    const fetchNotifications = async () => {
      try {
        const res = await api.get('/api/notifications');
        setInitialNotifications(res.data.data);
      } catch (err) {
        console.error('알림 목록 조회 실패', err);
      }
    };
    fetchNotifications();
  }, [token]);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (refreshToken) {
        await api.post('/api/auth/logout', { refreshToken }); // 서버에 로그아웃 알림
      }
    } catch (err) {
      console.error('로그아웃 API 실패', err);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken'); // 추가!
      localStorage.removeItem('nickname');
      navigate('/login');
    }
  };

  const handleNotificationClick = (notification) => {
    setShowDropdown(false);
    if (!notification.read) {
      markAsRead(notification.id);
    }
    navigate(`/posts/${notification.postId}`);
  };

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <nav className="bg-white border-b border-gray-200 shadow-sm sticky top-0 z-50">
      <div className="max-w-5xl mx-auto px-6 py-3 flex justify-between items-center">
        {/* 로고 */}
        <Link to="/posts" className="text-xl font-bold text-gray-900 hover:text-blue-600 transition-colors">
          커뮤니티
        </Link>

        {/* 메뉴 */}
        <div className="flex items-center gap-4">
          {token ? (
            <>
              {/* 알림 벨 */}
              <div className="relative" ref={dropdownRef}>
                <button
                  onClick={() => setShowDropdown((prev) => !prev)}
                  className="relative text-gray-500 hover:text-gray-900 text-xl transition-colors"
                >
                  🔔
                  {unreadCount > 0 && (
                    <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center font-bold">
                      {unreadCount}
                    </span>
                  )}
                </button>

                {/* 알림 드롭다운 */}
                {showDropdown && (
                  <div className="absolute top-10 right-0 bg-white border border-gray-100 rounded-xl shadow-lg w-72 max-h-80 overflow-y-auto z-50">
                    {notifications.length === 0 ? (
                      <div className="p-4 text-sm text-gray-400 text-center">알림이 없습니다.</div>
                    ) : (
                      notifications.map((n) => (
                        <div
                          key={n.id}
                          onClick={() => handleNotificationClick(n)}
                          className={`px-4 py-3 text-sm border-b border-gray-50 cursor-pointer hover:bg-gray-50 transition-colors
                            ${n.read ? 'text-gray-400' : 'text-gray-700 font-medium'}`}
                        >
                          {n.content}
                        </div>
                      ))
                    )}
                  </div>
                )}
              </div>

              {/* 글쓰기 버튼 */}
              <Link
                to="/posts/create"
                className="bg-blue-600 text-white text-sm font-medium px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
              >
                글쓰기
              </Link>

              {/* 로그아웃 */}
              <button onClick={handleLogout} className="text-sm text-gray-500 hover:text-gray-900 transition-colors">
                로그아웃
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="text-sm text-gray-600 hover:text-gray-900 transition-colors">
                로그인
              </Link>
              <Link
                to="/join"
                className="bg-blue-600 text-white text-sm font-medium px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
              >
                회원가입
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
