package com.swapit.repository;

import com.swapit.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByThinqUserKey(String thinqUserKey);

    Optional<UserEntity> findByLoginIdIgnoreCase(String loginId);

    Optional<UserEntity> findByFirebaseUid(String firebaseUid);

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByLoginIdIgnoreCase(String loginId);

    boolean existsByPhoneNumber(String phoneNumber);
}
