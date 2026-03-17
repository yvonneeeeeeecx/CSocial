package com.social.csocial.repository;

import com.social.csocial.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    void deleteByPostIdAndUserId(Long postId, Long userId);

    List<PostLike> findByPostIdOrderByCreatedAtDesc(Long postId);
}
