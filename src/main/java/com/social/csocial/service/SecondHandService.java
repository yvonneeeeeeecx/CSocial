package com.social.csocial.service;

import com.social.csocial.entity.SecondHandItem;
import com.social.csocial.entity.User;
import com.social.csocial.repository.SecondHandItemRepository;
import com.social.csocial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SecondHandService {

    @Autowired
    private SecondHandItemRepository secondHandItemRepository;

    @Autowired
    private UserRepository userRepository;

    public SecondHandItem publish(Long sellerId, String title, String content, String category,
                                  Double price, String imageUrl, String contact, String status) {
        if (sellerId == null) {
            throw new RuntimeException("发布者不能为空");
        }
        if (title == null || title.isBlank()) {
            throw new RuntimeException("商品标题不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new RuntimeException("商品描述不能为空");
        }
        if (category == null || category.isBlank()) {
            throw new RuntimeException("商品分类不能为空");
        }
        if (price == null || price < 0) {
            throw new RuntimeException("商品价格不能为空");
        }

        SecondHandItem item = new SecondHandItem();
        item.setSellerId(sellerId);
        item.setTitle(title.trim());
        item.setContent(content.trim());
        item.setCategory(category.trim());
        item.setPrice(price);
        item.setImageUrl(imageUrl == null ? null : imageUrl.trim());
        item.setContact(contact == null ? null : contact.trim());
        if (status != null && !status.isBlank()) {
            item.setStatus(status.trim().toUpperCase());
        }
        return secondHandItemRepository.save(item);
    }

    public List<SecondHandView> list(String category) {
        String categoryText = category == null ? "" : category.trim();
        List<SecondHandItem> items = secondHandItemRepository.findAllByOrderByCreatedAtDesc();
        if (!categoryText.isEmpty()) {
            items = items.stream()
                    .filter(item -> categoryText.equalsIgnoreCase(item.getCategory()))
                    .toList();
        }
        return buildViews(items);
    }

    public void deleteSecondHand(Long itemId, Long operatorUserId) {
        SecondHandItem item = secondHandItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("二手信息不存在"));
        if (!item.getSellerId().equals(operatorUserId)) {
            throw new RuntimeException("无权限删除该二手信息");
        }
        secondHandItemRepository.deleteById(itemId);
    }

    private List<SecondHandView> buildViews(List<SecondHandItem> items) {

        if (items == null || items.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = items.stream().map(SecondHandItem::getSellerId).distinct().toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));

        return items.stream().map(item -> {
            SecondHandView view = new SecondHandView();
            view.setId(item.getId());
            view.setSellerId(item.getSellerId());
            view.setTitle(item.getTitle());
            view.setContent(item.getContent());
            view.setCategory(item.getCategory());
            view.setPrice(item.getPrice());
            view.setImageUrl(item.getImageUrl());
            view.setContact(item.getContact());
            view.setStatus(item.getStatus());
            view.setCreatedAt(item.getCreatedAt());

            User user = userMap.get(item.getSellerId());
            if (user != null) {
                view.setUsername(user.getUsername());
                view.setNickname(user.getNickname());
                view.setAvatar(user.getAvatar());
            }
            return view;
        }).toList();
    }

    public static class SecondHandView {
        private Long id;
        private Long sellerId;
        private String title;
        private String content;
        private String category;
        private Double price;
        private String imageUrl;
        private String contact;
        private String status;
        private LocalDateTime createdAt;
        private String username;
        private String nickname;
        private String avatar;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getSellerId() { return sellerId; }
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
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
