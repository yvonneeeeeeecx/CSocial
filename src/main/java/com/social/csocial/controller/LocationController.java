package com.social.csocial.controller;

import com.social.csocial.entity.UserLocation;
import com.social.csocial.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "*") // 允许所有跨域请求
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * 更新用户位置
     */

    @Operation(summary = "/更新用户位置")
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateLocation(
            @RequestParam Long userId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        try {
            UserLocation location = locationService.updateUserLocation(userId, latitude, longitude);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "位置更新成功");
            response.put("data", location);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "位置更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取附近用户
     */
    @Operation(summary = "/获取附近用户位置",description = "/获取附近用户位置")
    @GetMapping("/nearby")
    public ResponseEntity<Map<String, Object>> getNearbyUsers(
            @RequestParam Long userId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "0.1") Double radius) { // 默认100米

        try {
            List<Map<String, Object>> nearbyUsers =
                    locationService.findNearbyUsers(userId, latitude, longitude, radius);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取附近用户成功");
            response.put("data", nearbyUsers);
            response.put("count", nearbyUsers.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取附近用户失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取用户位置
     */
    @Operation(summary = "/获取用户位置",description = "/获取用户位置")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserLocation(@PathVariable Long userId) {
        try {
            UserLocation location = locationService.getUserLocation(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", location);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取用户位置失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}