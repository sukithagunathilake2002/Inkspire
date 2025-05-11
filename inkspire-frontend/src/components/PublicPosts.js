
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/PublicPosts.css';

const PublicPosts = () => {
  const [posts, setPosts] = useState([]);
  const [commentsMap, setCommentsMap] = useState({});
  const [newCommentMap, setNewCommentMap] = useState({});
  const [likeCounts, setLikeCounts] = useState({});
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  useEffect(() => {
    fetchMyPublicPosts();
  }, []);

  const fetchMyPublicPosts = async () => {
    try {
      const response = await axios.get('http://localhost:8081/api/posts/my-posts', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const publicPosts = response.data.filter(post => !post.private);
      setPosts(publicPosts);

      publicPosts.forEach(post => {
        fetchComments(post.id);
        fetchLikeCount(post.id);
      });
    } catch (error) {
      console.error('Error fetching posts:', error);
      setMessage('Failed to load your public posts.');
    }
  };

  const fetchComments = async (postId) => {
    try {
      const response = await axios.get(`http://localhost:8081/posts/${postId}/comments`);
      setCommentsMap(prev => ({ ...prev, [postId]: response.data }));
    } catch (error) {
      console.error('Error fetching comments:', error);
    }
  };

  const handleCommentSubmit = async (postId) => {
    try {
      const content = newCommentMap[postId];
      await axios.post(
        `http://localhost:8081/posts/${postId}/comments`,
        content,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'text/plain',
          },
        }
      );
      setNewCommentMap(prev => ({ ...prev, [postId]: '' }));
      fetchComments(postId);
    } catch (err) {
      console.error("Error submitting comment", err);
    }
  };

  const fetchLikeCount = async (postId) => {
    try {
      const res = await axios.get(`http://localhost:8081/posts/${postId}/likes/count`);
      setLikeCounts(prev => ({ ...prev, [postId]: res.data }));
    } catch (err) {
      console.error("Error fetching like count", err);
    }
  };

  const toggleLike = async (postId) => {
    try {
 await axios.post(
  `http://localhost:8081/posts/${postId}/likes/toggle`,
  {}, // important: send empty object
  {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  }
);

      fetchLikeCount(postId);
    } catch (err) {
      console.error("Error toggling like", err);
    }
  };

  return (
    <div className="publicposts-container">
      <div className="publicposts-header">
        <h2>My Public Posts</h2>
      </div>

      {message && <p className="publicposts-message">{message}</p>}

      {posts.length === 0 ? (
        <p className="publicposts-empty">You have no public posts yet.</p>
      ) : (
        posts.map((post) => (
          <div key={post.id} className="publicposts-card">
            <p className="publicposts-creator"><strong>Creator:</strong> {post.creatorName}</p>
            <p className="publicposts-description"><strong>Description:</strong> {post.description}</p>
            <div className="publicposts-media">
              {post.mediaUrls && post.mediaUrls.length > 0 &&
                post.mediaUrls.map((mediaUrl, idx) => (
                  mediaUrl.endsWith('.mp4') || mediaUrl.endsWith('.mov') ? (
                    <video key={idx} width="300" controls>
                      <source src={`http://localhost:8081/uploads/${mediaUrl}`} />
                      Your browser does not support the video tag.
                    </video>
                  ) : (
                    <img
                      key={idx}
                      src={`http://localhost:8081/uploads/${mediaUrl}`}
                      alt={`Post media ${idx}`}
                      width="300"
                    />
                  )
                ))
              }
            </div>

            <div className="publicposts-likes">
              <button onClick={() => toggleLike(post.id)}>❤️ Like</button>
              <span>{likeCounts[post.id] || 0} Likes</span>
            </div>

            <div className="publicposts-comments">
              <h4>Comments</h4>
              {commentsMap[post.id]?.map((comment) => (
                <div key={comment.id} className="comment">
                  <p><strong>{comment.user?.name}:</strong> {comment.content}</p>
                </div>
              ))}

              <input
                type="text"
                value={newCommentMap[post.id] || ''}
                onChange={(e) => setNewCommentMap(prev => ({ ...prev, [post.id]: e.target.value }))}
                placeholder="Write a comment..."
              />
              <button onClick={() => handleCommentSubmit(post.id)}>Comment</button>
            </div>
          </div>
        ))
      )}
    </div>
  );
};

export default PublicPosts;
