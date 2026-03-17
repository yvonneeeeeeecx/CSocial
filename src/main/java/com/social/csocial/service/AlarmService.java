package com.social.csocial.service;

import com.social.csocial.entity.User;
import com.social.csocial.entity.UserLocation;
import com.social.csocial.entity.UserRelation;
import com.social.csocial.repository.UserLocationRepository;
import com.social.csocial.repository.UserRelationRepository;
import com.social.csocial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlarmService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLocationRepository userLocationRepository;

    @Autowired
    private UserRelationRepository userRelationRepository;

    public Map<String, Object> ring(Long userId, Double latitude, Double longitude, Double radiusKm) {
        User me = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!"ACTIVE".equalsIgnoreCase(me.getStatus())) {
            throw new RuntimeException("账号状态异常");
        }

        UserLocation location = userLocationRepository.findByUserId(userId).orElseGet(UserLocation::new);
        location.setUserId(userId);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        userLocationRepository.save(location);

        if (!Boolean.TRUE.equals(me.getAlarmEnabled())) {
            Map<String, Object> disabled = new HashMap<>();
            disabled.put("success", true);
            disabled.put("ringed", false);
            disabled.put("message", "你已关闭心动铃");
            disabled.put("nearbyLikeCount", 0);
            disabled.put("matchedNearbyCount", 0);
            disabled.put("matchedUserIds", Collections.emptyList());
            return disabled;
        }

        double effectiveRadius = 0.1;

        Set<Long> nearbyUserIds = userLocationRepository.findAll().stream()
                .filter(item -> !item.getUserId().equals(userId))
                .filter(item -> calculateDistance(latitude, longitude, item.getLatitude(), item.getLongitude()) <= effectiveRadius)
                .map(UserLocation::getUserId)
                .collect(Collectors.toSet());


        Set<Long> likeMyIds = userRelationRepository.findByToUserIdAndRelationType(userId, "like")
                .stream().map(UserRelation::getFromUserId).collect(Collectors.toSet());

        List<Long> matchedIds = userRelationRepository.findMatchedUserIds(userId);
        Set<Long> matchedSet = new HashSet<>(matchedIds);

        List<User> relatedUsers = userRepository.findAllById(nearbyUserIds);
        Set<Long> activeNearbyUsers = relatedUsers.stream()
                .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                .map(User::getId)
                .collect(Collectors.toSet());

        long nearbyLikeCount = activeNearbyUsers.stream().filter(likeMyIds::contains).count();
        List<Long> matchedNearbyIds = activeNearbyUsers.stream().filter(matchedSet::contains).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("ringed", nearbyLikeCount > 0);
        result.put("radiusKm", effectiveRadius);
        result.put("nearbyLikeCount", nearbyLikeCount);
        result.put("matchedNearbyCount", matchedNearbyIds.size());
        result.put("matchedUserIds", matchedNearbyIds);
        result.put("message", nearbyLikeCount > 0 ? "附近100m内由用户对你发起了互。" : "附近暂时没有喜欢你的人");

        return result;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
