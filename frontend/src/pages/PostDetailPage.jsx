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
  const token = localStorage.getItem('token');
  const nickname = localStorage.getItem('nickname');

  // 댓글만 새로 불러오는 함수 추가
  const fetchComments = async () => {
    try {
      const res = await api.get(`/api/posts/${id}/comments`);
      setComments(res.data.data);
    } catch (err) {
      console.error(err);
    }
  };

  // 최초 1회만 detail과 댓글을 같이 불러오고, 이후 댓글만 새로고침
  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const fromEdit = location.state?.fromEdit;
        const url = fromEdit ? `/api/posts/${id}/detail?increaseView=false` : `/api/posts/${id}/detail`;
        const res = await api.get(url);
        setPost(res.data.data.post);
        setComments(res.data.data.comments);
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

  // 댓글 작성/삭제 시 fetchComments만 호출
  const handleCommentSubmit = async () => {
    if (!newComment.trim()) return;
    try {
      await api.post(`/api/posts/${id}/comments`, { content: newComment });
      setNewComment('');
      fetchComments(); // detail 대신 comments만 갱신
    } catch (err) {
      alert(err.response?.data?.message || '댓글 작성 실패');
    }
  };

  const handleCommentDelete = async (commentId) => {
    if (!window.confirm('댓글을 삭제하시겠습니까?')) return;
    try {
      await api.delete(`/api/posts/${id}/comments/${commentId}`);
      fetchComments(); // detail 대신 comments만 갱신
    } catch (err) {
      alert(err.response?.data?.message || '댓글 삭제 실패');
    }
  };

  if (!post) return <p style={styles.loading}>로딩 중...</p>;

  return (
    <div style={styles.container}>
      <div style={styles.postBox}>
        <h1 style={styles.title}>{post.title}</h1>
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
        <hr style={styles.divider} />
        <p style={styles.content}>{post.content}</p>
        {token && post.nickname === nickname && (
          <div style={styles.actions}>
            <button style={styles.editButton} onClick={() => navigate(`/posts/${id}/edit`)}>
              수정
            </button>
            <button style={styles.deleteButton} onClick={handleDelete}>
              삭제
            </button>
          </div>
        )}
      </div>

      <div style={styles.commentBox}>
        <h3 style={styles.commentTitle}>댓글 {comments.length}개</h3>
        {token && (
          <div style={styles.commentInput}>
            <textarea
              style={styles.textarea}
              placeholder="댓글을 입력하세요"
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
            />
            <button style={styles.submitButton} onClick={handleCommentSubmit}>
              등록
            </button>
          </div>
        )}
        {comments.map((comment) => (
          <div key={comment.id} style={styles.comment}>
            <div style={styles.commentMeta}>
              <span style={styles.commentNickname}>{comment.nickname}</span>
              <span style={styles.commentDate}>{new Date(comment.createdAt).toLocaleDateString()}</span>
            </div>
            <p style={styles.commentContent}>{comment.content}</p>
            {token && comment.nickname === nickname && (
              <button style={styles.commentDeleteButton} onClick={() => handleCommentDelete(comment.id)}>
                삭제
              </button>
            )}
          </div>
        ))}
      </div>

      <button style={styles.backButton} onClick={() => navigate('/posts')}>
        목록으로
      </button>
    </div>
  );
}

const styles = {
  container: { maxWidth: '800px', margin: '40px auto', padding: '0 20px' },
  loading: { textAlign: 'center', marginTop: '40px' },
  postBox: {
    backgroundColor: 'white',
    padding: '32px',
    borderRadius: '8px',
    boxShadow: '0 1px 4px rgba(0,0,0,0.1)',
    marginBottom: '24px',
  },
  title: { fontSize: '24px', marginBottom: '12px' },
  meta: { display: 'flex', gap: '16px', color: '#888', fontSize: '14px' },
  divider: { margin: '20px 0', border: 'none', borderTop: '1px solid #eee' },
  content: { fontSize: '16px', lineHeight: '1.8', whiteSpace: 'pre-wrap' },
  actions: { display: 'flex', gap: '8px', justifyContent: 'flex-end', marginTop: '20px' },
  editButton: {
    padding: '8px 16px',
    backgroundColor: '#555',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  deleteButton: {
    padding: '8px 16px',
    backgroundColor: '#e74c3c',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  commentBox: {
    backgroundColor: 'white',
    padding: '24px',
    borderRadius: '8px',
    boxShadow: '0 1px 4px rgba(0,0,0,0.1)',
    marginBottom: '16px',
  },
  commentTitle: { marginBottom: '16px', fontSize: '18px' },
  commentInput: { display: 'flex', gap: '8px', marginBottom: '20px' },
  textarea: {
    flex: 1,
    padding: '10px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '14px',
    resize: 'none',
    height: '60px',
  },
  submitButton: {
    padding: '0 16px',
    backgroundColor: '#2d2d2d',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  comment: { padding: '16px 0', borderBottom: '1px solid #eee' },
  commentMeta: { display: 'flex', gap: '12px', marginBottom: '8px' },
  commentNickname: { fontWeight: 'bold', fontSize: '14px' },
  commentDate: { color: '#888', fontSize: '14px' },
  commentContent: { fontSize: '14px', lineHeight: '1.6' },
  commentDeleteButton: {
    marginTop: '8px',
    padding: '4px 8px',
    backgroundColor: 'transparent',
    color: '#e74c3c',
    border: '1px solid #e74c3c',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '12px',
  },
  backButton: {
    padding: '10px 20px',
    backgroundColor: '#f0f0f0',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
  },
};

export default PostDetailPage;
