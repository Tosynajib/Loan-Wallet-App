package com.tosin.koins.user.facade;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.common.response.ApplicationResponseFactory;
import com.tosin.koins.user.dto.UpdateProfileRequest;
import com.tosin.koins.user.dto.UserProfileResponse;
import com.tosin.koins.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final ApplicationResponseFactory responseFactory;

    @Override
    public ApiResponse<UserProfileResponse> getMyProfile() {
        return responseFactory.success("Profile retrieved successfully", userService.getMyProfile());
    }

    @Override
    public ApiResponse<UserProfileResponse> updateMyProfile(UpdateProfileRequest request) {
        return responseFactory.success("Profile updated successfully", userService.updateMyProfile(request));
    }
}