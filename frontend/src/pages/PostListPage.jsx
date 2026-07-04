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
  const token = localStorage.getItem('token');

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
    fetchPosts();
  }, [page, sort]);

  const formatDate = (createdAt) => {
    if (!createdAt) return '';
    const date = Array.isArray(createdAt) ? new Date(...createdAt) : new Date(createdAt);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  return (
    <div className="max-w-5xl mx-auto px-6 py-8">
      {/* 검색 & 정렬 영역 */}
      <div className="flex gap-3 mb-8">
        <div className="flex flex-1 gap-2">
          <input
            className="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg text-sm
                       focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                       transition-all placeholder-gray-400"
            placeholder="검색어를 입력하세요"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && searchPosts()}
          />
          <button
            className="px-5 py-2.5 bg-gray-800 text-white text-sm font-medium
                       rounded-lg hover:bg-gray-700 transition-colors"
            onClick={searchPosts}
          >
            검색
          </button>
        </div>
        <select
          className="px-4 py-2.5 border border-gray-200 rounded-lg text-sm
                     focus:outline-none focus:ring-2 focus:ring-blue-500
                     bg-white text-gray-700 cursor-pointer"
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
      {posts.length === 0 ? (
        /* 빈 상태 */
        <div className="flex flex-col items-center justify-center py-24 text-center">
          <div className="text-5xl mb-4">📝</div>
          <h3 className="text-lg font-semibold text-gray-700 mb-2">아직 게시글이 없어요</h3>
          <p className="text-sm text-gray-400 mb-6">첫 번째 게시글을 작성해보세요!</p>
          {token && (
            <button
              onClick={() => navigate('/posts/create')}
              className="px-5 py-2.5 bg-blue-600 text-white text-sm font-medium
                         rounded-lg hover:bg-blue-700 transition-colors"
            >
              글 작성하기
            </button>
          )}
        </div>
      ) : (
        <div className="flex flex-col gap-3">
          {posts.map((post) => (
            <div
              key={post.id}
              onClick={() => navigate(`/posts/${post.id}`)}
              className="bg-white border border-gray-100 rounded-xl p-5
                         hover:shadow-md hover:border-gray-200
                         transition-all cursor-pointer group"
            >
              {/* 제목 */}
              <h3
                className="text-base font-semibold text-gray-900 mb-3
                             group-hover:text-blue-600 transition-colors line-clamp-1"
              >
                {post.title}
              </h3>

              {/* 메타 정보 */}
              <div className="flex items-center gap-4 text-xs text-gray-400">
                <span className="font-medium text-gray-500">{post.nickname}</span>
                <span>❤️ {post.likeCount}</span>
                <span>👁️ {post.viewCount}</span>
                <span className="ml-auto">{formatDate(post.createAt)}</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* 페이지네이션 */}
      {totalPages > 0 && (
        <div className="flex justify-center items-center gap-4 mt-10">
          <button
            onClick={() => setPage(page - 1)}
            disabled={page === 0}
            className="px-5 py-2 bg-gray-800 text-white text-sm font-medium rounded-lg
                       hover:bg-gray-700 transition-colors
                       disabled:opacity-40 disabled:cursor-not-allowed"
          >
            이전
          </button>
          <span className="text-sm text-gray-500 font-medium">
            {page + 1} / {totalPages}
          </span>
          <button
            onClick={() => setPage(page + 1)}
            disabled={page >= totalPages - 1}
            className="px-5 py-2 bg-gray-800 text-white text-sm font-medium rounded-lg
                       hover:bg-gray-700 transition-colors
                       disabled:opacity-40 disabled:cursor-not-allowed"
          >
            다음
          </button>
        </div>
      )}
    </div>
  );
}

export default PostListPage;
