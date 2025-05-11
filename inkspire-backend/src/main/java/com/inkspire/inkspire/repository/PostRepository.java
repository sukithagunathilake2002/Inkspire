package com.inkspire.inkspire.repository;

import com.inkspire.inkspire.model.Post;
import com.inkspire.inkspire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Find all public posts
    List<Post> findByIsPrivateFalse();

    // Find all posts by a specific user
    List<Post> findByUser(User user);
}
