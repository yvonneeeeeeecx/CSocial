package com.social.csocial.repository;

import com.social.csocial.entity.CampusActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampusActivityRepository extends JpaRepository<CampusActivity, Long> {

    List<CampusActivity> findAllByOrderByCreatedAtDesc();
}
