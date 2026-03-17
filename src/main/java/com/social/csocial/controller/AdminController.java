package com.social.csocial.controller;

import com.social.csocial.entity.Announcement;
import com.social.csocial.entity.Post;
import com.social.csocial.entity.User;
import com.social.csocial.service.AdminReportService;
import com.social.csocial.service.AnnouncementService;
import com.social.csocial.service.PostService;
import com.social.csocial.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AdminReportService adminReportService;


    @Operation(summary = "管理员仪表盘")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(@RequestParam Long adminUserId) {
        Map<String, Object> stats = adminReportService.getOverview(adminUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "管理报表-趋势")
    @GetMapping("/reports/trend")
    public ResponseEntity<Map<String, Object>> trend(
            @RequestParam Long adminUserId,
            @RequestParam(defaultValue = "7") Integer days) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", adminReportService.getTrend(adminUserId, days));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "用户管理-列表")
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestParam Long adminUserId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {

        List<User> users = userService.listUsersByAdmin(adminUserId, keyword, status);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", users);
        response.put("count", users.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "用户管理-更新状态")
    @PutMapping("/users/{targetUserId}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @RequestParam Long adminUserId,
            @PathVariable Long targetUserId,
            @RequestParam String status) {

        User user = userService.updateUserStatusByAdmin(adminUserId, targetUserId, status);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "状态更新成功");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "内容管理-动态列表")
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> listPosts(
            @RequestParam Long adminUserId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String keyword) {

        userService.assertAdmin(adminUserId);
        List<Post> posts = postService.listPostsForAdmin(userId, keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", posts);
        response.put("count", posts.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "内容管理-删除动态")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(
            @RequestParam Long adminUserId,
            @PathVariable Long postId) {

        userService.assertAdmin(adminUserId);
        postService.forceDeletePost(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "动态已删除");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "公告管理-发布")
    @PostMapping("/announcements")
    public ResponseEntity<Map<String, Object>> publishAnnouncement(
            @RequestParam Long adminUserId,
            @RequestParam String title,
            @RequestParam String content) {

        Announcement announcement = announcementService.publish(adminUserId, title, content);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "公告发布成功");
        response.put("data", announcement);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "公告管理-删除")
    @DeleteMapping("/announcements/{announcementId}")
    public ResponseEntity<Map<String, Object>> deleteAnnouncement(
            @RequestParam Long adminUserId,
            @PathVariable Long announcementId) {

        announcementService.delete(adminUserId, announcementId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "公告已删除");
        return ResponseEntity.ok(response);
    }
}
