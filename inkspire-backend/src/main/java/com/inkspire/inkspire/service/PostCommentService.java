package com.inkspire.inkspire.service;

import com.inkspire.inkspire.model.Comment;
import com.inkspire.inkspire.model.Post;
import com.inkspire.inkspire.model.User;
import com.inkspire.inkspire.repository.CommentRepository;
import com.inkspire.inkspire.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final CommentRepository commentRepo;
    private final PostRepository postRepo;

    public List<Comment> getCommentsForPost(Long postId) {
        return commentRepo.findByPostId(postId);
    }

    public Comment addComment(Long postId, User user, String content) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setPost(post);
        return commentRepo.save(comment);
    }

    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepo.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own comments.");
        }
        commentRepo.delete(comment);
    }

    public Comment updateComment(Long commentId, User user, String newContent) {
    Comment comment = commentRepo.findById(commentId)
        .orElseThrow(() -> new RuntimeException("Comment not found"));

    if (!comment.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("You can only edit your own comments.");
    }

    comment.setContent(newContent);
    return commentRepo.save(comment);
}

}
