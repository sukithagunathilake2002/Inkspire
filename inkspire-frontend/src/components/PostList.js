import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/PostList.css'; // Make sure you link the CSS

const PostList = () => {
  const [posts, setPosts] = useState([]);
  const [message, setMessage] = useState('');
  const [editingPostId, setEditingPostId] = useState(null);
  const [editDescription, setEditDescription] = useState('');
  const [editIsPrivate, setEditIsPrivate] = useState(false);
  const navigate = useNavigate();

  const token = localStorage.getItem('token');

  useEffect(() => {
    fetchMyPosts();
  }, []);

  const fetchMyPosts = async () => {
    try {
      const response = await axios.get('http://localhost:8081/api/posts/my-posts', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setPosts(response.data);
    } catch (error) {
      console.error('Error fetching posts:', error);
      setMessage('Failed to load posts');
    }
  };

  const handleDelete = async (postId) => {
    const confirmed = window.confirm('Are you sure you want to delete this post?');
    if (!confirmed) return;

    try {
      await axios.delete(`http://localhost:8081/api/posts/delete/${postId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setMessage('Post deleted successfully');
      fetchMyPosts();
    } catch (error) {
      console.error('Error deleting post:', error);
      setMessage('Failed to delete post');
    }
  };

  const handleEdit = (post) => {
    setEditingPostId(post.id);
    setEditDescription(post.description);
    setEditIsPrivate(post.private);
  };

  const handleUpdate = async (postId) => {
    const confirmed = window.confirm('Are you sure you want to update this post?');
    if (!confirmed) return;

    try {
      const formData = new FormData();
      formData.append('description', editDescription);
      formData.append('isPrivate', editIsPrivate);

      await axios.put(`http://localhost:8081/api/posts/update/${postId}`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data',
        },
      });

      setMessage('Post updated successfully');
      setEditingPostId(null);
      fetchMyPosts();
    } catch (error) {
      console.error('Error updating post:', error);
      setMessage('Failed to update post');
    }
  };

  return (
    <div className="postlist-container">
      <div className="postlist-header">
        <h2 className="postlist-title">My Posts</h2>
        <button className="postlist-newpost-btn" onClick={() => navigate('/newpost')}>
          + New Post
        </button>
      </div>

      {message && <p className="postlist-message">{message}</p>}

      {posts.length === 0 ? (
        <p className="postlist-empty">No posts found.</p>
      ) : (
        posts.map((post) => (
          <div key={post.id} className="postlist-card">
            {editingPostId === post.id ? (
              <div className="postlist-edit">
                <textarea
                  value={editDescription}
                  onChange={(e) => setEditDescription(e.target.value)}
                  rows={3}
                />
                <select
                  value={editIsPrivate}
                  onChange={(e) => setEditIsPrivate(e.target.value === 'true')}
                >
                  <option value="false">Public</option>
                  <option value="true">Private</option>
                </select>
                <div className="postlist-buttons">
                  <button onClick={() => handleUpdate(post.id)}>Save</button>
                  <button onClick={() => setEditingPostId(null)}>Cancel</button>
                </div>
              </div>
            ) : (
              <div className="postlist-content">
                <div className="postlist-media">
                  {post.mediaUrls && post.mediaUrls.length > 0 &&
                    post.mediaUrls.map((mediaUrl, idx) => (
                      mediaUrl.endsWith('.mp4') || mediaUrl.endsWith('.mov') ? (
                        <video key={idx} controls className="postlist-media-item">
                          <source src={`http://localhost:8081/${mediaUrl.startsWith('uploads/') ? mediaUrl : 'uploads/' + mediaUrl}`}
 />
                        </video>
                      ) : (
                        <img
                          key={idx}
                          src={`http://localhost:8081/${mediaUrl.startsWith('uploads/') ? mediaUrl : 'uploads/' + mediaUrl}`}
                          alt={`Post media ${idx}`}
                          className="postlist-media-item"
                        />
                      )
                    ))
                  }
                </div>
                <p className="postlist-description">{post.description}</p>
                <p className="postlist-privacy">{post.private ? 'Private' : 'Public'}</p>
                <div className="postlist-buttons">
                  <button onClick={() => handleEdit(post)}>Edit</button>
                  <button onClick={() => handleDelete(post.id)}>Delete</button>
                </div>
              </div>
            )}
          </div>
        ))
      )}
    </div>
  );
};

export default PostList;
