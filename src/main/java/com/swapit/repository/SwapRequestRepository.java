package com.swapit.repository;

import com.swapit.domain.entity.SwapRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SwapRequestRepository extends JpaRepository<SwapRequestEntity, Long> {
    Optional<SwapRequestEntity> findFirstByUser_IdOrderByCreatedAtDesc(Long userId);
}
