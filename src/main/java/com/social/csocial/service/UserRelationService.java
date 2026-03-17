
package com.social.csocial.service;

import com.social.csocial.entity.UserRelation;
import com.social.csocial.repository.UserRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Service
public class UserRelationService {

    @Autowired
    private UserRelationRepository userRelationRepository;

    /**
     * 喜欢某个用户
     */
    public UserRelation likeUser(Long fromUserId, Long toUserId) {
        assertNotBlocked(fromUserId, toUserId);

        // 检查是否已经喜欢过
        if (userRelationRepository.existsByFromUserIdAndToUserIdAndRelationType(fromUserId, toUserId, "like")) {
            throw new RuntimeException("已经标记过该用户");
        }

        // 创建喜欢关系
        UserRelation relation = new UserRelation(fromUserId, toUserId, "like");
        UserRelation savedRelation = userRelationRepository.save(relation);

        System.out.println("用户 " + fromUserId + " 标记了用户 " + toUserId);

        // 检查是否互相喜欢（匹配成功）
        checkAndNotifyMatch(fromUserId, toUserId);

        return savedRelation;
    }


    /**
     * 取消喜欢
     */
    @Transactional
    public void unlikeUser(Long fromUserId, Long toUserId) {
        userRelationRepository.deleteByFromUserIdAndToUserIdAndRelationType(fromUserId, toUserId, "like");
        System.out.println("用户 " + fromUserId + " 取消标记用户 " + toUserId);
    }


    /**
     * 关注用户
     */
    public UserRelation followUser(Long fromUserId, Long toUserId) {
        assertNotBlocked(fromUserId, toUserId);

        if (userRelationRepository.existsByFromUserIdAndToUserIdAndRelationType(fromUserId, toUserId, "follow")) {
            throw new RuntimeException("已经关注过该用户");
        }

        UserRelation relation = new UserRelation(fromUserId, toUserId, "follow");
        return userRelationRepository.save(relation);
    }


    /**
     * 取消关注
     */
    public void unfollowUser(Long fromUserId, Long toUserId) {
        userRelationRepository.deleteByFromUserIdAndToUserIdAndRelationType(fromUserId, toUserId, "follow");
    }

    /**
     * 获取我关注的人列表
     */
    public List<Long> getFollowingUsers(Long userId) {
        List<UserRelation> relations = userRelationRepository.findByFromUserIdAndRelationType(userId, "follow");
        List<Long> followingIds = new ArrayList<>();
        for (UserRelation relation : relations) {
            Long targetId = relation.getToUserId();
            if (isBlockedBetween(userId, targetId)) {
                continue;
            }
            followingIds.add(targetId);
        }
        return followingIds;
    }


    /**
     * 获取关注我的人列表
     */
    public List<Long> getFollowers(Long userId) {
        List<UserRelation> relations = userRelationRepository.findByToUserIdAndRelationType(userId, "follow");
        List<Long> followerIds = new ArrayList<>();
        for (UserRelation relation : relations) {
            Long fromId = relation.getFromUserId();
            if (isBlockedBetween(userId, fromId)) {
                continue;
            }
            followerIds.add(fromId);
        }
        return followerIds;
    }


    /**
     * 获取互相关注用户列表
     */
    public List<Long> getMutualFollowers(Long userId) {
        List<Long> followingIds = getFollowingUsers(userId);
        List<Long> mutualIds = new ArrayList<>();
        for (Long targetId : followingIds) {
            boolean followedBack = userRelationRepository
                    .existsByFromUserIdAndToUserIdAndRelationType(targetId, userId, "follow");
            if (followedBack) {
                mutualIds.add(targetId);
            }
        }
        return mutualIds;
    }

