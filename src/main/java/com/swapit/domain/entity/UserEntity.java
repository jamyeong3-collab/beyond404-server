package com.swapit.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "thinq_user_key", nullable = false, unique = true, length = 100)
    private String thinqUserKey;

    @Column(name = "login_id", unique = true, length = 50)
    private String loginId;

    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    @Column(name = "firebase_uid", unique = true, length = 128)
    private String firebaseUid;

    @Column(name = "email", unique = true, length = 120)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone_number", unique = true, length = 30)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected UserEntity() {
    }

    private UserEntity(String thinqUserKey, String name, String phoneNumber) {
        OffsetDateTime now = OffsetDateTime.now();
        this.thinqUserKey = thinqUserKey;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static UserEntity create(String thinqUserKey, String name, String phoneNumber) {
        return new UserEntity(thinqUserKey, name, phoneNumber);
    }

    public static UserEntity createWithCredentials(
            String loginId,
            String passwordHash,
            String thinqUserKey,
            String name,
            String phoneNumber
    ) {
        UserEntity user = new UserEntity(thinqUserKey, name, phoneNumber);
        user.loginId = loginId;
        user.passwordHash = passwordHash;
        return user;
    }

    public static UserEntity createWithFirebase(
            String firebaseUid,
            String email,
            boolean emailVerified,
            String thinqUserKey,
            String name,
            String phoneNumber
    ) {
        UserEntity user = new UserEntity(thinqUserKey, name, phoneNumber);
        user.firebaseUid = firebaseUid;
        user.email = email;
        user.emailVerified = emailVerified;
        user.loginId = email;
        return user;
    }

    public void updateProfile(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateFirebaseProfile(String email, boolean emailVerified, String name, String phoneNumber) {
        this.email = email;
        this.emailVerified = emailVerified;
        this.loginId = email;
        updateProfile(name, phoneNumber);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getThinqUserKey() {
        return thinqUserKey;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}
