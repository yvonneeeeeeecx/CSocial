package com.social.csocial.controller;

import com.social.csocial.entity.SecondHandItem;
import com.social.csocial.service.SecondHandService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secondhand")
@CrossOrigin(origins = "*")
public class SecondHandController {

    @Autowired
    private SecondHandService secondHandService;

    @Operation(summary = "二手发布")
    @PostMapping
    public ResponseEntity<Map<String, Object>> publish(
            @RequestParam Long sellerId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String category,
            @RequestParam Double price,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String contact,
            @RequestParam(required = false) String status) {

        SecondHandItem item = secondHandService.publish(sellerId, title, content, category, price, imageUrl, contact, status);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "二手发布成功");
        response.put("data", item);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "二手列表")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@RequestParam(required = false) String category) {
        List<SecondHandService.SecondHandView> list = secondHandService.list(category);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", list);
        response.put("count", list.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "删除二手信息", description = "仅发布者可删除")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Map<String, Object>> deleteSecondHand(
            @PathVariable Long itemId,
            @RequestParam Long operatorUserId) {
        secondHandService.deleteSecondHand(itemId, operatorUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

}
