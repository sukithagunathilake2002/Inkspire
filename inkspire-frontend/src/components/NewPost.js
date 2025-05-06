import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/NewPost.css'

function NewPost() {
  const [caption, setCaption] = useState('');
  const [files, setFiles] = useState([]);
  const [isVideo, setIsVideo] = useState(false);
  const navigate = useNavigate();

  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    setFiles(selectedFiles);

    if (selectedFiles.length === 1 && selectedFiles[0].type.startsWith('video/')) {
      setIsVideo(true);
    } else {
      setIsVideo(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {
      const mediaUrls = [];
  
      for (let file of files) {
        const formData = new FormData();
        formData.append('file', file);
  
        const uploadRes = await axios.post('http://localhost:8080/api/upload', formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });
  
        console.log('Upload Response:', uploadRes.data); // 📋 LOG EACH UPLOAD RESPONSE
        mediaUrls.push(uploadRes.data);
      }
  
      console.log('Final mediaUrls to POST:', mediaUrls); // 📋 LOG ALL MEDIA URLS
      console.log('Posting to /api/posts with:', {
        caption,
        isVideo,
        mediaUrls,
      });
  
      const createRes = await axios.post('http://localhost:8080/api/posts', {
        caption,
        isVideo,
        mediaUrls,
      });
  
      console.log('Post creation success:', createRes.data);
  
      alert('Post created successfully!');
      navigate('/');
    } catch (error) {
      console.error('Post creation failed:', error.response ? error.response.data : error.message);
      alert('Failed to create post.');
    }
  };
  

  return (
    <div className="newpost-container">
  <h2 className="newpost-title">Create New Post</h2>
  <form className="newpost-form" onSubmit={handleSubmit}>
    <textarea
      placeholder="Write a caption..."
      value={caption}
      onChange={(e) => setCaption(e.target.value)}
      required
    />
    <input
      type="file"
      multiple
      accept="image/*,video/*"
      onChange={handleFileChange}
    />
    <small className="newpost-note">
      Upload 1 video (max 30s) OR up to 3 images.
    </small>
    <button type="submit" className="newpost-button">Post</button>
  </form>
</div>

  );
}

export default NewPost;
