package com.social.csocial.service;

import com.social.csocial.entity.CampusActivity;
import com.social.csocial.entity.User;
import com.social.csocial.repository.CampusActivityRepository;
import com.social.csocial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CampusActivityService {

    @Autowired
    private CampusActivityRepository campusActivityRepository;

    @Autowired
    private UserRepository userRepository;

    public CampusActivity publish(Long organizerId, String title, String content, String category,
                                  String location, String contact, LocalDateTime startTime, LocalDateTime endTime) {
        if (organizerId == null) {
            throw new RuntimeException("发布者不能为空");
        }
        if (title == null || title.isBlank()) {
            throw new RuntimeException("活动标题不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new RuntimeException("活动内容不能为空");
        }
        if (category == null || category.isBlank()) {
            throw new RuntimeException("活动分类不能为空");
        }
        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new RuntimeException("结束时间不能早于开始时间");
        }

        CampusActivity activity = new CampusActivity();
        activity.setOrganizerId(organizerId);
        activity.setTitle(title.trim());
        activity.setContent(content.trim());
        activity.setCategory(category.trim());
        activity.setLocation(location == null ? null : location.trim());
        activity.setContact(contact == null ? null : contact.trim());
        activity.setStartTime(startTime);
        activity.setEndTime(endTime);
        return campusActivityRepository.save(activity);
    }

    public List<ActivityView> list(String category) {
        String categoryText = category == null ? "" : category.trim();
        List<CampusActivity> activities = campusActivityRepository.findAllByOrderByCreatedAtDesc();
        if (!categoryText.isEmpty()) {
            activities = activities.stream()
                    .filter(item -> categoryText.equalsIgnoreCase(item.getCategory()))
                    .toList();
        }
        return buildViews(activities);
    }

    public void deleteActivity(Long activityId, Long operatorUserId) {
        CampusActivity activity = campusActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("活动不存在"));
        if (!activity.getOrganizerId().equals(operatorUserId)) {
            throw new RuntimeException("无权限删除该活动");
        }
        campusActivityRepository.deleteById(activityId);
    }

    private List<ActivityView> buildViews(List<CampusActivity> activities) {

        if (activities == null || activities.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = activities.stream().map(CampusActivity::getOrganizerId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));

        return activities.stream().map(item -> {
            ActivityView view = new ActivityView();
            view.setId(item.getId());
            view.setOrganizerId(item.getOrganizerId());
            view.setTitle(item.getTitle());
            view.setContent(item.getContent());
            view.setCategory(item.getCategory());
            view.setLocation(item.getLocation());
            view.setContact(item.getContact());
            view.setStartTime(item.getStartTime());
            view.setEndTime(item.getEndTime());
            view.setCreatedAt(item.getCreatedAt());

            User user = userMap.get(item.getOrganizerId());
            if (user != null) {
                view.setUsername(user.getUsername());
                view.setNickname(user.getNickname());
                view.setAvatar(user.getAvatar());
            }
            return view;
        }).toList();
    }

    public static class ActivityView {
        private Long id;
        private Long organizerId;
        private String title;
        private String content;
        private String category;
        private String location;
        private String contact;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private LocalDateTime createdAt;
        private String username;
        private String nickname;
        private String avatar;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getOrganizerId() { return organizerId; }
        public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }
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
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
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
