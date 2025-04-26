// src/context/AuthContext.js
import React, { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../services/authService';

const AuthContext = createContext();

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const verifyToken = async () => {
      const storedToken = localStorage.getItem('token');
      if (!storedToken) {
        setLoading(false);
        return;
      }

      try {
        console.log('Verifying token:', storedToken);
        const response = await fetch('http://localhost:8081/api/auth/verify', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${storedToken}`,
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          credentials: 'include',
          mode: 'cors' // Add this line
        });

        if (!response.ok) {
          throw new Error(`Token verification failed with status: ${response.status}`);
        }

        const user = JSON.parse(localStorage.getItem('user'));
        if (!user) {
          throw new Error('No user data found in localStorage');
        }

        setCurrentUser(user);
        setToken(storedToken);
        console.log('Token verified, user set:', user); // Debug log
      } catch (error) {
        console.error('Token verification failed:', error.message);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setCurrentUser(null);
        setToken(null);
        navigate('/login');
      } finally {
        setLoading(false);
      }
    };

    verifyToken();
  }, [navigate]);

  const login = async (email, password) => {
    try {
      console.log('Attempting login with:', { email }); // Debug log
      const data = await authService.login(email, password);
      setToken(data.token);
      setCurrentUser(data);
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data));
      console.log('Login successful:', data); // Debug log
      return data;
    } catch (error) {
      console.error('Login error:', error.message);
      throw new Error(error.message || 'Login failed');
    }
  };

  const signup = async (userData) => {
    try {
      console.log('Starting signup process with:', userData); // Debug log
      const response = await authService.signup(userData);
      console.log('Signup successful:', response); // Debug log
      // Don't set user or token here - user must login after signup
      return response;
    } catch (error) {
      console.error('Signup error in context:', error.message);
      throw new Error(error.message || 'Failed to create account');
    }
  };

  const logout = () => {
    console.log('Logging out user:', currentUser?.email); // Debug log
    setCurrentUser(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const resetPassword = async (email, newPassword) => {
    try {
      console.log('Attempting password reset for:', email); // Debug log
      const success = await authService.resetPassword(email, newPassword);
      console.log('Password reset result:', success); // Debug log
      return success;
    } catch (error) {
      console.error('Reset password error:', error.message);
      throw new Error(error.message || 'Failed to reset password');
    }
  };

  const updateUser = async (userData) => {
    try {
      const response = await authService.updateUser(userData);
      setCurrentUser(prev => ({
        ...prev,
        ...userData
      }));
      return response;
    } catch (error) {
      throw new Error(error.message || 'Failed to update profile');
    }
  };

  const value = {
    currentUser,
    token,
    login,
    signup,
    logout,
    resetPassword,
    updateUser,
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};