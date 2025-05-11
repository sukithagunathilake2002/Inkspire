package com.inkspire.inkspire.service;

import java.util.List;
import com.inkspire.inkspire.model.Comment;

public interface CommentService {

    List<Comment> getCommentsForPost(Long postId);

    Comment addCommentToPost(Long postId, String content, String commentBy, String commentById, String commentByProfile);

    void deleteComment(Long postId, Long commentId);

    Comment editComment(Long commentId, String content);
}
