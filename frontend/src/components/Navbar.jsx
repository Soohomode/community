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

  // 페이지 로드 시 기존 알림 목록 불러오기
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

  // 드롭다운 바깥 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('nickname');
    navigate('/login');
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
    <nav style={styles.nav}>
      <Link to="/posts" style={styles.logo}>
        커뮤니티
      </Link>
      <div style={styles.menu}>
        {token ? (
          <>
            <div style={styles.notificationWrapper} ref={dropdownRef}>
              <button
                onClick={() => setShowDropdown((prev) => !prev)}
                style={styles.bellButton}
              >
                🔔
                {unreadCount > 0 && (
                  <span style={styles.badge}>{unreadCount}</span>
                )}
              </button>

              {showDropdown && (
                <div style={styles.dropdown}>
                  {notifications.length === 0 ? (
                    <div style={styles.emptyItem}>알림이 없습니다.</div>
                  ) : (
                    notifications.map((n) => (
                      <div
                        key={n.id}
                        style={styles.dropdownItem}
                        onClick={() => handleNotificationClick(n)}
                      >
                        {n.content}
                      </div>
                    ))
                  )}
                </div>
              )}
            </div>

            <Link to="/posts/create" style={styles.link}>
              글쓰기
            </Link>
            <button onClick={handleLogout} style={styles.button}>
              로그아웃
            </button>
          </>
        ) : (
          <>
            <Link to="/login" style={styles.link}>
              로그인
            </Link>
            <Link to="/join" style={styles.link}>
              회원가입
            </Link>
          </>
        )}
      </div>
    </nav>
  );
}

const styles = {
  nav: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '16px 32px',
    backgroundColor: '#2d2d2d',
    color: 'white',
  },
  logo: {
    color: 'white',
    textDecoration: 'none',
    fontSize: '20px',
    fontWeight: 'bold',
  },
  menu: {
    display: 'flex',
    gap: '16px',
    alignItems: 'center',
  },
  link: {
    color: 'white',
    textDecoration: 'none',
  },
  button: {
    background: 'none',
    border: '1px solid white',
    color: 'white',
    padding: '6px 12px',
    cursor: 'pointer',
    borderRadius: '4px',
  },
  notificationWrapper: {
    position: 'relative',
  },
  bellButton: {
    background: 'none',
    border: 'none',
    color: 'white',
    fontSize: '18px',
    cursor: 'pointer',
    position: 'relative',
  },
  badge: {
    position: 'absolute',
    top: '-6px',
    right: '-8px',
    backgroundColor: '#e74c3c',
    color: 'white',
    borderRadius: '50%',
    fontSize: '11px',
    padding: '2px 6px',
    fontWeight: 'bold',
  },
  dropdown: {
    position: 'absolute',
    top: '32px',
    right: 0,
    backgroundColor: 'white',
    color: '#333',
    width: '280px',
    maxHeight: '320px',
    overflowY: 'auto',
    borderRadius: '8px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
    zIndex: 100,
  },
  dropdownItem: {
    padding: '12px 16px',
    fontSize: '14px',
    borderBottom: '1px solid #eee',
    cursor: 'pointer',
  },
  emptyItem: {
    padding: '16px',
    fontSize: '14px',
    color: '#999',
    textAlign: 'center',
  },
};

export default Navbar;

/*
- Navbar 컴포넌트는 로그인 상태에 따라 다른 메뉴를 보여줍니다.
- 로그인 상태에서는 글쓰기 버튼과 로그아웃 버튼이 보이고, 로그아웃 시 토큰이 제거되고 로그인 페이지로 이동합니다.
- 비로그인 상태에서는 로그인과 회원가입 링크가 보입니다.
- 스타일은 간단한 인라인 스타일로 적용되어 있습니다.

로그인 상태 (token 있음) → 글쓰기, 로그아웃 표시
비로그인 상태 (token 없음) → 로그인, 회원가입 표시

localStorage → 브라우저에 데이터를 저장하는 공간
로그인 시 토큰 저장, 로그아웃 시 토큰 삭제
*/
