import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const PrivateRoute = ({ children }) => {
  const { currentUser } = useAuth();

  // If there's no authenticated user, redirect to login
  if (!currentUser) {
    return <Navigate to="/login" />;
  }

  // If there is a user, render the protected component
  return children;
};

export default PrivateRoute; 