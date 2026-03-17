package com.social.csocial.repository;

import com.social.csocial.entity.CommunityMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    boolean existsByCommunityIdAndUserId(Long communityId, Long userId);

    boolean existsByCommunityIdAndUserIdAndStatus(Long communityId, Long userId, String status);

    Optional<CommunityMember> findByCommunityIdAndUserId(Long communityId, Long userId);

    Optional<CommunityMember> findByCommunityIdAndUserIdAndStatus(Long communityId, Long userId, String status);

    long countByCommunityId(Long communityId);

    long countByCommunityIdAndStatus(Long communityId, String status);

    List<CommunityMember> findByCommunityIdOrderByCreatedAtDesc(Long communityId);

    List<CommunityMember> findByCommunityIdAndStatusOrderByCreatedAtDesc(Long communityId, String status);

    List<CommunityMember> findByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByCommunityId(Long communityId);
}

