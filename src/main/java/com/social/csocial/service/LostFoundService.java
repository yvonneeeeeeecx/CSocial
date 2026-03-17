package com.social.csocial.service;

import com.social.csocial.entity.LostFoundItem;
import com.social.csocial.entity.User;
import com.social.csocial.repository.LostFoundItemRepository;
import com.social.csocial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LostFoundService {

    @Autowired
    private LostFoundItemRepository lostFoundItemRepository;

    @Autowired
    private UserRepository userRepository;

    public LostFoundItem publish(Long reporterId, String title, String content, String category,
                                 String location, String contact, String status, String imageUrl) {
        if (reporterId == null) {
            throw new RuntimeException("发布者不能为空");
        }
        if (title == null || title.isBlank()) {
            throw new RuntimeException("标题不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new RuntimeException("内容不能为空");
        }
        if (category == null || category.isBlank()) {
            throw new RuntimeException("分类不能为空");
        }

        LostFoundItem item = new LostFoundItem();
        item.setReporterId(reporterId);
        item.setTitle(title.trim());
        item.setContent(content.trim());
        item.setCategory(category.trim());
        item.setLocation(location == null ? null : location.trim());
        item.setContact(contact == null ? null : contact.trim());
        item.setImageUrl(imageUrl == null ? null : imageUrl.trim());
        if (status != null && !status.isBlank()) {
            item.setStatus(status.trim().toUpperCase());
        }
        return lostFoundItemRepository.save(item);
    }

    public List<LostFoundView> list(String category) {
        String categoryText = category == null ? "" : category.trim();
        List<LostFoundItem> items = lostFoundItemRepository.findAllByOrderByCreatedAtDesc();
        if (!categoryText.isEmpty()) {
            items = items.stream()
                    .filter(item -> categoryText.equalsIgnoreCase(item.getCategory()))
                    .toList();
        }
        return buildViews(items);
    }

    public void deleteLostFound(Long itemId, Long operatorUserId) {
        LostFoundItem item = lostFoundItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("失物招领信息不存在"));
        if (!item.getReporterId().equals(operatorUserId)) {
            throw new RuntimeException("无权限删除该失物招领信息");
        }
        lostFoundItemRepository.deleteById(itemId);
    }

    private List<LostFoundView> buildViews(List<LostFoundItem> items) {

        if (items == null || items.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = items.stream().map(LostFoundItem::getReporterId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));

        return items.stream().map(item -> {
            LostFoundView view = new LostFoundView();
            view.setId(item.getId());
            view.setReporterId(item.getReporterId());
            view.setTitle(item.getTitle());
            view.setContent(item.getContent());
            view.setCategory(item.getCategory());
            view.setLocation(item.getLocation());
            view.setContact(item.getContact());
            view.setImageUrl(item.getImageUrl());
            view.setStatus(item.getStatus());
            view.setCreatedAt(item.getCreatedAt());

            User user = userMap.get(item.getReporterId());
            if (user != null) {
                view.setUsername(user.getUsername());
                view.setNickname(user.getNickname());
                view.setAvatar(user.getAvatar());
            }
            return view;
        }).toList();
    }

    public static class LostFoundView {
        private Long id;
        private Long reporterId;
        private String title;
        private String content;
        private String category;
        private String location;
        private String contact;
        private String imageUrl;
        private String status;
        private LocalDateTime createdAt;
        private String username;
        private String nickname;
        private String avatar;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getReporterId() { return reporterId; }
        public void setReporterId(Long reporterId) { this.reporterId = reporterId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
    }
}
