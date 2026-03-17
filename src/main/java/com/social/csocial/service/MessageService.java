package com.social.csocial.service;

import com.social.csocial.entity.Message;
import com.social.csocial.entity.User;
import com.social.csocial.repository.MessageRepository;
import com.social.csocial.repository.UserRelationRepository;
import com.social.csocial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRelationRepository userRelationRepository;

    public MessageView sendMessage(Long fromUserId, Long toUserId, String content) {
        if (fromUserId == null || toUserId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (fromUserId.equals(toUserId)) {
            throw new RuntimeException("不能给自己发送私信");
        }
        if (content == null || content.isBlank()) {
            throw new RuntimeException("私信内容不能为空");
        }
        if (userRelationRepository.existsByFromUserIdAndToUserIdAndRelationType(fromUserId, toUserId, "block")
                || userRelationRepository.existsByFromUserIdAndToUserIdAndRelationType(toUserId, fromUserId, "block")) {
            throw new RuntimeException("对方已被拉黑，无法发送私信");
        }

        Message message = new Message(fromUserId, toUserId, content.trim());
        Message saved = messageRepository.save(message);
        return toView(saved, loadUserMap(List.of(fromUserId, toUserId)));
    }

    public List<MessageView> listConversation(Long userId, Long targetId) {
        if (userId == null || targetId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        List<Message> messages = messageRepository.findConversation(userId, targetId);
        Map<Long, User> userMap = loadUserMap(List.of(userId, targetId));
        return messages.stream().map(item -> toView(item, userMap)).toList();
    }

    public List<ConversationView> listConversations(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        List<Message> messages = messageRepository.findByUser(userId);
        Map<Long, Message> latestByPeer = new java.util.LinkedHashMap<>();
        for (Message message : messages) {
            Long peerId = message.getFromUserId().equals(userId) ? message.getToUserId() : message.getFromUserId();
            if (!latestByPeer.containsKey(peerId)) {
                latestByPeer.put(peerId, message);
            }
        }
        List<Long> peerIds = latestByPeer.keySet().stream().toList();
        Map<Long, User> userMap = loadUserMap(peerIds);

        return peerIds.stream().map(peerId -> {
            Message message = latestByPeer.get(peerId);
            User peer = userMap.get(peerId);
            ConversationView view = new ConversationView();
            view.setPeerId(peerId);
            view.setLastMessage(message == null ? null : message.getContent());
            view.setLastMessageAt(message == null ? null : message.getCreatedAt());
            if (peer != null) {
                view.setPeerUsername(peer.getUsername());
                view.setPeerNickname(peer.getNickname());
                view.setPeerAvatar(peer.getAvatar());
            }
            return view;
        }).toList();
    }

    private Map<Long, User> loadUserMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));
    }


    private MessageView toView(Message message, Map<Long, User> userMap) {
        MessageView view = new MessageView();
        view.setId(message.getId());
        view.setFromUserId(message.getFromUserId());
        view.setToUserId(message.getToUserId());
        view.setContent(message.getContent());
        view.setCreatedAt(message.getCreatedAt());

        User fromUser = userMap.get(message.getFromUserId());
        if (fromUser != null) {
            view.setFromUsername(fromUser.getUsername());
            view.setFromNickname(fromUser.getNickname());
            view.setFromAvatar(fromUser.getAvatar());
        }
        return view;
    }

    public static class MessageView {
        private Long id;
        private Long fromUserId;
        private Long toUserId;
        private String content;
        private LocalDateTime createdAt;
        private String fromUsername;
        private String fromNickname;
        private String fromAvatar;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getFromUserId() { return fromUserId; }
        public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }
        public Long getToUserId() { return toUserId; }
        public void setToUserId(Long toUserId) { this.toUserId = toUserId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public String getFromUsername() { return fromUsername; }
        public void setFromUsername(String fromUsername) { this.fromUsername = fromUsername; }
        public String getFromNickname() { return fromNickname; }
        public void setFromNickname(String fromNickname) { this.fromNickname = fromNickname; }
        public String getFromAvatar() { return fromAvatar; }
        public void setFromAvatar(String fromAvatar) { this.fromAvatar = fromAvatar; }
    }

    public static class ConversationView {
        private Long peerId;
        private String peerUsername;
        private String peerNickname;
        private String peerAvatar;
        private String lastMessage;
        private LocalDateTime lastMessageAt;

        public Long getPeerId() { return peerId; }
        public void setPeerId(Long peerId) { this.peerId = peerId; }
        public String getPeerUsername() { return peerUsername; }
        public void setPeerUsername(String peerUsername) { this.peerUsername = peerUsername; }
        public String getPeerNickname() { return peerNickname; }
        public void setPeerNickname(String peerNickname) { this.peerNickname = peerNickname; }
        public String getPeerAvatar() { return peerAvatar; }
        public void setPeerAvatar(String peerAvatar) { this.peerAvatar = peerAvatar; }
        public String getLastMessage() { return lastMessage; }
        public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
        public LocalDateTime getLastMessageAt() { return lastMessageAt; }
        public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    }
}

