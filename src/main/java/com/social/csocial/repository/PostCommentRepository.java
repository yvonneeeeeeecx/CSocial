package com.social.csocial.repository;

import com.social.csocial.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    List<PostComment> findByPostIdOrderByCreatedAtDesc(Long postId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
