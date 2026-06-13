import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/axios';

function PostEditPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [form, setForm] = useState({ title: '', content: '' });
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const res = await api.get(`/api/posts/${id}`);
                setForm({
                    title: res.data.data.title,
                    content: res.data.data.content,
                });
            } catch (err) {
                console.error(err);
            }
        };
        fetchPost();
    }, [id]);

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
            await api.put(`/api/posts/${id}`, form);
            navigate(`/posts/${id}`);
        } catch (err) {
            setError(err.response?.data?.message || '수정 실패');
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.box}>
                <h2 style={styles.title}>게시글 수정</h2>
                {error && <p style={styles.error}>{error}</p>}
                <form onSubmit={handleSubmit}>
                    <input
                        style={styles.input}
                        type="text"
                        name="title"
                        placeholder="제목"
                        value={form.title}
                        onChange={handleChange}
                    />
                    <textarea
                        style={styles.textarea}
                        name="content"
                        placeholder="내용을 입력하세요"
                        value={form.content}
                        onChange={handleChange}
                    />
                    <div style={styles.buttons}>
                        <button
                            type="button"
                            style={styles.cancelButton}
                            onClick={() => navigate(`/posts/${id}`)}>
                            취소
                        </button>
                        <button type="submit" style={styles.submitButton}>
                            수정
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        padding: '40px 20px',
        backgroundColor: '#f5f5f5',
        minHeight: 'calc(100vh - 60px)',
    },
    box: {
        backgroundColor: 'white',
        padding: '40px',
        borderRadius: '8px',
        boxShadow: '0 1px 4px rgba(0,0,0,0.1)',
        width: '100%',
        maxWidth: '800px',
        height: 'fit-content',
    },
    title: { marginBottom: '24px', fontSize: '22px' },
    error: { color: 'red', fontSize: '14px', marginBottom: '12px' },
    input: {
        width: '100%',
        padding: '12px',
        marginBottom: '16px',
        border: '1px solid #ddd',
        borderRadius: '4px',
        fontSize: '16px',
        boxSizing: 'border-box',
    },
    textarea: {
        width: '100%',
        padding: '12px',
        marginBottom: '16px',
        border: '1px solid #ddd',
        borderRadius: '4px',
        fontSize: '15px',
        lineHeight: '1.6',
        minHeight: '300px',
        resize: 'vertical',
        boxSizing: 'border-box',
    },
    buttons: { display: 'flex', justifyContent: 'flex-end', gap: '8px' },
    cancelButton: {
        padding: '10px 20px',
        backgroundColor: '#f0f0f0',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '15px',
    },
    submitButton: {
        padding: '10px 24px',
        backgroundColor: '#2d2d2d',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '15px',
    },
};

export default PostEditPage;