package com.social.csocial.controller;

import com.social.csocial.entity.Community;
import com.social.csocial.service.CommunityAnnouncementService;
import com.social.csocial.service.CommunityMemberService;
import com.social.csocial.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@CrossOrigin(origins = "*")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CommunityMemberService communityMemberService;

    @Autowired
    private CommunityAnnouncementService communityAnnouncementService;

    @Operation(summary = "创建社群")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @RequestParam Long creatorId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Integer maxMembers,
            @RequestParam(required = false) Boolean joinApprovalRequired) {

        Community community = communityService.create(creatorId, name, description, category, location, startTime, endTime, maxMembers, joinApprovalRequired);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "社群创建成功");
        response.put("data", community);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "社群列表")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        List<CommunityService.CommunityView> list = communityService.list(keyword, category, status);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", list);
        response.put("count", list.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "我创建的社群")
    @GetMapping("/user/{userId}/created")
    public ResponseEntity<Map<String, Object>> listCreated(@PathVariable Long userId) {
        List<CommunityService.CommunityView> list = communityService.listCreated(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", list);
        response.put("count", list.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "我加入的社群")
    @GetMapping("/user/{userId}/joined")
    public ResponseEntity<Map<String, Object>> listJoined(@PathVariable Long userId) {
        List<CommunityService.CommunityView> list = communityService.listJoined(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", list);
        response.put("count", list.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "删除社群", description = "仅创建者可删除")
    @DeleteMapping("/{communityId}")
    public ResponseEntity<Map<String, Object>> deleteCommunity(
            @PathVariable Long communityId,
            @RequestParam Long operatorUserId) {
        communityService.deleteCommunity(communityId, operatorUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "加入社群")
    @PostMapping("/{communityId}/join")
    public ResponseEntity<Map<String, Object>> join(@PathVariable Long communityId, @RequestParam Long userId) {
        String status = communityMemberService.join(communityId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", status);
        response.put("message", "APPROVED".equalsIgnoreCase(status) ? "报名成功" : "已提交申请，等待审核");
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "退出社群")
    @PostMapping("/{communityId}/leave")
    public ResponseEntity<Map<String, Object>> leave(@PathVariable Long communityId, @RequestParam Long userId) {
        communityMemberService.leave(communityId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已退出社群");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "社群成员")
    @GetMapping("/{communityId}/members")
    public ResponseEntity<Map<String, Object>> members(@PathVariable Long communityId) {
        List<CommunityMemberService.MemberView> list = communityMemberService.listMembers(communityId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", list);
        response.put("count", list.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "社群待审核成员")
    @GetMapping("/{communityId}/members/pending")
    public ResponseEntity<Map<String, Object>> pendingMembers(@PathVariable Long communityId, @RequestParam Long operatorId) {
        List<CommunityMemberService.MemberView> list = communityMemberService.listPending(communityId, operatorId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", list);
        response.put("count", list.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "审核通过")
    @PostMapping("/{communityId}/members/{userId}/approve")
    public ResponseEntity<Map<String, Object>> approve(@PathVariable Long communityId,
                                                       @PathVariable Long userId,
                                                       @RequestParam Long operatorId) {
        communityMemberService.approve(communityId, userId, operatorId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已通过审核");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "审核拒绝")
    @PostMapping("/{communityId}/members/{userId}/reject")
    public ResponseEntity<Map<String, Object>> reject(@PathVariable Long communityId,
                                                      @PathVariable Long userId,
                                                      @RequestParam Long operatorId) {
        communityMemberService.reject(communityId, userId, operatorId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已拒绝申请");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "社群公告发布")
    @PostMapping("/{communityId}/announcements")
    public ResponseEntity<Map<String, Object>> publishAnnouncement(
            @PathVariable Long communityId,
            @RequestParam Long authorId,
            @RequestParam String title,
            @RequestParam String content) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "公告发布成功");
        response.put("data", communityAnnouncementService.publish(communityId, authorId, title, content));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "社群公告列表")
    @GetMapping("/{communityId}/announcements")
    public ResponseEntity<Map<String, Object>> listAnnouncements(@PathVariable Long communityId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", communityAnnouncementService.list(communityId));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "删除社群公告", description = "仅发布者可删除")
    @DeleteMapping("/{communityId}/announcements/{announcementId}")
    public ResponseEntity<Map<String, Object>> deleteAnnouncement(
            @PathVariable Long communityId,
            @PathVariable Long announcementId,
            @RequestParam Long operatorUserId) {
        communityAnnouncementService.delete(communityId, announcementId, operatorUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}

