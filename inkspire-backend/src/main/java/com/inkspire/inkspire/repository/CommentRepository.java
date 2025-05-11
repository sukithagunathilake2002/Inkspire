package com.inkspire.inkspire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.inkspire.inkspire.model.Comment;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}

