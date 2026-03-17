package com.social.csocial.controller;

import com.social.csocial.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Operation(summary = "发送私信", description = "给指定用户发送一条私信")
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @Parameter(description = "发送者ID") @RequestParam Long fromUserId,
            @Parameter(description = "接收者ID") @RequestParam Long toUserId,
            @Parameter(description = "私信内容") @RequestParam String content) {

        MessageService.MessageView view = messageService.sendMessage(fromUserId, toUserId, content);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "私信已发送");
        response.put("data", view);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "会话消息", description = "获取与某用户的私信会话")
    @GetMapping("/conversation")
    public ResponseEntity<Map<String, Object>> listConversation(
            @Parameter(description = "当前用户ID") @RequestParam Long userId,
            @Parameter(description = "目标用户ID") @RequestParam Long targetId) {

        List<MessageService.MessageView> messages = messageService.listConversation(userId, targetId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", messages);
        response.put("count", messages.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "会话列表", description = "获取用户的私信会话列表")
    @GetMapping("/conversations")
    public ResponseEntity<Map<String, Object>> listConversations(
            @Parameter(description = "当前用户ID") @RequestParam Long userId) {

        List<MessageService.ConversationView> conversations = messageService.listConversations(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", conversations);
        response.put("count", conversations.size());
        return ResponseEntity.ok(response);
    }
}

