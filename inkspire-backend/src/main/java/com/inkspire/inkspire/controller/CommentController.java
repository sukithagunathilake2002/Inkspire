package com.inkspire.inkspire.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inkspire.inkspire.Dto.CommentDto;
import com.inkspire.inkspire.model.Comment;
import com.inkspire.inkspire.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // GET: Get all comments for a post
    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsForPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsForPost(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // POST: Add a comment to a post
    @PostMapping
    public ResponseEntity<Comment> addCommentToPost(
            @PathVariable Long postId,
            @RequestBody CommentDto request
    ) {
        Comment comment = commentService.addCommentToPost(
                postId,
                request.getContent(),
                request.getCommentBy(),
                request.getCommentById(),
                request.getCommentByProfile()
        );
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    // DELETE: Remove a comment from a post
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(postId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // PUT: Edit a comment
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> editComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto request
    ) {
        Comment editedComment = commentService.editComment(commentId, request.getContent());
        if (editedComment != null) {
            return new ResponseEntity<>(editedComment, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}