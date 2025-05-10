import React, { useEffect, useState } from 'react';
import axios from 'axios';

const PublicFeed = () => {
  const [posts, setPosts] = useState([]);
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchPublicPosts();
  }, []);

  const fetchPublicPosts = async () => {
    try {
      const response = await axios.get('http://localhost:8081/api/posts/public');
      setPosts(response.data);
    } catch (error) {
      console.error('Error fetching public posts:', error);
      setMessage('Failed to load public posts');
    }
  };

  return (
    <div className="public-feed-container" style={{ padding: '20px' }}>
      <h2>Public Feed</h2>
      {message && <p>{message}</p>}

      {posts.length === 0 ? (
        <p>No public posts available.</p>
      ) : (
        posts.map((post) => (
          <div key={post.id} className="post-card" style={{ marginBottom: '20px', border: '1px solid #ccc', padding: '10px' }}>
            <p>
              <strong>Creator:</strong> {post.creatorName}
            </p>
            <p>
              <strong>Description:</strong> {post.description}
            </p>
            <div>
              {post.mediaUrls && post.mediaUrls.length > 0 &&
                post.mediaUrls.map((mediaUrl, idx) => (
                  mediaUrl.endsWith('.mp4') || mediaUrl.endsWith('.mov') ? (
                    <video key={idx} width="300" controls style={{ marginBottom: '10px' }}>
                      <source src={`http://localhost:8081/uploads/${mediaUrl}`} />
                    </video>
                  ) : (
                    <img
                      key={idx}
                      src={`http://localhost:8081/uploads/${mediaUrl}`}
                      alt={`Post media ${idx}`}
                      width="300"
                      style={{ marginBottom: '10px' }}
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

export default PublicFeed;
