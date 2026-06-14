import { Link, useNavigate } from 'react-router-dom';

function Navbar() {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('nickname');
    navigate('/login');
  };

  return (
    <nav style={styles.nav}>
      <Link to="/posts" style={styles.logo}>
        커뮤니티
      </Link>
      <div style={styles.menu}>
        {token ? (
          <>
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
