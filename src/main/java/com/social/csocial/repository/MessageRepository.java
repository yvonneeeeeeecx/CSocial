package com.social.csocial.repository;

import com.social.csocial.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.fromUserId = :userId AND m.toUserId = :targetId) OR (m.fromUserId = :targetId AND m.toUserId = :userId) ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("userId") Long userId, @Param("targetId") Long targetId);

    @Query("SELECT m FROM Message m WHERE m.fromUserId = :userId OR m.toUserId = :userId ORDER BY m.createdAt DESC")
    List<Message> findByUser(@Param("userId") Long userId);
}

