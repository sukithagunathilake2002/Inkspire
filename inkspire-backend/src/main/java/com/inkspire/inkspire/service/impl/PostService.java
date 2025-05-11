package com.inkspire.inkspire.service.impl;

import com.inkspire.inkspire.model.Post;
import com.inkspire.inkspire.model.User;
import com.inkspire.inkspire.payload.PostResponse;
import com.inkspire.inkspire.repository.PostRepository;
import com.inkspire.inkspire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Post createPost(String description, List<String> mediaUrls, boolean isPrivate, boolean isVideo, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        Post post = new Post();
        post.setDescription(description);
        post.setMediaUrls(mediaUrls);
        post.setPrivate(isPrivate);
        post.setVideo(isVideo);
        post.setUser(user);

        Post savedPost = postRepository.save(post);
        System.out.println("Post saved successfully with ID: " + savedPost.getId());
        return savedPost;
    }

    public List<PostResponse> getPublicPosts() {
        return postRepository.findByIsPrivateFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getUserPosts(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return postRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Post updatePost(Long postId, String newDescription, boolean isPrivate, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own posts");
        }

        post.setDescription(newDescription);
        post.setPrivate(isPrivate);
        Post updatedPost = postRepository.save(post);
        System.out.println("Post updated successfully with ID: " + updatedPost.getId());
        return updatedPost;
    }

    public void deletePost(Long postId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own posts");
        }

        postRepository.delete(post);
        System.out.println("Post deleted successfully with ID: " + postId);
    }

    private PostResponse mapToResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setDescription(post.getDescription());
        response.setMediaUrls(post.getMediaUrls());
        response.setCreatorName(post.getUser().getName());
        response.setPrivate(post.isPrivate());
        response.setVideo(post.isVideo());
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("No authenticated user in context");
        }
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
    }
}
