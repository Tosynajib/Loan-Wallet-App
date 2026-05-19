package com.tosin.koins.user.controller;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.user.dto.UpdateProfileRequest;
import com.tosin.koins.user.dto.UserProfileResponse;
import com.tosin.koins.user.facade.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile APIs")
public class UserController {

    private final UserFacade userFacade;

    @GetMapping("/me")
    @Operation(summary = "Get authenticated user's profile")
    public ApiResponse<UserProfileResponse> getMyProfile() {
        return userFacade.getMyProfile();
    }

    @PutMapping("/me")
    @Operation(summary = "Update authenticated user's profile")
    public ApiResponse<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userFacade.updateMyProfile(request);
    }
}