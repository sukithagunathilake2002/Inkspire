import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles/PublicFeed.css'; // Optional: style uniquely

const BASE_URL = 'http://localhost:8081';

const PublicFeed = () => {
  const [posts, setPosts] = useState([]);
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchPublicPosts();
  }, []);

  const fetchPublicPosts = async () => {
    try {
      const response = await axios.get(`${BASE_URL}/api/posts/public`);
      setPosts(response.data);
    } catch (error) {
      console.error('Error fetching public posts:', error);
      setMessage('Failed to load public posts');
    }
  };

  return (
    <div className="public-feed-container">
      <h2 className="feed-title">Public Feed</h2>
      {message && <p className="feed-message">{message}</p>}

      {posts.length === 0 ? (
        <p className="feed-empty">No public posts available.</p>
      ) : (
        posts.map((post) => (
          <div key={post.id} className="feed-card">
            <p className="feed-creator"><strong>By:</strong> {post.creatorName}</p>
            <div className="feed-media">
              {post.mediaUrls && post.mediaUrls.length > 0 &&
                (post.isVideo ? (
                  <video controls width="100%" className="feed-video">
                    <source src={`${BASE_URL}/uploads/${post.mediaUrls[0]}`} />
                    Your browser does not support the video tag.
                  </video>
                ) : (
                  post.mediaUrls.map((mediaUrl, idx) => (
                    <img
                      key={idx}
                      src={`${BASE_URL}/uploads/${mediaUrl}`}
                      alt={`Post media ${idx}`}
                      className="feed-image"
                    />
                  ))
                ))}
            </div>
            <div className="feed-body">
              <p className="feed-description">{post.description}</p>
              <p className="feed-privacy">{post.private ? 'Private' : 'Public'}</p>
            </div>
          </div>
        ))
      )}
    </div>
  );
};

export default PublicFeed;
