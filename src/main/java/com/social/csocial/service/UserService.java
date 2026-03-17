package com.social.csocial.service;

import com.social.csocial.entity.User;
import com.social.csocial.repository.UserRepository;
import com.social.csocial.util.SimplePasswordUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimplePasswordUtil passwordUtil;

    @PostConstruct
    public void initAdminAccount() {
        if (userRepository.existsByUsername("admin")) {
            return;
        }

        User admin = new User("admin", passwordUtil.encodePassword("admin123"), "系统管理员");
        admin.setRole("ADMIN");
        admin.setStatus("ACTIVE");
        admin.setAlarmEnabled(false);
        admin.setAlarmRadiusKm(1.0);
        userRepository.save(admin);
    }

    public User register(String username, String password, String nickname, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        if (email != null && !email.isEmpty() && userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User(username, passwordUtil.encodePassword(password), nickname);
        user.setEmail(email);
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setAlarmEnabled(true);
        user.setAlarmRadiusKm(1.0);
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }

        User user = userOpt.get();
        boolean needSave = false;

        String storedPassword = user.getPassword();
        boolean passwordMatched = storedPassword != null && passwordUtil.matches(password, storedPassword);

        if (!passwordMatched && storedPassword != null && password.equals(storedPassword)) {
            user.setPassword(passwordUtil.encodePassword(password));
            needSave = true;
            passwordMatched = true;
        }

        if (!passwordMatched) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() == null || user.getStatus().trim().isEmpty()) {
            user.setStatus("ACTIVE");
            needSave = true;
        }

        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
            needSave = true;
        }

        if (user.getAlarmEnabled() == null) {
            user.setAlarmEnabled(true);
            needSave = true;
        }

        if (user.getAlarmRadiusKm() == null || user.getAlarmRadiusKm() <= 0) {
            user.setAlarmRadiusKm(1.0);
            needSave = true;
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        if (needSave) {
            user = userRepository.save(user);
        }

        return user;
    }

    public User getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPassword(null);
        return user;
    }

    public User updateUser(Long userId, String nickname, String profile, String interests,
                           String college, String major, Integer grade) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        if (nickname != null) user.setNickname(nickname);
        if (profile != null) user.setProfile(profile);
        if (interests != null) user.setInterests(interests);
        if (college != null) user.setCollege(college);
        if (major != null) user.setMajor(major);
        if (grade != null) user.setGrade(grade);

        return userRepository.save(user);
    }

    public User updateAlarmSetting(Long userId, Boolean alarmEnabled, Double alarmRadiusKm) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        if (alarmEnabled != null) {
            user.setAlarmEnabled(alarmEnabled);
        }
        if (alarmRadiusKm != null) {
            if (alarmRadiusKm <= 0 || alarmRadiusKm > 10) {
                throw new RuntimeException("心动铃半径范围应在0~10公里");
            }
            user.setAlarmRadiusKm(alarmRadiusKm);
        }

        return userRepository.save(user);
    }

    public List<User> listCampusUsers(Long userId, String keyword) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        String keywordText = keyword == null ? "" : keyword.trim().toLowerCase();

        List<User> users = userRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> item.getId() != null && !item.getId().equals(userId))
                .filter(item -> item.getRole() == null || !"ADMIN".equalsIgnoreCase(item.getRole()))
                .filter(item -> "ACTIVE".equalsIgnoreCase(item.getStatus()))
                .filter(item -> {
                    if (keywordText.isEmpty()) {
                        return true;
                    }
                    String username = item.getUsername() == null ? "" : item.getUsername().toLowerCase();
                    String nickname = item.getNickname() == null ? "" : item.getNickname().toLowerCase();
                    String college = item.getCollege() == null ? "" : item.getCollege().toLowerCase();
                    String major = item.getMajor() == null ? "" : item.getMajor().toLowerCase();
                    return username.contains(keywordText) || nickname.contains(keywordText)
                            || college.contains(keywordText) || major.contains(keywordText);
                })
                .toList();

        users.forEach(item -> item.setPassword(null));
        return users;
    }

    public List<User> listUsersByAdmin(Long adminUserId) {

        return listUsersByAdmin(adminUserId, null, null);
    }

    public List<User> listUsersByAdmin(Long adminUserId, String keyword, String status) {
        assertAdmin(adminUserId);
        String keywordText = keyword == null ? "" : keyword.trim().toLowerCase();
        String statusText = status == null ? "" : status.trim().toUpperCase();

        List<User> users = userRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> {
                    if (statusText.isEmpty()) {
                        return true;
                    }
                    String itemStatus = item.getStatus() == null ? "" : item.getStatus().trim().toUpperCase();
                    return itemStatus.equals(statusText);
                })
                .filter(item -> {
                    if (keywordText.isEmpty()) {
                        return true;
                    }
                    String username = item.getUsername() == null ? "" : item.getUsername().toLowerCase();
                    String nickname = item.getNickname() == null ? "" : item.getNickname().toLowerCase();
                    String email = item.getEmail() == null ? "" : item.getEmail().toLowerCase();
                    return username.contains(keywordText) || nickname.contains(keywordText) || email.contains(keywordText);
                })
                .toList();

        users.forEach(item -> item.setPassword(null));
        return users;
    }


    public User updateUserStatusByAdmin(Long adminUserId, Long targetUserId, String status) {
        assertAdmin(adminUserId);
        if (!"ACTIVE".equalsIgnoreCase(status) && !"DISABLED".equalsIgnoreCase(status)) {
            throw new RuntimeException("状态仅支持 ACTIVE / DISABLED");
        }

        User target = userRepository.findById(targetUserId).orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("ADMIN".equalsIgnoreCase(target.getRole())) {
            throw new RuntimeException("管理员账号不可被禁用");
        }
        target.setStatus(status.toUpperCase());
        User saved = userRepository.save(target);
        saved.setPassword(null);
        return saved;
    }

    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isAdmin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }

    public void assertAdmin(Long userId) {
        if (!isAdmin(userId)) {
            throw new RuntimeException("无管理员权限");
        }
    }
}
