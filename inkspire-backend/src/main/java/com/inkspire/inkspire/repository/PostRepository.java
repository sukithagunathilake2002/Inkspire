package com.inkspire.inkspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inkspire.inkspire.model.Post;

public interface PostRepository  extends JpaRepository<Post, Long>{

    
}
