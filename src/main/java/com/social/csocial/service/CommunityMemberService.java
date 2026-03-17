package com.social.csocial.service;

import com.social.csocial.entity.Community;
import com.social.csocial.entity.CommunityMember;
import com.social.csocial.entity.User;
import com.social.csocial.repository.CommunityMemberRepository;
import com.social.csocial.repository.CommunityRepository;
import com.social.csocial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommunityMemberService {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityMemberRepository communityMemberRepository;

    @Autowired
    private UserRepository userRepository;

    public String join(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("社群不存在"));
        if (communityMemberRepository.existsByCommunityIdAndUserId(communityId, userId)) {
            throw new RuntimeException("已提交过申请或已加入该社群");
        }
        if (community.getEndTime() != null && community.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("社群已过期");
        }
        if (community.getMaxMembers() != null) {
            long count = communityMemberRepository.countByCommunityIdAndStatus(communityId, "APPROVED");
            if (count >= community.getMaxMembers()) {
                throw new RuntimeException("社群人数已满");
            }
        }
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        boolean needApproval = community.getJoinApprovalRequired() == null || community.getJoinApprovalRequired();
        CommunityMember member = new CommunityMember();
        member.setCommunityId(communityId);
        member.setUserId(userId);
        member.setRole("MEMBER");
        member.setStatus(needApproval ? "PENDING" : "APPROVED");
        communityMemberRepository.save(member);
        return member.getStatus();
    }

    public void leave(Long communityId, Long userId) {
        CommunityMember member = communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new RuntimeException("未加入该社群"));
        if ("OWNER".equalsIgnoreCase(member.getRole())) {
            throw new RuntimeException("创建者不能退出社群");
        }
        communityMemberRepository.delete(member);
    }

    public void approve(Long communityId, Long userId, Long operatorId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("社群不存在"));
        if (!community.getCreatorId().equals(operatorId)) {
            throw new RuntimeException("无权限审核该社群");
        }
        CommunityMember member = communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new RuntimeException("未找到该成员"));
        if ("OWNER".equalsIgnoreCase(member.getRole())) {
            throw new RuntimeException("不能审核创建者");
        }
        if (!"PENDING".equalsIgnoreCase(member.getStatus())) {
            throw new RuntimeException("该成员无需审核");
        }
        if (community.getMaxMembers() != null) {
            long count = communityMemberRepository.countByCommunityIdAndStatus(communityId, "APPROVED");
            if (count >= community.getMaxMembers()) {
                throw new RuntimeException("社群人数已满");
            }
        }
        member.setStatus("APPROVED");
        communityMemberRepository.save(member);
    }

    public void reject(Long communityId, Long userId, Long operatorId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("社群不存在"));
        if (!community.getCreatorId().equals(operatorId)) {
            throw new RuntimeException("无权限审核该社群");
        }
        CommunityMember member = communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new RuntimeException("未找到该成员"));
        if ("OWNER".equalsIgnoreCase(member.getRole())) {
            throw new RuntimeException("不能审核创建者");
        }
        if (!"PENDING".equalsIgnoreCase(member.getStatus())) {
            throw new RuntimeException("该成员无需审核");
        }
        communityMemberRepository.delete(member);
    }

    public List<MemberView> listMembers(Long communityId) {
        List<CommunityMember> members = communityMemberRepository
                .findByCommunityIdAndStatusOrderByCreatedAtDesc(communityId, "APPROVED");
        return buildMemberViews(members);
    }

    public List<MemberView> listPending(Long communityId, Long operatorId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("社群不存在"));
        if (!community.getCreatorId().equals(operatorId)) {
            throw new RuntimeException("无权限查看审核列表");
        }
        List<CommunityMember> members = communityMemberRepository
                .findByCommunityIdAndStatusOrderByCreatedAtDesc(communityId, "PENDING");
        return buildMemberViews(members);
    }

    private List<MemberView> buildMemberViews(List<CommunityMember> members) {
        if (members == null || members.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = members.stream().map(CommunityMember::getUserId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));

        return members.stream().map(item -> {
            MemberView view = new MemberView();
            view.setUserId(item.getUserId());
            view.setRole(item.getRole());
            view.setStatus(item.getStatus());
            view.setJoinedAt(item.getCreatedAt());
            User user = userMap.get(item.getUserId());
            if (user != null) {
                view.setNickname(user.getNickname());
                view.setUsername(user.getUsername());
                view.setAvatar(user.getAvatar());
            }
            return view;
        }).toList();
    }

    public boolean isJoined(Long communityId, Long userId) {
        return communityMemberRepository.existsByCommunityIdAndUserIdAndStatus(communityId, userId, "APPROVED");
    }

    public static class MemberView {
        private Long userId;
        private String username;
        private String nickname;
        private String avatar;
        private String role;
        private String status;
        private LocalDateTime joinedAt;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getJoinedAt() { return joinedAt; }
        public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    }
}
