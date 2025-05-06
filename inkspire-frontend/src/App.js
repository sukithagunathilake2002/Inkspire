import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import Login from './components/auth/Login';
import Signup from './components/auth/Signup';
import ForgotPassword from './components/auth/ForgotPassword';
import Header from './components/Header';
import Footer from './components/Footer';
import PlanList from './components/PlanList';
import CreatePlan from './components/CreatePlan';
import Reminders from './components/Reminders';
import Dashboard from './components/Dashboard';
import Profile from './components/Profile';
import LearningPlans from './components/LearningPlans';
import PrivateRoute from './components/PrivateRoute';
import PostList from './components/PostList';
import NewPost from './components/NewPost';
// Removed GoogleOAuthProvider import as it's not being used

function App() {
  return (
    <Router>
      <NotificationProvider>
        <AuthProvider>
          <div className="app">
            <Header />
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/signup" element={<Signup />} />
              <Route path="/forgot-password" element={<ForgotPassword />} />
              
              <Route
                path="/"
                element={
                  <PrivateRoute>
                    <Dashboard />
                  </PrivateRoute>
                }
              />
              
              <Route
                path="/learning-plans"
                element={
                  <PrivateRoute>
                    <LearningPlans />
                  </PrivateRoute>
                }
              />

              <Route
                path="/plans"
                element={
                  <PrivateRoute>
                    <PlanList />
                  </PrivateRoute>
                }
              />

              <Route
                path="/create-plan"
                element={
                  <PrivateRoute>
                    <CreatePlan />
                  </PrivateRoute>
                }
              />

              <Route
                path="/reminders"
                element={
                  <PrivateRoute>
                    <Reminders />
                  </PrivateRoute>
                }
              />

              <Route
                path="/profile"
                element={
                  <PrivateRoute>
                    <Profile />
                  </PrivateRoute>
                }
              />
              <Route path="/" element={<PostList />} />
              <Route path="/newpost" element={<NewPost />} />
              
            </Routes>
            <Footer />
          </div>
        </AuthProvider>
      </NotificationProvider>
    </Router>
  );
}

export default App;