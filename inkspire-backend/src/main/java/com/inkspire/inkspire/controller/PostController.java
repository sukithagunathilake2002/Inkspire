package com.inkspire.inkspire.controller;

import com.inkspire.inkspire.model.Post;
import com.inkspire.inkspire.service.impl.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody @Valid Post post) {
        if (post == null) {
            return ResponseEntity.badRequest().body("Post data is missing.");
        }
    
        if (post.getMediaUrls() == null || post.getMediaUrls().isEmpty()) {
            return ResponseEntity.badRequest().body("At least one media URL must be provided.");
        }
    
        if (post.getIsVideo() == null) {
            return ResponseEntity.badRequest().body("Post type (isVideo) must be specified.");
        }
    
        if (post.getIsVideo()) {
            // It's a video post
            if (post.getMediaUrls().size() != 1) {
                return ResponseEntity.badRequest().body("A video post must contain exactly one media URL.");
            }
        } else {
            // It's an image post
            if (post.getMediaUrls().size() > 3) {
                return ResponseEntity.badRequest().body("An image post cannot have more than 3 media URLs.");
            }
        }
    
        Post savedPost = postService.savePost(post);
        return ResponseEntity.ok(savedPost);
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok("Post deleted");
    }

    @PutMapping("/{id}")
public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody @Valid Post updatedPost) {
    Post existingPost = postService.getPostById(id);

    if (existingPost == null) {
        return ResponseEntity.notFound().build();
    }

    // Update fields manually
    existingPost.setCaption(updatedPost.getCaption());
    existingPost.setIsVideo(updatedPost.getIsVideo());
    existingPost.setMediaUrls(updatedPost.getMediaUrls());

    Post savedPost = postService.savePost(existingPost);
    return ResponseEntity.ok(savedPost);
}

}