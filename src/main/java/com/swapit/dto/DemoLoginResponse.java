package com.swapit.dto;

public record DemoLoginResponse(
        long userId,
        String loginId,
        String email,
        boolean emailVerified,
        String userName,
        String phoneNumber,
        String thinqUserKey
) {
}
