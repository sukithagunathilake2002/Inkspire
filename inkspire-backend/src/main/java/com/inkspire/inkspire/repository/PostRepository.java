package com.inkspire.inkspire.repository;


import com.inkspire.inkspire.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}