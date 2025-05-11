package com.inkspire.inkspire.service;

import com.inkspire.inkspire.model.Like;
import com.inkspire.inkspire.model.Post;
import com.inkspire.inkspire.model.User;
import com.inkspire.inkspire.repository.LikeRepository;
import com.inkspire.inkspire.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepo;
    private final PostRepository postRepo;

    public boolean toggleLike(Long postId, User user) {
        if (likeRepo.existsByPostIdAndUserId(postId, user.getId())) {
            likeRepo.deleteByPostIdAndUserId(postId, user.getId());
            return false; // unliked
        } else {
            Post post = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepo.save(like);
            return true; // liked
        }
    }

    public long getLikeCount(Long postId) {
        return likeRepo.countByPostId(postId);
    }
}
