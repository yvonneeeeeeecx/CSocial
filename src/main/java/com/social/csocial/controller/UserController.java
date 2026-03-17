package com.social.csocial.controller;

import com.social.csocial.entity.User;
import com.social.csocial.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册", description = "创建新用户账号")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Parameter(description = "用户名") @RequestParam String username,
            @Parameter(description = "密码") @RequestParam String password,
            @Parameter(description = "昵称") @RequestParam String nickname,
            @Parameter(description = "邮箱") @RequestParam(required = false) String email) {

        User user = userService.register(username, password, nickname, email);
        user.setPassword(null);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "注册成功");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "用户登录", description = "用户登录验证")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Parameter(description = "用户名") @RequestParam String username,
            @Parameter(description = "密码") @RequestParam String password) {

        User user = userService.login(username, password);
        user.setPassword(null);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登录成功");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户基本信息")
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @Parameter(description = "用户ID") @PathVariable Long userId) {

        User user = userService.getUserById(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "更新用户信息", description = "更新用户个人资料")
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "昵称") @RequestParam(required = false) String nickname,
            @Parameter(description = "个人简介") @RequestParam(required = false) String profile,
            @Parameter(description = "兴趣标签") @RequestParam(required = false) String interests,
            @Parameter(description = "学院") @RequestParam(required = false) String college,
            @Parameter(description = "专业") @RequestParam(required = false) String major,
            @Parameter(description = "年级") @RequestParam(required = false) Integer grade) {

        User user = userService.updateUser(userId, nickname, profile, interests, college, major, grade);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "更新成功");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "更新心动铃设置", description = "更新用户的心动铃开关与半径")
    @PutMapping("/{userId}/alarm-settings")
    public ResponseEntity<Map<String, Object>> updateAlarmSetting(
            @PathVariable Long userId,
            @RequestParam(required = false) Boolean alarmEnabled,
            @RequestParam(required = false) Double alarmRadiusKm) {

        User user = userService.updateAlarmSetting(userId, alarmEnabled, alarmRadiusKm);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "心动铃设置已更新");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "校园用户列表", description = "获取校园用户列表")
    @GetMapping("/campus")
    public ResponseEntity<Map<String, Object>> listCampusUsers(
            @RequestParam Long userId,
            @RequestParam(required = false) String keyword) {
        java.util.List<User> users = userService.listCampusUsers(userId, keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", users);
        response.put("count", users.size());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "检查用户名", description = "检查用户名是否已被注册")
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(
            @Parameter(description = "用户名") @RequestParam String username) {


        boolean exists = userService.checkUsernameExists(username);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("exists", exists);
        response.put("message", exists ? "用户名已存在" : "用户名可用");
        return ResponseEntity.ok(response);
    }
}
