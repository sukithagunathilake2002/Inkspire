package com.inkspire.inkspire.service;

import com.inkspire.inkspire.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserService extends UserDetailsService {
    User registerUser(User user);
    String loginUser(String email, String password);
    User updateUser(User user);
    void deleteUser(Long userId);
    User getUserById(Long userId);
    User getUserByEmail(String email);
    boolean resetPassword(String email, String newPassword);
    User processOAuth2User(OAuth2User oAuth2User, String provider);
    boolean isPhoneNumberTaken(String phoneNumber);
}