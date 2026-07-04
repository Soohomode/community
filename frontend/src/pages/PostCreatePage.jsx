import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

function PostCreatePage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ title: '', content: '' });
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.title.trim() || !form.content.trim()) {
      setError('제목과 내용을 입력해주세요.');
      return;
    }
    try {
      const res = await api.post('/api/posts', form);
      navigate(`/posts/${res.data.data.id}`);
    } catch (err) {
      setError(err.response?.data?.message || '게시글 작성 실패');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-10 px-4">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white border border-gray-100 rounded-2xl shadow-sm p-8">
          {/* 제목 */}
          <h2 className="text-xl font-bold text-gray-900 mb-6">게시글 작성</h2>

          {/* 에러 메시지 */}
          {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            {/* 제목 입력 */}
            <input
              className="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm
                         focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                         placeholder-gray-400 transition-all"
              type="text"
              name="title"
              placeholder="제목을 입력하세요"
              value={form.title}
              onChange={handleChange}
            />

            {/* 내용 입력 */}
            <textarea
              className="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm
                         focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                         placeholder-gray-400 transition-all
                         resize-y min-h-64 leading-relaxed"
              name="content"
              placeholder="내용을 입력하세요"
              value={form.content}
              onChange={handleChange}
            />

            {/* 버튼 */}
            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => navigate('/posts')}
                className="px-5 py-2.5 border border-gray-200 text-gray-500 text-sm
                           rounded-lg hover:bg-gray-50 transition-colors"
              >
                취소
              </button>
              <button
                type="submit"
                className="px-5 py-2.5 bg-blue-600 text-white text-sm font-medium
                           rounded-lg hover:bg-blue-700 transition-colors"
              >
                작성
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default PostCreatePage;
