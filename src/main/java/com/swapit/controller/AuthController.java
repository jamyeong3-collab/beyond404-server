package com.swapit.controller;

import com.swapit.dto.DemoLoginRequest;
import com.swapit.dto.DemoLoginResponse;
import com.swapit.dto.LoginIdCheckResponse;
import com.swapit.dto.LoginRequest;
import com.swapit.dto.SignupRequest;
import com.swapit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/demo-login")
    public DemoLoginResponse demoLogin(@Valid @RequestBody DemoLoginRequest request) {
        return userService.demoLogin(request);
    }

    @GetMapping("/check-login-id")
    public LoginIdCheckResponse checkLoginId(@RequestParam String loginId) {
        return userService.checkLoginId(loginId);
    }

    @PostMapping("/signup")
    public DemoLoginResponse signup(@Valid @RequestBody SignupRequest request) {
        return userService.signup(request);
    }

    @PostMapping("/login")
    public DemoLoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
