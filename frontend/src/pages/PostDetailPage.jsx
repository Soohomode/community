import { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import api from '../api/axios';

function PostDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const token = localStorage.getItem('token');
  const nickname = localStorage.getItem('nickname');

  const fetchComments = async () => {
    try {
      const res = await api.get(`/api/posts/${id}/comments`);
      setComments(res.data.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const fromEdit = location.state?.fromEdit;
        const url = fromEdit ? `/api/posts/${id}/detail?increaseView=false` : `/api/posts/${id}/detail`;
        const res = await api.get(url);
        setPost(res.data.data.post);
        setComments(res.data.data.comments);
        setLikeCount(res.data.data.post.likeCount);
      } catch (err) {
        console.error(err);
      }
    };
    fetchDetail();
  }, [id, location.state?.fromEdit]);

  const handleDelete = async () => {
    if (!window.confirm('삭제하시겠습니까?')) return;
    try {
      await api.delete(`/api/posts/${id}`);
      navigate('/posts');
    } catch (err) {
      alert(err.response?.data?.message || '삭제 실패');
    }
  };

  const handleCommentSubmit = async () => {
    if (!newComment.trim()) return;
    try {
      await api.post(`/api/posts/${id}/comments`, { content: newComment });
      setNewComment('');
      fetchComments();
    } catch (err) {
      alert(err.response?.data?.message || '댓글 작성 실패');
    }
  };

  const handleCommentDelete = async (commentId) => {
    if (!window.confirm('댓글을 삭제하시겠습니까?')) return;
    try {
      await api.delete(`/api/posts/${id}/comments/${commentId}`);
      fetchComments();
    } catch (err) {
      alert(err.response?.data?.message || '댓글 삭제 실패');
    }
  };

  // 낙관적 업데이트로 좋아요 토글
  const handleLike = async () => {
    if (!token) {
      navigate('/login');
      return;
    }

    // 1. 이전 상태 저장 (실패 시 rollback 용)
    const prevLiked = liked;
    const prevCount = likeCount;

    // 2. 즉시 UI 업데이트 (낙관적 업데이트)
    setLiked(!liked);
    setLikeCount(liked ? likeCount - 1 : likeCount + 1);

    try {
      // 3. API 호출
      const res = await api.post(`/api/posts/${id}/like`);
      // 4. API 응답값으로 최종 확정
      setLiked(res.data.data);
    } catch (err) {
      // 5. 실패 시 이전 상태로 rollback
      setLiked(prevLiked);
      setLikeCount(prevCount);
      alert('좋아요 처리에 실패했습니다.');
    }
  };

  const formatDate = (createdAt) => {
    if (!createdAt) return '';
    const date = Array.isArray(createdAt) ? new Date(...createdAt) : new Date(createdAt);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (!post)
    return (
      <div className="flex justify-center items-center min-h-64">
        <div className="text-gray-400 text-sm">로딩 중...</div>
      </div>
    );

  return (
    <div className="max-w-5xl mx-auto px-6 py-8">
      {/* 게시글 카드 */}
      <div className="bg-white border border-gray-100 rounded-xl p-8 mb-4 shadow-sm">
        {/* 제목 */}
        <h1 className="text-2xl font-bold text-gray-900 mb-4">{post.title}</h1>

        {/* 메타 정보 */}
        <div className="flex items-center gap-4 text-sm text-gray-400 pb-5 border-b border-gray-100">
          <span className="font-medium text-gray-600">{post.nickname}</span>
          <span>👁️ {post.viewCount}</span>
          <span className="ml-auto">{formatDate(post.createAt)}</span>
        </div>

        {/* 본문 */}
        <p className="py-6 text-gray-700 text-base leading-relaxed whitespace-pre-wrap border-b border-gray-100">
          {post.content}
        </p>

        {/* 좋아요 버튼 */}
        <div className="flex justify-center pt-6 pb-2">
          <button
            onClick={handleLike}
            className={`flex items-center gap-2 px-6 py-2.5 rounded-full border-2 
                        font-medium text-sm transition-all
                        ${
                          liked
                            ? 'border-red-400 bg-red-50 text-red-500 hover:bg-red-100'
                            : 'border-gray-200 bg-white text-gray-400 hover:border-gray-300 hover:bg-gray-50'
                        }`}
          >
            <span className="text-lg">{liked ? '❤️' : '🤍'}</span>
            <span>{likeCount}</span>
          </button>
        </div>

        {/* 수정/삭제 버튼 */}
        {token && post.nickname === nickname && (
          <div className="flex justify-end gap-2 pt-4">
            <button
              onClick={() => navigate(`/posts/${id}/edit`)}
              className="px-4 py-2 bg-gray-100 text-gray-600 text-sm rounded-lg
                         hover:bg-gray-200 transition-colors"
            >
              수정
            </button>
            <button
              onClick={handleDelete}
              className="px-4 py-2 bg-red-50 text-red-500 text-sm rounded-lg
                         hover:bg-red-100 transition-colors"
            >
              삭제
            </button>
          </div>
        )}
      </div>

      {/* 댓글 카드 */}
      <div className="bg-white border border-gray-100 rounded-xl p-6 mb-4 shadow-sm">
        <h3 className="text-base font-semibold text-gray-700 mb-5">댓글 {comments.length}개</h3>

        {/* 댓글 입력창 */}
        {token && (
          <div className="flex gap-2 mb-6">
            <textarea
              className="flex-1 px-4 py-3 border border-gray-200 rounded-lg text-sm
                         focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                         resize-none placeholder-gray-400 transition-all"
              placeholder="댓글을 입력하세요"
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              rows={2}
            />
            <button
              onClick={handleCommentSubmit}
              className="px-5 bg-gray-800 text-white text-sm font-medium rounded-lg
                         hover:bg-gray-700 transition-colors"
            >
              등록
            </button>
          </div>
        )}

        {/* 댓글 목록 */}
        {comments.length === 0 ? (
          <p className="text-center text-sm text-gray-400 py-6">첫 번째 댓글을 남겨보세요!</p>
        ) : (
          <div className="flex flex-col divide-y divide-gray-50">
            {comments.map((comment) => (
              <div key={comment.id} className="py-4">
                <div className="flex items-center gap-3 mb-2">
                  <span className="text-sm font-semibold text-gray-700">{comment.nickname}</span>
                  <span className="text-xs text-gray-400">{formatDate(comment.createdAt)}</span>
                  {token && comment.nickname === nickname && (
                    <button
                      onClick={() => handleCommentDelete(comment.id)}
                      className="ml-auto text-xs text-gray-300 hover:text-red-400 transition-colors"
                    >
                      삭제
                    </button>
                  )}
                </div>
                <p className="text-sm text-gray-600 leading-relaxed">{comment.content}</p>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* 목록으로 버튼 */}
      <button
        onClick={() => navigate('/posts')}
        className="text-sm text-gray-400 hover:text-gray-600 transition-colors flex items-center gap-1"
      >
        ← 목록으로
      </button>
    </div>
  );
}

export default PostDetailPage;
