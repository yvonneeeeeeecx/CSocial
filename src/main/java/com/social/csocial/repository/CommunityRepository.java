package com.social.csocial.repository;

import com.social.csocial.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    List<Community> findAllByOrderByCreatedAtDesc();

    List<Community> findByCreatorIdOrderByCreatedAtDesc(Long creatorId);
}
