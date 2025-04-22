package com.inkspire.inkspire.config;

import com.inkspire.inkspire.model.User;
import com.inkspire.inkspire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Configuration
public class OAuth2Config {
    
    @Autowired
    private UserService userService;
    
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        
        return request -> {
            OAuth2User oAuth2User = delegate.loadUser(request);
            User user = userService.processOAuth2User(oAuth2User, request.getClientRegistration().getRegistrationId());
            
            // Convert your User to OAuth2User
            return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                Map.of("sub", user.getEmail(), "name", user.getName()),
                "sub"
            );
        };
    }
}