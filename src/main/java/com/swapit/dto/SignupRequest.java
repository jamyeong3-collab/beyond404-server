package com.swapit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank @Size(min = 4, max = 50) String loginId,
        @NotBlank @Size(min = 4, max = 80) String password,
        @NotBlank @Size(max = 50) String userName,
        @NotBlank @Size(max = 30) String phoneNumber
) {
}
