import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

function PostListPage() {
  const navigate = useNavigate();
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [keyword, setKeyword] = useState('');
  const [sort, setSort] = useState('latest');

  const fetchPosts = async () => {
    try {
      const res = await api.get('/api/posts', {
        params: { page, size: 10, sort },
      });
      setPosts(res.data.data.posts);
      setTotalPages(res.data.data.totalPages);
    } catch (err) {
      console.error(err);
    }
  };

  const searchPosts = async () => {
    if (!keyword.trim()) {
      fetchPosts();
      return;
    }
    try {
      const res = await api.get('/api/posts/search', {
        params: { keyword, page, size: 10 },
      });
      setPosts(res.data.data.posts);
      setTotalPages(res.data.data.totalPages);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const res = await api.get('/api/posts', {
          params: { page, size: 10, sort },
        });
        setPosts(res.data.data.posts);
        setTotalPages(res.data.data.totalPages);
      } catch (err) {
        console.error(err);
      }
    };
    fetchPosts();
  }, [page, sort]);

  return (
    <div style={styles.container}>
      {/* 검색 & 정렬 */}
      <div style={styles.toolbar}>
        <div style={styles.searchBox}>
          <input
            style={styles.searchInput}
            placeholder="검색어를 입력하세요"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && searchPosts()}
          />
          <button style={styles.searchButton} onClick={searchPosts}>
            검색
          </button>
        </div>
        <select
          style={styles.select}
          value={sort}
          onChange={(e) => {
            setSort(e.target.value);
            setPage(0);
          }}
        >
          <option value="latest">최신순</option>
          <option value="oldest">오래된순</option>
          <option value="popular">조회수순</option>
        </select>
      </div>

      {/* 게시글 목록 */}
      <div style={styles.list}>
        {posts.length === 0 ? (
          <p style={styles.empty}>게시글이 없습니다.</p>
        ) : (
          posts.map((post) => (
            <div key={post.id} style={styles.item} onClick={() => navigate(`/posts/${post.id}`)}>
              <h3 style={styles.title}>{post.title}</h3>
              <div style={styles.meta}>
                <span>{post.nickname}</span>
                <span>조회수 {post.viewCount}</span>
                <span>
                  {post.createdAt
                    ? new Date(
                        Array.isArray(post.createdAt) ? new Date(...post.createdAt) : post.createdAt,
                      ).toLocaleDateString()
                    : ''}
                </span>
              </div>
            </div>
          ))
        )}
      </div>

      {/* 페이징 */}
      <div style={styles.pagination}>
        <button style={styles.pageButton} disabled={page === 0} onClick={() => setPage(page - 1)}>
          이전
        </button>
        <span style={styles.pageInfo}>
          {page + 1} / {totalPages}
        </span>
        <button style={styles.pageButton} disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>
          다음
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: { maxWidth: '800px', margin: '40px auto', padding: '0 20px' },
  toolbar: { display: 'flex', justifyContent: 'space-between', marginBottom: '20px', gap: '12px' },
  searchBox: { display: 'flex', gap: '8px', flex: 1 },
  searchInput: { flex: 1, padding: '10px', border: '1px solid #ddd', borderRadius: '4px', fontSize: '14px' },
  searchButton: {
    padding: '10px 16px',
    backgroundColor: '#2d2d2d',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  select: { padding: '10px', border: '1px solid #ddd', borderRadius: '4px', fontSize: '14px' },
  list: { display: 'flex', flexDirection: 'column', gap: '12px' },
  item: {
    backgroundColor: 'white',
    padding: '20px',
    borderRadius: '8px',
    boxShadow: '0 1px 4px rgba(0,0,0,0.1)',
    cursor: 'pointer',
  },
  title: { margin: '0 0 8px 0', fontSize: '18px' },
  meta: { display: 'flex', gap: '16px', color: '#888', fontSize: '14px' },
  empty: { textAlign: 'center', color: '#888', padding: '40px' },
  pagination: { display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '16px', marginTop: '24px' },
  pageButton: {
    padding: '8px 16px',
    backgroundColor: '#2d2d2d',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  pageInfo: { fontSize: '14px', color: '#555' },
};

export default PostListPage;
