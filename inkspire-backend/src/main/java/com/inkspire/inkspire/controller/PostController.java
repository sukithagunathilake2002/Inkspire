package com.inkspire.inkspire.controller;

import com.inkspire.inkspire.model.Post;
import com.inkspire.inkspire.payload.PostResponse;
import com.inkspire.inkspire.service.impl.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("description") String description,
            @RequestParam("isPrivate") boolean isPrivate,
            @RequestParam("media") List<MultipartFile> mediaFiles,
            Authentication authentication) {

        try {
            System.out.println("Description: " + description);
            System.out.println("IsPrivate: " + isPrivate);
            System.out.println("Authenticated User: " + (authentication != null ? authentication.getName() : "anonymous"));

            if (mediaFiles == null || mediaFiles.isEmpty()) {
                return ResponseEntity.badRequest().body("No media files provided.");
            }

            if (mediaFiles.size() > 3) {
                return ResponseEntity.badRequest().body("You can only upload up to 3 images or 1 video.");
            }

            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            List<String> savedFileNames = new ArrayList<>();
            boolean isVideo = false;

            for (MultipartFile mediaFile : mediaFiles) {
                if (mediaFile.isEmpty()) continue;

                String originalFilename = mediaFile.getOriginalFilename();
                if (originalFilename == null) continue;

                String fileName = UUID.randomUUID() + "_" + originalFilename;
                File destFile = new File(uploadDir + fileName);
                mediaFile.transferTo(destFile);

                System.out.println("Saved: " + fileName);
                savedFileNames.add(fileName);

                if (originalFilename.endsWith(".mp4") || originalFilename.endsWith(".mov")) {
                    isVideo = true;
                }
            }

            if (isVideo && mediaFiles.size() > 1) {
                return ResponseEntity.badRequest().body("Only one video can be uploaded at a time.");
            }

            Post post = postService.createPost(description, savedFileNames, isPrivate, isVideo, authentication);
            return ResponseEntity.ok("Post created successfully with ID: " + post.getId());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Error while creating post: " + e.getMessage());
        }
    }

    @GetMapping("/public")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<PostResponse>> getPublicPosts() {
        List<PostResponse> posts = postService.getPublicPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<List<PostResponse>> getMyPosts(Authentication authentication) {
        List<PostResponse> posts = postService.getUserPosts(authentication);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @RequestParam("description") String description,
            @RequestParam("isPrivate") boolean isPrivate,
            Authentication authentication) {

        try {
            Post updatedPost = postService.updatePost(postId, description, isPrivate, authentication);
            return ResponseEntity.ok("Post updated successfully. ID: " + updatedPost.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Error while updating post: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Authentication authentication) {
        try {
            postService.deletePost(postId, authentication);
            return ResponseEntity.ok("Post deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Error while deleting post: " + e.getMessage());
        }
    }
}
