package com.social.csocial.controller;

import com.social.csocial.entity.CampusActivity;
import com.social.csocial.service.CampusActivityService;
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
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*")
public class CampusActivityController {

    @Autowired
    private CampusActivityService campusActivityService;

    @Operation(summary = "活动发布")
    @PostMapping
    public ResponseEntity<Map<String, Object>> publish(
            @RequestParam Long organizerId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String contact,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        CampusActivity activity = campusActivityService.publish(organizerId, title, content, category, location, contact, startTime, endTime);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "活动发布成功");
        response.put("data", activity);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "活动列表")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@RequestParam(required = false) String category) {
        List<CampusActivityService.ActivityView> list = campusActivityService.list(category);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", list);
        response.put("count", list.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "删除活动", description = "仅发布者可删除")
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Map<String, Object>> deleteActivity(
            @PathVariable Long activityId,
            @RequestParam Long operatorUserId) {
        campusActivityService.deleteActivity(activityId, operatorUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

}
