package com.social.csocial.repository;

import com.social.csocial.entity.CommunityAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityAnnouncementRepository extends JpaRepository<CommunityAnnouncement, Long> {

    List<CommunityAnnouncement> findByCommunityIdOrderByCreatedAtDesc(Long communityId);

    void deleteByCommunityId(Long communityId);
}

