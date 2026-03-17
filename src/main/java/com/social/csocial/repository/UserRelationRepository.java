package com.social.csocial.repository;

import com.social.csocial.entity.UserRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, Long> {

    // 查找特定关系
    Optional<UserRelation> findByFromUserIdAndToUserIdAndRelationType(Long fromUserId, Long toUserId, String relationType);

    // 查找用户发出的所有关系
    List<UserRelation> findByFromUserIdAndRelationType(Long fromUserId, String relationType);

    // 查找用户收到的所有关系
    List<UserRelation> findByToUserIdAndRelationType(Long toUserId, String relationType);

    // 检查关系是否存在
    boolean existsByFromUserIdAndToUserIdAndRelationType(Long fromUserId, Long toUserId, String relationType);

    // 删除关系
    void deleteByFromUserIdAndToUserIdAndRelationType(Long fromUserId, Long toUserId, String relationType);

    // 查找互相喜欢的关系（匹配）
    @Query("SELECT ur1.fromUserId FROM UserRelation ur1 " +
            "WHERE ur1.relationType = 'like' " +
            "AND ur1.toUserId = :userId " +
            "AND EXISTS (SELECT 1 FROM UserRelation ur2 " +
            "WHERE ur2.relationType = 'like' " +
            "AND ur2.fromUserId = :userId " +
            "AND ur2.toUserId = ur1.fromUserId)")
    List<Long> findMatchedUserIds(@Param("userId") Long userId);
}