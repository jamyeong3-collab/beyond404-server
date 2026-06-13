package com.swapit.service;

import com.swapit.domain.entity.UserEntity;
import com.swapit.dto.DemoLoginRequest;
import com.swapit.dto.DemoLoginResponse;
import com.swapit.dto.LoginIdCheckResponse;
import com.swapit.dto.LoginRequest;
import com.swapit.dto.SignupRequest;
import com.swapit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DemoLoginResponse demoLogin(DemoLoginRequest request) {
        String phoneNumber = formatPhoneNumber(request.phoneNumber());
        String thinqUserKey = toThinqUserKey(phoneNumber, request.userName());
        UserEntity user = userRepository.findByThinqUserKey(thinqUserKey)
                .map(existingUser -> {
                    existingUser.updateProfile(request.userName(), phoneNumber);
                    return existingUser;
                })
                .orElseGet(() -> UserEntity.create(thinqUserKey, request.userName(), phoneNumber));
        UserEntity savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public LoginIdCheckResponse checkLoginId(String loginId) {
        String normalizedLoginId = normalizeLoginId(loginId);
        if (normalizedLoginId.length() < 4) {
            return new LoginIdCheckResponse(false, "아이디는 4자 이상 입력해 주세요.");
        }

        boolean available = !userRepository.existsByLoginIdIgnoreCase(normalizedLoginId);
        return new LoginIdCheckResponse(
                available,
                available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다."
        );
    }

    @Transactional
    public DemoLoginResponse signup(SignupRequest request) {
        String loginId = normalizeLoginId(request.loginId());
        String phoneNumber = formatPhoneNumber(request.phoneNumber());
        if (userRepository.existsByLoginIdIgnoreCase(loginId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }

        String passwordHash = passwordEncoder.encode(request.password());
        String thinqUserKey = "login:" + loginId.toLowerCase(Locale.ROOT);
        UserEntity user = UserEntity.createWithCredentials(
                loginId,
                passwordHash,
                thinqUserKey,
                request.userName(),
                phoneNumber
        );

        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public DemoLoginResponse login(LoginRequest request) {
        String loginId = normalizeLoginId(request.loginId());
        UserEntity user = userRepository.findByLoginIdIgnoreCase(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return toResponse(user);
    }

    public static String toThinqUserKey(String phoneNumber, String userName) {
        String normalizedPhone = phoneNumber == null ? "" : phoneNumber.replaceAll("[^0-9A-Za-z]", "");
        String keySource = normalizedPhone.isBlank() ? userName : normalizedPhone;
        return "phone:" + keySource.toLowerCase(Locale.ROOT);
    }

    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }

        String digits = phoneNumber.replaceAll("[^0-9]", "");
        if (digits.length() == 11) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
        }
        if (digits.length() == 10) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
        }

        return phoneNumber.trim();
    }

    private static String normalizeLoginId(String loginId) {
        return loginId == null ? "" : loginId.trim();
    }

    private DemoLoginResponse toResponse(UserEntity user) {
        return new DemoLoginResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getPhoneNumber(),
                user.getThinqUserKey()
        );
    }
}
