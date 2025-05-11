package com.inkspire.inkspire.controller;

import com.inkspire.inkspire.model.Comment;
import com.inkspire.inkspire.model.User;
import com.inkspire.inkspire.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final PostCommentService commentService;

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId));
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestBody String content
    ) {
        return ResponseEntity.ok(commentService.addComment(postId, user, content));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> editComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user,
            @RequestBody String newContent
    ) {
        Comment updated = commentService.updateComment(commentId, user, newContent);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user
    ) {
        commentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build();
    }
}
