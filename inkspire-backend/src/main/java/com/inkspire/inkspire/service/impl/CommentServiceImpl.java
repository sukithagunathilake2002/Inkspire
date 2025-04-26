package com.inkspire.inkspire.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.inkspire.inkspire.model.Comment;
import com.inkspire.inkspire.repository.CommentRepository;
import com.inkspire.inkspire.service.CommentService;

public class CommentServiceImpl implements CommentService{

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final PostRepository postRepository;

    // Retrieves all comments associated with a post
    @Override
    public List<Comment> getCommentsForPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // Adds a new comment to a post
    @Override
    public Comment addCommentToPost(Long postId, String content, String commentBy, String commentById, String commentByProfile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCommentBy(commentBy);
        comment.setCommentById(commentById);
        comment.setCommentByProfile(commentByProfile);
        comment.setCreatedAt(new Date()); // Set current Date object
        comment.setPost(post); // Set the whole post object reference

        return commentRepository.save(comment);
    }

    // Deletes a comment
    @Override
    public void deleteComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        if (!comment.getPost().getId().equals(postId)) {
            throw new RuntimeException("Comment does not belong to the specified post");
        }
        commentRepository.delete(comment);
    }

    // Edits the content of a comment
    @Override
    public Comment editComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        comment.setContent(content);
        return commentRepository.save(comment);
    }
}
    
