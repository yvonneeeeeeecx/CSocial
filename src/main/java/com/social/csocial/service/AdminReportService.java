package com.social.csocial.service;

import com.social.csocial.entity.Post;
import com.social.csocial.repository.AnnouncementRepository;
import com.social.csocial.repository.PostCommentRepository;
import com.social.csocial.repository.PostRepository;
import com.social.csocial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminReportService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    public Map<String, Object> getOverview(Long adminUserId) {
        userService.assertAdmin(adminUserId);

        long userCount = userRepository.count();
        long activeUserCount = userRepository.countByStatusIgnoreCase("ACTIVE");
        long disabledUserCount = userRepository.countByStatusIgnoreCase("DISABLED");
        long postCount = postRepository.count();
        long commentCount = postCommentRepository.count();
        long announcementCount = announcementRepository.count();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime sevenDayStart = LocalDate.now().minusDays(6).atStartOfDay();

        long todayNewUsers = userRepository.countByCreatedAtBetween(todayStart, now);
        long todayNewPosts = postRepository.countByCreatedAtBetween(todayStart, now);
        long sevenDayNewUsers = userRepository.countByCreatedAtBetween(sevenDayStart, now);
        long sevenDayNewPosts = postRepository.countByCreatedAtBetween(sevenDayStart, now);
        long sevenDayComments = postCommentRepository.countByCreatedAtBetween(sevenDayStart, now);

        List<Post> posts = postRepository.findAll();
        long totalLikeCount = posts.stream().mapToLong(item -> item.getLikeCount() == null ? 0 : item.getLikeCount()).sum();
        long totalCommentCount = posts.stream().mapToLong(item -> item.getCommentCount() == null ? 0 : item.getCommentCount()).sum();
        long interactionCount = totalLikeCount + totalCommentCount;

        long trafficIndex = todayNewUsers + todayNewPosts * 2 + sevenDayComments + activeUserCount;

        Map<String, Object> result = new HashMap<>();
        result.put("userCount", userCount);
        result.put("activeUserCount", activeUserCount);
        result.put("disabledUserCount", disabledUserCount);
        result.put("postCount", postCount);
        result.put("commentCount", commentCount);
        result.put("announcementCount", announcementCount);
        result.put("interactionCount", interactionCount);
        result.put("todayNewUsers", todayNewUsers);
        result.put("todayNewPosts", todayNewPosts);
        result.put("sevenDayNewUsers", sevenDayNewUsers);
        result.put("sevenDayNewPosts", sevenDayNewPosts);
        result.put("sevenDayComments", sevenDayComments);
        result.put("trafficIndex", trafficIndex);
        return result;
    }

    public List<Map<String, Object>> getTrend(Long adminUserId, int days) {
        userService.assertAdmin(adminUserId);
        int reportDays = Math.min(Math.max(days, 3), 30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = reportDays - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            long newUsers = userRepository.countByCreatedAtBetween(start, end);
            long newPosts = postRepository.countByCreatedAtBetween(start, end);
            long newComments = postCommentRepository.countByCreatedAtBetween(start, end);

            Map<String, Object> item = new HashMap<>();
            item.put("date", formatter.format(date));
            item.put("newUsers", newUsers);
            item.put("newPosts", newPosts);
            item.put("newComments", newComments);
            item.put("traffic", newUsers + newPosts * 2 + newComments);
            trend.add(item);
        }
        return trend;
    }
}
