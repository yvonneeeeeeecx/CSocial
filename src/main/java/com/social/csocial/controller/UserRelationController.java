package com.social.csocial.controller;

import com.social.csocial.entity.UserRelation;
import com.social.csocial.service.UserRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relation")
@CrossOrigin(origins = "*")
public class UserRelationController {

    @Autowired
    private UserRelationService userRelationService;

    /**
     * 喜欢某个用户
     */
    @Operation(summary = "标记用户", description = "标记某个用户，如果对方也喜欢你则会匹配成功")
    @PostMapping("/like/{toUserId}")
    public ResponseEntity<Map<String, Object>> likeUser(
            @Parameter(description = "当前用户ID") @RequestParam Long fromUserId,
            @Parameter(description = "目标用户ID") @PathVariable Long toUserId) {

        try {
            UserRelation relation = userRelationService.likeUser(fromUserId, toUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "标记成功");
            response.put("data", relation);

            // 检查是否匹配成功
            boolean isMatched = userRelationService.isMatched(fromUserId, toUserId);
            response.put("matched", isMatched);
            if (isMatched) {
                response.put("message", "标记成功！你们互相标记，匹配成功！🎉");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 取消喜欢
     */
    @Operation(summary = "取消标记", description = "取消对某个用户的标记")
    @DeleteMapping("/like/{toUserId}")
    public ResponseEntity<Map<String, Object>> unlikeUser(
            @Parameter(description = "当前用户ID") @RequestParam Long fromUserId,
            @Parameter(description = "目标用户ID") @PathVariable Long toUserId) {

        try {
            userRelationService.unlikeUser(fromUserId, toUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "取消标记成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 关注用户
     */
    @Operation(summary = "关注用户", description = "关注某个用户")
    @PostMapping("/follow/{toUserId}")
    public ResponseEntity<Map<String, Object>> followUser(
            @Parameter(description = "当前用户ID") @RequestParam Long fromUserId,
            @Parameter(description = "目标用户ID") @PathVariable Long toUserId) {

        try {
            UserRelation relation = userRelationService.followUser(fromUserId, toUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "关注成功");
            response.put("data", relation);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 取消关注
     */
    @Operation(summary = "取消关注", description = "取消对某个用户的关注")
    @DeleteMapping("/follow/{toUserId}")
    public ResponseEntity<Map<String, Object>> unfollowUser(
            @Parameter(description = "当前用户ID") @RequestParam Long fromUserId,
            @Parameter(description = "目标用户ID") @PathVariable Long toUserId) {

        try {
            userRelationService.unfollowUser(fromUserId, toUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "取消关注成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 拉黑用户
     */
    @Operation(summary = "拉黑用户", description = "将某个用户加入黑名单")
    @PostMapping("/block/{toUserId}")
    public ResponseEntity<Map<String, Object>> blockUser(
            @Parameter(description = "当前用户ID") @RequestParam Long fromUserId,
            @Parameter(description = "目标用户ID") @PathVariable Long toUserId) {

        try {
            UserRelation relation = userRelationService.blockUser(fromUserId, toUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "已加入黑名单");
            response.put("data", relation);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 取消拉黑
     */
    @Operation(summary = "取消拉黑", description = "将某个用户移出黑名单")
    @DeleteMapping("/block/{toUserId}")
    public ResponseEntity<Map<String, Object>> unblockUser(
            @Parameter(description = "当前用户ID") @RequestParam Long fromUserId,
            @Parameter(description = "目标用户ID") @PathVariable Long toUserId) {

        try {
            userRelationService.unblockUser(fromUserId, toUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "已移出黑名单");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取我喜欢的人列表
     */

    @Operation(summary = "获取我标记的人", description = "获取当前用户标记的所有用户列表")
    @GetMapping("/liked")
    public ResponseEntity<Map<String, Object>> getLikedUsers(
            @Parameter(description = "用户ID") @RequestParam Long userId) {

        try {
            List<Long> likedUserIds = userRelationService.getLikedUsers(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", likedUserIds);
            response.put("count", likedUserIds.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取喜欢我的人列表
     */
    @Operation(summary = "获取标记我的人", description = "获取所有标记当前用户的用户列表")
    @GetMapping("/liked-by")
    public ResponseEntity<Map<String, Object>> getLikedByUsers(
            @Parameter(description = "用户ID") @RequestParam Long userId) {

        try {
            List<Long> likedByUserIds = userRelationService.getLikedByUsers(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", likedByUserIds);
            response.put("count", likedByUserIds.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取关注的人列表
     */
    @Operation(summary = "获取关注的人", description = "获取当前用户关注的用户列表")
    @GetMapping("/following")
    public ResponseEntity<Map<String, Object>> getFollowingUsers(
            @Parameter(description = "用户ID") @RequestParam Long userId) {

        try {
            List<Long> followingIds = userRelationService.getFollowingUsers(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", followingIds);
            response.put("count", followingIds.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取关注我的人列表
     */
    @Operation(summary = "获取关注我的人", description = "获取所有关注当前用户的用户列表")
    @GetMapping("/followers")
    public ResponseEntity<Map<String, Object>> getFollowers(
            @Parameter(description = "用户ID") @RequestParam Long userId) {

        try {
            List<Long> followerIds = userRelationService.getFollowers(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", followerIds);
            response.put("count", followerIds.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取互相关注用户列表
     */
    @Operation(summary = "获取互相关注", description = "获取互相关注的用户列表")
    @GetMapping("/mutual-follow")
    public ResponseEntity<Map<String, Object>> getMutualFollowUsers(
            @Parameter(description = "用户ID") @RequestParam Long userId) {

        try {
            List<Long> mutualIds = userRelationService.getMutualFollowers(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mutualIds);
            response.put("count", mutualIds.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取黑名单用户列表
     */
    @Operation(summary = "获取黑名单", description = "获取当前用户的黑名单列表")
    @GetMapping("/blocked")
    public ResponseEntity<Map<String, Object>> getBlockedUsers(
            @Parameter(description = "用户ID") @RequestParam Long userId) {

        try {
            List<Long> blockedIds = userRelationService.getBlockedUsers(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", blockedIds);
            response.put("count", blockedIds.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取匹配用户列表
     */

    @Operation(summary = "获取匹配用户", description = "获取互相标记的匹配用户列表")
    @GetMapping("/matches")
    public ResponseEntity<Map<String, Object>> getMatchedUsers(
            @Parameter(description = "用户ID") @RequestParam Long userId) {

        try {
            List<Long> matchedUserIds = userRelationService.getMatchedUsers(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", matchedUserIds);
            response.put("count", matchedUserIds.size());
            response.put("message", "找到 " + matchedUserIds.size() + " 个匹配用户");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 检查是否匹配
     */
    @Operation(summary = "检查匹配状态", description = "检查两个用户是否互相标记（匹配）")
    @GetMapping("/check-match")
    public ResponseEntity<Map<String, Object>> checkMatch(
            @Parameter(description = "用户1ID") @RequestParam Long user1Id,
            @Parameter(description = "用户2ID") @RequestParam Long user2Id) {

        try {
            boolean isMatched = userRelationService.isMatched(user1Id, user2Id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("matched", isMatched);
            response.put("message", isMatched ? "你们互相标记！🎉" : "尚未匹配");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}