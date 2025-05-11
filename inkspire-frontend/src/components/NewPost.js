import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/NewPost.css';

const NewPost = () => {
  const [description, setDescription] = useState('');
  const [isPrivate, setIsPrivate] = useState(false);
  const [mediaFiles, setMediaFiles] = useState([]);
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);

    const videoFiles = files.filter(file => file.type.startsWith('video/'));
    const imageFiles = files.filter(file => file.type.startsWith('image/'));

    if (videoFiles.length > 1) {
      setMessage('You can only upload one video at a time.');
      setMediaFiles([]);
      return;
    }

    if (videoFiles.length === 1 && imageFiles.length > 0) {
      setMessage('You cannot upload both video and images together.');
      setMediaFiles([]);
      return;
    }

    if (imageFiles.length > 3) {
      setMessage('You can upload up to 3 images.');
      setMediaFiles([]);
      return;
    }

    setMediaFiles(files);
    setMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (mediaFiles.length === 0) {
      setMessage('Please select at least one media file.');
      return;
    }

    const formData = new FormData();
    formData.append('description', description);
    formData.append('isPrivate', isPrivate.toString());

    mediaFiles.forEach((file) => {
      formData.append('media', file);
    });

    try {
      await axios.post('http://localhost:8081/api/posts/create', formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data',
        },
      });

      setMessage('Post created successfully!');
      setDescription('');
      setIsPrivate(false);
      setMediaFiles([]);
      document.getElementById('mediaInput').value = '';

      setTimeout(() => navigate('/postlist'), 1500);
    } catch (error) {
      console.error('Error creating post:', error);
      setMessage('Failed to create post.');
    }
  };

  return (
    <div className="new-post-container">
      <h2>Create New Post</h2>
      <form onSubmit={handleSubmit} encType="multipart/form-data">
        <div>
          <label>Description:</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
            rows={4}
            cols={50}
          />
        </div>

        <div>
          <label>Private:</label>
          <input
            type="checkbox"
            checked={isPrivate}
            onChange={(e) => setIsPrivate(e.target.checked)}
          />{' '}
          (Check = Private, Uncheck = Public)
        </div>

        <div>
          <label>Upload Media (up to 3 photos or 1 video, max 30s):</label>
          <input
            id="mediaInput"
            type="file"
            accept="image/*,video/*"
            multiple
            onChange={handleFileChange}
            required
          />
        </div>

        <button type="submit">Post</button>
      </form>

      {message && <p className="message">{message}</p>}
    </div>
  );
};

export default NewPost;
