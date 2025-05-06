import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import '../styles/PostList.css'


const BASE_URL = "http://localhost:8080"; // ✅ Backend server URL

function PostList() {
  const [posts, setPosts] = useState([]);
  const [editCaption, setEditCaption] = useState('');
  const [editingPostId, setEditingPostId] = useState(null);

  const fetchPosts = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/api/posts`);
      setPosts(res.data);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, []);

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this post?')) {
      try {
        await axios.delete(`${BASE_URL}/api/posts/${id}`);
        fetchPosts();
      } catch (error) {
        console.error(error);
      }
    }
  };

  const startEditing = (post) => {
    setEditingPostId(post.id);
    setEditCaption(post.caption);
  };

  const cancelEditing = () => {
    setEditingPostId(null);
    setEditCaption('');
  };

  const handleUpdate = async (post) => {
    try {
      await axios.put(`${BASE_URL}/api/posts/${post.id}`, {
        caption: editCaption,
        isVideo: post.isVideo,
        mediaUrls: post.mediaUrls,
      });
      setEditingPostId(null);
      fetchPosts();
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="postlist-container">
  <div className="postlist-header">
    <h2>All Posts</h2>
    <Link to="/newpost" className="create-post-btn">Create New Post</Link>
  </div>

  <div>
    {posts.map(post => (
      <div className="post-card" key={post.id}>
        {post.isVideo ? (
          <video className="post-media" controls src={`${BASE_URL}${post.mediaUrls[0]}`} />
        ) : (
          post.mediaUrls.map((url, idx) => (
            <img
              key={idx}
              src={`${BASE_URL}${url}`}
              alt={`media-${idx}`}
              className="post-media"
            />
          ))
        )}

        <div className="post-body">
          {editingPostId === post.id ? (
            <>
              <textarea
                className="edit-caption"
                value={editCaption}
                onChange={(e) => setEditCaption(e.target.value)}
              />
              <div className="post-buttons">
                <button className="btn btn-primary" onClick={() => handleUpdate(post)}>Save</button>
                <button className="btn btn-secondary" onClick={cancelEditing}>Cancel</button>
              </div>
            </>
          ) : (
            <>
              <p className="post-caption">{post.caption}</p>
              <div className="post-buttons">
                <button className="btn btn-outline-primary" onClick={() => startEditing(post)}>Edit</button>
                <button className="btn btn-outline-danger" onClick={() => handleDelete(post.id)}>Delete</button>
              </div>
            </>
          )}
        </div>
      </div>
    ))}
  </div>
</div>

  );
}

export default PostList;
