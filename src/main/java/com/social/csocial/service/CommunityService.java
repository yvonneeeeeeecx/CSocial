package com.social.csocial.service;

import com.social.csocial.entity.Community;
import com.social.csocial.entity.CommunityMember;
import com.social.csocial.entity.User;
import com.social.csocial.repository.CommunityAnnouncementRepository;
import com.social.csocial.repository.CommunityMemberRepository;
import com.social.csocial.repository.CommunityRepository;
import com.social.csocial.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityMemberRepository communityMemberRepository;

    @Autowired
    private CommunityAnnouncementRepository communityAnnouncementRepository;


    @Autowired
    private UserRepository userRepository;


    public Community create(Long creatorId, String name, String description, String category,
                            String location, LocalDateTime startTime, LocalDateTime endTime, Integer maxMembers,
                            Boolean joinApprovalRequired) {
        if (creatorId == null) {
            throw new RuntimeException("创建者不能为空");
        }
        if (name == null || name.isBlank()) {
            throw new RuntimeException("社群名称不能为空");
        }
        if (description == null || description.isBlank()) {
            throw new RuntimeException("社群描述不能为空");
        }
        if (category == null || category.isBlank()) {
            throw new RuntimeException("社群分类不能为空");
        }
        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new RuntimeException("结束时间不能早于开始时间");
        }
        userRepository.findById(creatorId).orElseThrow(() -> new RuntimeException("创建者不存在"));

        Community community = new Community();
        community.setCreatorId(creatorId);
        community.setName(name.trim());
        community.setDescription(description.trim());
        community.setCategory(category.trim());
        community.setLocation(location == null ? null : location.trim());
        community.setStartTime(startTime);
        community.setEndTime(endTime);
        community.setMaxMembers(maxMembers);
        community.setJoinApprovalRequired(joinApprovalRequired == null || joinApprovalRequired);
        community.setStatus(computeStatus(startTime, endTime));
        Community saved = communityRepository.save(community);

        CommunityMember owner = new CommunityMember();
        owner.setCommunityId(saved.getId());
        owner.setUserId(creatorId);
        owner.setRole("OWNER");
        owner.setStatus("APPROVED");
        communityMemberRepository.save(owner);

        return saved;
    }

    public List<CommunityView> list(String keyword, String category, String status) {
        String keywordText = keyword == null ? "" : keyword.trim().toLowerCase();
        String categoryText = category == null ? "" : category.trim();
        String statusText = status == null ? "" : status.trim().toUpperCase();

        List<Community> communities = communityRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> keywordText.isEmpty() ||
                        (item.getName() != null && item.getName().toLowerCase().contains(keywordText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(keywordText)))
                .filter(item -> categoryText.isEmpty() || categoryText.equalsIgnoreCase(item.getCategory()))
                .toList();

        return buildViews(communities, statusText, statusText.isEmpty(), null);
    }

    public List<CommunityView> listCreated(Long userId) {
        List<Community> list = communityRepository.findByCreatorIdOrderByCreatedAtDesc(userId);
        return buildViews(list, "", false, null);
    }

    public List<CommunityView> listJoined(Long userId) {
        List<CommunityMember> members = communityMemberRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Long> communityIds = members.stream().map(CommunityMember::getCommunityId).distinct().toList();
        if (communityIds.isEmpty()) {
            return List.of();
        }
        Map<Long, String> joinStatusMap = members.stream()
                .collect(Collectors.toMap(CommunityMember::getCommunityId, CommunityMember::getStatus, (a, b) -> a));
        List<Community> communities = communityRepository.findAllById(communityIds);
        return buildViews(communities, "", false, joinStatusMap);
    }

    @Transactional
    public void deleteCommunity(Long communityId, Long operatorUserId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("社群不存在"));
        if (!community.getCreatorId().equals(operatorUserId)) {
            throw new RuntimeException("无权限删除该社群");
        }

        communityAnnouncementRepository.deleteByCommunityId(communityId);
        communityMemberRepository.deleteByCommunityId(communityId);
        communityRepository.deleteById(communityId);
    }

    public Community getById(Long communityId) {
        return communityRepository.findById(communityId).orElseThrow(() -> new RuntimeException("社群不存在"));
    }


    private List<CommunityView> buildViews(List<Community> communities, String statusFilter, boolean hideExpired,
                                           Map<Long, String> joinStatusMap) {
        if (communities == null || communities.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = communities.stream().map(Community::getCreatorId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));

        return communities.stream().map(item -> {
            String status = computeStatus(item.getStartTime(), item.getEndTime());
            if (!statusFilter.isEmpty() && !statusFilter.equalsIgnoreCase(status)) {
                return null;
            }
            if (hideExpired && "EXPIRED".equalsIgnoreCase(status)) {
                return null;
            }
            CommunityView view = new CommunityView();
            view.setId(item.getId());
            view.setName(item.getName());
            view.setDescription(item.getDescription());
            view.setCategory(item.getCategory());
            view.setLocation(item.getLocation());
            view.setStartTime(item.getStartTime());
            view.setEndTime(item.getEndTime());
            view.setMaxMembers(item.getMaxMembers());
            view.setStatus(status);
            view.setJoinApprovalRequired(item.getJoinApprovalRequired());
            view.setCreatorId(item.getCreatorId());
            view.setCreatedAt(item.getCreatedAt());
            view.setMemberCount(communityMemberRepository.countByCommunityIdAndStatus(item.getId(), "APPROVED"));
            if (joinStatusMap != null) {
                view.setJoinStatus(joinStatusMap.get(item.getId()));
            }

            User user = userMap.get(item.getCreatorId());
            if (user != null) {
                view.setCreatorName(user.getNickname() == null ? user.getUsername() : user.getNickname());
                view.setCreatorAvatar(user.getAvatar());
            }
            return view;
        }).filter(item -> item != null).toList();
    }

    private String computeStatus(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        if (endTime != null && endTime.isBefore(now)) {
            return "EXPIRED";
        }
        return "OPEN";
    }

    public static class CommunityView {
        private Long id;
        private String name;
        private String description;
        private String category;
        private String location;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer maxMembers;
        private String status;
        private String joinStatus;
        private Boolean joinApprovalRequired;
        private Long creatorId;
        private String creatorName;
        private String creatorAvatar;
        private long memberCount;
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public Integer getMaxMembers() { return maxMembers; }
        public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getJoinStatus() { return joinStatus; }
        public void setJoinStatus(String joinStatus) { this.joinStatus = joinStatus; }
        public Boolean getJoinApprovalRequired() { return joinApprovalRequired; }
        public void setJoinApprovalRequired(Boolean joinApprovalRequired) { this.joinApprovalRequired = joinApprovalRequired; }
        public Long getCreatorId() { return creatorId; }
        public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
        public String getCreatorName() { return creatorName; }
        public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
        public String getCreatorAvatar() { return creatorAvatar; }
        public void setCreatorAvatar(String creatorAvatar) { this.creatorAvatar = creatorAvatar; }
        public long getMemberCount() { return memberCount; }
        public void setMemberCount(long memberCount) { this.memberCount = memberCount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
