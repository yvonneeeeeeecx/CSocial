package com.social.csocial.controller;

import com.social.csocial.entity.Announcement;
import com.social.csocial.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcement")
@CrossOrigin(origins = "*")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Operation(summary = "公告列表", description = "用户端查看公告")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        List<Announcement> list = announcementService.list();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", list);
        response.put("count", list.size());
        return ResponseEntity.ok(response);
    }
}
