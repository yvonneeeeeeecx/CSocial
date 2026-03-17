package com.social.csocial.controller;

import com.social.csocial.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/alarm")
@CrossOrigin(origins = "*")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @Operation(summary = "触发心动铃", description = "根据当前位置计算附近喜欢你的人数")
    @PostMapping("/ring")
    public ResponseEntity<Map<String, Object>> ring(
            @RequestParam Long userId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Double radiusKm) {
        return ResponseEntity.ok(alarmService.ring(userId, latitude, longitude, radiusKm));
    }
}
