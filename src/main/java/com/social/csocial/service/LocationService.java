package com.social.csocial.service;

import com.social.csocial.entity.UserLocation;
import com.social.csocial.repository.UserLocationRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationService {

    @Autowired
    private UserLocationRepository userLocationRepository;

    /**
     * 更新用户位置
     */
    @Operation(summary = "/更新用户位置",description = "/更新用户位置")
    public UserLocation updateUserLocation(Long userId, Double latitude, Double longitude) {
        // 查找是否已存在该用户的位置记录
        Optional<UserLocation> existingLocation = userLocationRepository.findByUserId(userId);

        UserLocation userLocation;
        if (existingLocation.isPresent()) {
            // 更新现有位置
            userLocation = existingLocation.get();
            userLocation.setLatitude(latitude);
            userLocation.setLongitude(longitude);
            System.out.println("更新用户 " + userId + " 的位置: " + latitude + ", " + longitude);
        } else {
            // 创建新位置记录
            userLocation = new UserLocation(userId, latitude, longitude);
            System.out.println("创建用户 " + userId + " 的位置: " + latitude + ", " + longitude);
        }

        return userLocationRepository.save(userLocation);
    }

    /**
     * 获取附近用户
     */
    @Operation(summary = "/获取附近用户位置",description = "/获取附近用户位置")
    public List<Map<String, Object>> findNearbyUsers(Long currentUserId, Double currentLat, Double currentLon, Double radiusInKm) {
        // 获取所有用户位置（除当前用户外）
        List<UserLocation> allLocations = userLocationRepository.findAll()
                .stream()
                .filter(location -> !location.getUserId().equals(currentUserId))
                .collect(Collectors.toList());

        List<Map<String, Object>> nearbyUsers = new ArrayList<>();

        for (UserLocation location : allLocations) {
            double distance = calculateDistance(currentLat, currentLon,
                    location.getLatitude(), location.getLongitude());

            // 如果距离在指定范围内
            if (distance <= radiusInKm) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("userId", location.getUserId());
                userInfo.put("distance", Math.round(distance * 1000)); // 转换为米
                userInfo.put("latitude", location.getLatitude());
                userInfo.put("longitude", location.getLongitude());

                nearbyUsers.add(userInfo);
            }
        }

        // 按距离排序
        nearbyUsers.sort(Comparator.comparingDouble(u -> (Double) u.get("distance")));

        System.out.println("用户 " + currentUserId + " 附近找到 " + nearbyUsers.size() + " 个用户");
        return nearbyUsers;
    }

    /**
     * 获取用户位置
     */
    public UserLocation getUserLocation(Long userId) {
        return userLocationRepository.findByUserId(userId).orElse(null);
    }

    /**
     * 计算两个坐标点之间的距离（公里）- Haversine公式
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 地球半径（公里）

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}