    /**
     * 拉黑用户
     */
    @Transactional
    public UserRelation blockUser(Long fromUserId, Long toUserId) {
        if (userRelationRepository.existsByFromUserIdAndToUserIdAndRelationType(fromUserId, toUserId, "block")) {
            throw new RuntimeException("已经拉黑该用户");
        }

        // 拉黑时清理双方的关注与标记关系
        userRelationRepository.deleteByFromUserIdAndToUserIdAndRelationType(fromUserId, toUserId, "follow");
        userRelationRepository.deleteByFromUserIdAndToUserIdAndRelationType(toUserId, fromUserId, "follow");
        userRelationRepository.deleteByFromUserIdAndToUserIdAndRelationType(fromUserId, toUserId, "like");
        userRelationRepository.deleteByFromUserIdAndToUserIdAndRelationType(toUserId, fromUserId, "like");

        UserRelation relation = new UserRelation(fromUserId, toUserId, "block");
        return userRelationRepository.save(relation);
    }



    /**
     * 取消拉黑
     */
    @Transactional
    public void unblockUser(Long fromUserId, Long toUserId) {
        userRelationRepository.deleteByFromUserIdAndToUserIdAndRelationType(fromUserId, toUserId, "block");
    }


    /**
     * 获取拉黑用户列表
     */
    public List<Long> getBlockedUsers(Long userId) {
        List<UserRelation> relations = userRelationRepository.findByFromUserIdAndRelationType(userId, "block");
        List<Long> blockedIds = new ArrayList<>();
        for (UserRelation relation : relations) {
            blockedIds.add(relation.getToUserId());
        }
        return blockedIds;
    }

    /**
     * 获取我喜欢的人列表
     */

    public List<Long> getLikedUsers(Long userId) {
        List<UserRelation> relations = userRelationRepository.findByFromUserIdAndRelationType(userId, "like");
        List<Long> likedUserIds = new ArrayList<>();
        for (UserRelation relation : relations) {
            Long targetId = relation.getToUserId();
            if (isBlockedBetween(userId, targetId)) {
                continue;
            }
            likedUserIds.add(targetId);
        }
        return likedUserIds;
    }


    /**
     * 获取喜欢我的人列表
     */
    public List<Long> getLikedByUsers(Long userId) {
        List<UserRelation> relations = userRelationRepository.findByToUserIdAndRelationType(userId, "like");
        List<Long> likedByUserIds = new ArrayList<>();
        for (UserRelation relation : relations) {
            Long fromId = relation.getFromUserId();
            if (isBlockedBetween(userId, fromId)) {
                continue;
            }
            likedByUserIds.add(fromId);
        }
        return likedByUserIds;
    }


    /**
     * 获取匹配用户列表（互相喜欢）
     */
    public List<Long> getMatchedUsers(Long userId) {
        return userRelationRepository.findMatchedUserIds(userId);
    }

    /**
     * 检查是否互相喜欢
     */
    public boolean isMatched(Long user1Id, Long user2Id) {
        boolean user1LikesUser2 = userRelationRepository
                .existsByFromUserIdAndToUserIdAndRelationType(user1Id, user2Id, "like");
        boolean user2LikesUser1 = userRelationRepository
                .existsByFromUserIdAndToUserIdAndRelationType(user2Id, user1Id, "like");

        return user1LikesUser2 && user2LikesUser1;
    }

    private void assertNotBlocked(Long fromUserId, Long toUserId) {
        if (isBlockedBetween(fromUserId, toUserId)) {
            throw new RuntimeException("双方存在拉黑关系，无法操作");
        }
    }

    private boolean isBlockedBetween(Long userA, Long userB) {
        boolean blockedByA = userRelationRepository.existsByFromUserIdAndToUserIdAndRelationType(userA, userB, "block");
        boolean blockedByB = userRelationRepository.existsByFromUserIdAndToUserIdAndRelationType(userB, userA, "block");
        return blockedByA || blockedByB;
    }


    /**
     * 检查并通知匹配（模拟匹配成功逻辑）
     */
    private void checkAndNotifyMatch(Long fromUserId, Long toUserId) {
        if (isMatched(fromUserId, toUserId)) {
            System.out.println("🎉 匹配成功！用户 " + fromUserId + " 和用户 " + toUserId + " 互相喜欢！");
            // 这里可以添加推送通知、WebSocket消息等
        }
    }
}

