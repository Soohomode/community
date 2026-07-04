import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axios';

function JoinPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '', nickname: '' });
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/api/auth/join', form);
      alert('회원가입 성공! 로그인해주세요.');
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="bg-white w-full max-w-sm rounded-2xl shadow-sm border border-gray-100 p-8">
        {/* 제목 */}
        <h2 className="text-2xl font-bold text-gray-900 text-center mb-6">회원가입</h2>

        {/* 에러 메시지 */}
        {error && <p className="text-red-500 text-sm text-center mb-4">{error}</p>}

        {/* 폼 */}
        <form onSubmit={handleSubmit} className="flex flex-col gap-3">
          <input
            className="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm
                       focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                       placeholder-gray-400 transition-all"
            type="email"
            name="email"
            placeholder="이메일"
            value={form.email}
            onChange={handleChange}
          />
          <input
            className="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm
                       focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                       placeholder-gray-400 transition-all"
            type="text"
            name="nickname"
            placeholder="닉네임"
            value={form.nickname}
            onChange={handleChange}
          />
          <input
            className="w-full px-4 py-3 border border-gray-200 rounded-lg text-sm
                       focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                       placeholder-gray-400 transition-all"
            type="password"
            name="password"
            placeholder="비밀번호"
            value={form.password}
            onChange={handleChange}
          />
          <button
            type="submit"
            className="w-full py-3 bg-blue-600 text-white text-sm font-semibold
                       rounded-lg hover:bg-blue-700 transition-colors mt-1"
          >
            회원가입
          </button>
        </form>

        {/* 하단 링크 */}
        <p className="text-center text-sm text-gray-400 mt-5">
          이미 계정이 있으신가요?{' '}
          <Link to="/login" className="text-blue-600 font-medium hover:underline">
            로그인
          </Link>
        </p>
      </div>
    </div>
  );
}

export default JoinPage;
