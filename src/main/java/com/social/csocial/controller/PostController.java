package com.social.csocial.controller;

import com.social.csocial.entity.Post;
import com.social.csocial.entity.PostComment;
import com.social.csocial.service.PostService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(summary = "发布动态", description = "发布校园图文动态")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(
            @Parameter(description = "发布用户ID") @RequestParam Long userId,
            @Parameter(description = "动态标题") @RequestParam String title,
            @Parameter(description = "动态内容") @RequestParam String content,
            @Parameter(description = "图片地址") @RequestParam(required = false) String imageUrl) {

        Post post = postService.createPost(userId, title, content, imageUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "发布成功");
        response.put("data", post);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "动态列表", description = "按时间倒序获取全站动态")
    @GetMapping
    public ResponseEntity<Map<String, Object>> listPosts() {
        List<PostService.PostView> posts = postService.listPostViews();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", posts);
        response.put("count", posts.size());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "分页动态列表", description = "分页获取全站动态")
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> pagePosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<Post> pageData = postService.pagePosts(page, size);
        List<PostService.PostView> postViews = postService.buildPostViews(pageData.getContent());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", postViews);
        response.put("page", page);
        response.put("size", size);
        response.put("total", pageData.getTotalElements());
        response.put("totalPages", pageData.getTotalPages());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "用户动态", description = "获取某用户发布的动态")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> listPostsByUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {

        List<PostService.PostView> posts = postService.listPostViewsByUser(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", posts);
        response.put("count", posts.size());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "点赞动态", description = "给动态点赞")
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> likePost(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        Post post = postService.likePost(postId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "点赞成功");
        response.put("data", post);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "评论动态", description = "给动态发表评论")
    @PostMapping("/{postId}/comment")
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam String content) {

        PostComment comment = postService.addComment(postId, userId, content);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "评论成功");
        response.put("data", comment);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "评论列表", description = "获取动态评论列表")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Map<String, Object>> listComments(@PathVariable Long postId) {
        List<PostService.CommentView> comments = postService.listComments(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", comments);
        response.put("count", comments.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "删除评论", description = "仅评论者可删除")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long operatorUserId) {
        postService.deleteComment(commentId, operatorUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "点赞用户列表", description = "获取动态点赞用户列表")
    @GetMapping("/{postId}/likes")
    public ResponseEntity<Map<String, Object>> listLikes(@PathVariable Long postId) {
        List<PostService.LikeUserView> likes = postService.listLikeUsers(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", likes);
        response.put("count", likes.size());
        return ResponseEntity.ok(response);
    }



    @Operation(summary = "删除动态", description = "仅发布者可删除动态")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "操作用户ID") @RequestParam Long operatorUserId) {

        postService.deletePost(postId, operatorUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
