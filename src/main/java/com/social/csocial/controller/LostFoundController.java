package com.social.csocial.controller;

import com.social.csocial.entity.LostFoundItem;
import com.social.csocial.service.LostFoundService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lostfound")
@CrossOrigin(origins = "*")
public class LostFoundController {

    @Autowired
    private LostFoundService lostFoundService;

    @Operation(summary = "失物招领发布")
    @PostMapping
    public ResponseEntity<Map<String, Object>> publish(
            @RequestParam Long reporterId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String contact,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String imageUrl) {

        LostFoundItem item = lostFoundService.publish(reporterId, title, content, category, location, contact, status, imageUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "失物招领发布成功");
        response.put("data", item);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "失物招领列表")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@RequestParam(required = false) String category) {
        List<LostFoundService.LostFoundView> list = lostFoundService.list(category);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", list);
        response.put("count", list.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "删除失物招领", description = "仅发布者可删除")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Map<String, Object>> deleteLostFound(
            @PathVariable Long itemId,
            @RequestParam Long operatorUserId) {
        lostFoundService.deleteLostFound(itemId, operatorUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

}
