package com.swapit.dto;

public record DemoLoginResponse(
        long userId,
        String loginId,
        String userName,
        String phoneNumber,
        String thinqUserKey
) {
}
