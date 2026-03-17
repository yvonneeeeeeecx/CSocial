package com.social.csocial.repository;

import com.social.csocial.entity.SecondHandItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecondHandItemRepository extends JpaRepository<SecondHandItem, Long> {

    List<SecondHandItem> findAllByOrderByCreatedAtDesc();
}
