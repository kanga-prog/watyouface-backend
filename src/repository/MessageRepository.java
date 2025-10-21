package com.watyouface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.watyouface.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {}
