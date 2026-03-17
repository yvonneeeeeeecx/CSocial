package com.social.csocial.service;

import com.social.csocial.entity.Announcement;
import com.social.csocial.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private UserService userService;

    public Announcement publish(Long adminUserId, String title, String content) {
        userService.assertAdmin(adminUserId);
        if (title == null || title.isBlank()) {
            throw new RuntimeException("公告标题不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new RuntimeException("公告内容不能为空");
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(title.trim());
        announcement.setContent(content.trim());
        announcement.setPublisherId(adminUserId);
        return announcementRepository.save(announcement);
    }

    public List<Announcement> list() {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }

    public void delete(Long adminUserId, Long announcementId) {
        userService.assertAdmin(adminUserId);
        if (!announcementRepository.existsById(announcementId)) {
            throw new RuntimeException("公告不存在");
        }
        announcementRepository.deleteById(announcementId);
    }
}
