import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Paper, TextField, Button, Typography } from '@mui/material';
import '../styles/Profile.css';

const Profile = () => {
  const { currentUser } = useAuth();
  const [formData, setFormData] = useState({
    name: '',
    phoneNumber: '',
  });
  const [isEditing, setIsEditing] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (currentUser) {
      const storedUser = JSON.parse(localStorage.getItem('user'));
      setFormData({
        name: currentUser.name || '',
        phoneNumber: storedUser?.phoneNumber || currentUser.phoneNumber || '',
      });
    }
  }, [currentUser]);

  const handleChange = (e) => {
    e.preventDefault(); // Prevent any default behavior
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleEdit = (e) => {
    e.preventDefault(); // Prevent form submission
    setIsEditing(true);
  };

  const handleCancel = (e) => {
    e.preventDefault(); // Prevent form submission
    setIsEditing(false);
    // Reset form data to current user data
    const storedUser = JSON.parse(localStorage.getItem('user'));
    setFormData({
      name: currentUser.name || '',
      phoneNumber: storedUser?.phoneNumber || currentUser.phoneNumber || '',
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent form submission
    e.stopPropagation(); // Stop event bubbling

    try {
      const response = await fetch('http://localhost:8081/api/auth/profile', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(formData)
      });

      if (!response.ok) {
        throw new Error('Failed to update profile');
      }

      const updatedData = await response.json();
      
      // Update local storage with new data
      const currentStoredUser = JSON.parse(localStorage.getItem('user'));
      const updatedUser = { ...currentStoredUser, ...formData };
      localStorage.setItem('user', JSON.stringify(updatedUser));

      // Only set isEditing to false after successful update
      setIsEditing(false);
      setError('');
    } catch (err) {
      console.error('Error updating profile:', err);
      setError(err.message || 'Failed to update profile');
      // Don't change isEditing state on error
    }
  };

  if (!currentUser) {
    return <div>Loading...</div>;
  }

  return (
    <div className="profile-container">
      <Paper elevation={3} className="profile-paper">
        <Typography variant="h4" gutterBottom>
          Profile
        </Typography>

        <form onSubmit={handleSubmit} className="profile-form">
          <div className="profile-field">
            <TextField
              label="Name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              disabled={!isEditing}
              fullWidth
              margin="normal"
              variant="outlined"
            />
          </div>

          <div className="profile-field">
            <TextField
              label="Email"
              value={currentUser.email || ''}
              disabled
              fullWidth
              margin="normal"
              variant="outlined"
              helperText="Email cannot be changed"
            />
          </div>

          <div className="profile-field">
            <TextField
              label="Phone Number"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              disabled={!isEditing}
              fullWidth
              margin="normal"
              variant="outlined"
            />
          </div>

          {error && (
            <Typography color="error" className="error-message">
              {error}
            </Typography>
          )}

          <div className="profile-actions">
            {isEditing ? (
              <>
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                >
                  Save Changes
                </Button>
                <Button
                  type="button"
                  variant="outlined"
                  onClick={handleCancel}
                >
                  Cancel
                </Button>
              </>
            ) : (
              <Button
                type="button"
                variant="contained"
                color="primary"
                onClick={handleEdit}
              >
                Edit Profile
              </Button>
            )}
          </div>
        </form>
      </Paper>
    </div>
  );
};

export default Profile; 