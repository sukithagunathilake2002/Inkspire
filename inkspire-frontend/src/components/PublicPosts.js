import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/PublicPosts.css';

const PublicPosts = () => {
  const [posts, setPosts] = useState([]);
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
    } catch (error) {
      console.error('Error fetching posts:', error);
      setMessage('Failed to load your public posts.');
    }
  };

  return (
    <div className="publicposts-container">
      <div className="publicposts-header">
        <h2>My Public Posts</h2>
        <button className="publicposts-newpost-btn" onClick={() => navigate('/newpost')}>
          + New Post
        </button>
      </div>

      {message && <p className="publicposts-message">{message}</p>}

      {posts.length === 0 ? (
        <p className="publicposts-empty">You have no public posts yet.</p>
      ) : (
        posts.map((post) => (
          <div key={post.id} className="publicposts-card">
            <p className="publicposts-creator"><strong></strong> {post.creatorName}</p>
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
          </div>
        ))
      )}
    </div>
  );
};

export default PublicPosts;
