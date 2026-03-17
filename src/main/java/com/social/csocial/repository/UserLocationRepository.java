package com.social.csocial.repository;

import com.social.csocial.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {

    // 根据用户ID查找位置
    Optional<UserLocation> findByUserId(Long userId);

    // 查找所有用户位置
    List<UserLocation> findAll();

    // 删除用户位置
    void deleteByUserId(Long userId);
}