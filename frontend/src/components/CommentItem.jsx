import React, {useState} from 'react';
import {Link} from 'react-router-dom';
import {getToken} from '../services/authService';

const API_BASE = 'http://localhost:8080';

const CommentItem = ({comment, bacaanId, onCommentRefresh}) => {
    const [showReplyForm, setShowReplyForm] = useState(false);
    const [replyText, setReplyText] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleReplySubmit = async (e) => {
        e.preventDefault();
        const token = getToken();
        if (!token) return;

        setIsSubmitting(true);
        try {
            const payload = {
                isiKomentar: replyText,
                bacaanId: bacaanId,
                parentCommentId: comment.id // Tandai sebagai anak dari komentar ini
            };

            const res = await fetch(`${API_BASE}/api/comment`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                setShowReplyForm(false);
                setReplyText('');
                if (onCommentRefresh) onCommentRefresh();
            } else {
                throw new Error("Gagal mengirim balasan");
            }
        } catch (error) {
            console.error("Error:", error);
            alert("Gagal mengirim balasan");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div style={{display: 'flex', flexDirection: 'column', marginTop: '8px'}}>
            {/* Box Komentar Utama */}
            <article style={{
                backgroundColor: 'var(--base)',
                border: '1px solid var(--surface1)',
                borderRadius: '10px',
                padding: '12px',
            }}>
                <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '8px'}}>
                    <small style={{color: 'var(--subtext0)'}}>
                        <strong>{comment.username || 'Unknown'}</strong>
                    </small>
                    <small style={{color: 'var(--subtext0)'}}>
                        {comment.createdAt ? new Date(comment.createdAt).toLocaleString() : ''}
                    </small>
                </div>

                <p style={{margin: 0, whiteSpace: 'pre-wrap', color: 'var(--text)'}}>
                    {comment.isiKomentar}
                </p>

                {/* Aksi Bawah: Reply, Edit, Hapus */}
                <div style={{display: 'flex', gap: '8px', marginTop: '12px', alignItems: 'center'}}>
                    <button
                        onClick={() => setShowReplyForm(!showReplyForm)}
                        style={{
                            background: 'none', border: 'none', color: 'var(--blue)',
                            cursor: 'pointer', padding: 0, fontSize: '13px', fontWeight: 'bold'
                        }}
                    >
                        {showReplyForm ? 'Batal Balas' : 'Balas'}
                    </button>

                    <div style={{flex: 1}}></div>
                    {/* Spacer */}

                    <Link to={`/bacaan/${bacaanId}/comment/${comment.id}/edit`}>
                        <button className="btn btn-edit" style={{padding: '4px 12px', fontSize: '12px'}}>Edit</button>
                    </Link>
                    <Link to={`/bacaan/${bacaanId}/comment/${comment.id}/delete`}>
                        <button className="btn btn-delete" style={{padding: '4px 12px', fontSize: '12px'}}>Hapus
                        </button>
                    </Link>
                </div>
            </article>

            {/* Collapsible Form Balasan */}
            {showReplyForm && (
                <div style={{
                    marginLeft: '24px',
                    marginTop: '8px',
                    paddingLeft: '12px',
                    borderLeft: '2px solid var(--blue)'
                }}>
                    <form onSubmit={handleReplySubmit} style={{display: 'flex', flexDirection: 'column', gap: '8px'}}>
                        <textarea
                            className="input-entry"
                            value={replyText}
                            onChange={(e) => setReplyText(e.target.value)}
                            placeholder={`Balas komentar ${comment.username}...`}
                            rows="2"
                            required
                            style={{width: '100%', resize: 'vertical'}}
                        />
                        <button
                            className="btn btn-add"
                            type="submit"
                            disabled={isSubmitting}
                            style={{alignSelf: 'flex-start', padding: '6px 12px', fontSize: '13px'}}
                        >
                            {isSubmitting ? 'Mengirim...' : 'Kirim'}
                        </button>
                    </form>
                </div>
            )}

            {/* Render Balasan (Replies) secara Rekursif */}
            {comment.replies && comment.replies.length > 0 && (
                <div style={{
                    marginLeft: '24px',
                    paddingLeft: '12px',
                    borderLeft: '2px solid var(--surface1)',
                    marginTop: '8px'
                }}>
                    {comment.replies.map((reply) => (
                        <CommentItem
                            key={reply.id}
                            comment={reply}
                            bacaanId={bacaanId}
                            onCommentRefresh={onCommentRefresh}
                        />
                    ))}
                </div>
            )}
        </div>
    );
};

export default CommentItem;