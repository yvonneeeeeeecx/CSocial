package com.social.csocial.service;

import com.social.csocial.entity.Community;
import com.social.csocial.entity.CommunityAnnouncement;
import com.social.csocial.entity.User;
import com.social.csocial.repository.CommunityAnnouncementRepository;
import com.social.csocial.repository.CommunityRepository;
import com.social.csocial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommunityAnnouncementService {

    @Autowired
    private CommunityAnnouncementRepository communityAnnouncementRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    public CommunityAnnouncement publish(Long communityId, Long authorId, String title, String content) {
        if (title == null || title.isBlank()) {
            throw new RuntimeException("公告标题不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new RuntimeException("公告内容不能为空");
        }

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("社群不存在"));
        if (!community.getCreatorId().equals(authorId)) {
            throw new RuntimeException("仅社群创建者可以发布公告");
        }
        userRepository.findById(authorId).orElseThrow(() -> new RuntimeException("用户不存在"));

        CommunityAnnouncement announcement = new CommunityAnnouncement();
        announcement.setCommunityId(communityId);
        announcement.setAuthorId(authorId);
        announcement.setTitle(title.trim());
        announcement.setContent(content.trim());
        return communityAnnouncementRepository.save(announcement);
    }

    public List<AnnouncementView> list(Long communityId) {
        List<CommunityAnnouncement> list = communityAnnouncementRepository.findByCommunityIdOrderByCreatedAtDesc(communityId);
        if (list.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = list.stream().map(CommunityAnnouncement::getAuthorId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));

        return list.stream().map(item -> {
            AnnouncementView view = new AnnouncementView();
            view.setId(item.getId());
            view.setCommunityId(item.getCommunityId());
            view.setAuthorId(item.getAuthorId());
            view.setTitle(item.getTitle());
            view.setContent(item.getContent());
            view.setCreatedAt(item.getCreatedAt());
            User user = userMap.get(item.getAuthorId());
            if (user != null) {
                view.setNickname(user.getNickname());
                view.setUsername(user.getUsername());
            }
            return view;
        }).toList();
    }

    public void delete(Long communityId, Long announcementId, Long operatorUserId) {
        CommunityAnnouncement announcement = communityAnnouncementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("社群公告不存在"));
        if (!announcement.getCommunityId().equals(communityId)) {
            throw new RuntimeException("社群公告不存在");
        }
        if (!announcement.getAuthorId().equals(operatorUserId)) {
            throw new RuntimeException("无权限删除该社群公告");
        }
        communityAnnouncementRepository.deleteById(announcementId);
    }

    public static class AnnouncementView {

        private Long id;
        private Long communityId;
        private Long authorId;
        private String title;
        private String content;
        private String nickname;
        private String username;
        private java.time.LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getCommunityId() { return communityId; }
        public void setCommunityId(Long communityId) { this.communityId = communityId; }
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
