package com.social.csocial.repository;

import com.social.csocial.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Post> findAllByOrderByCreatedAtDesc();

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
