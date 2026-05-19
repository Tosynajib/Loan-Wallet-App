package com.tosin.koins.user.facade;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.user.dto.UpdateProfileRequest;
import com.tosin.koins.user.dto.UserProfileResponse;

public interface UserFacade {

    ApiResponse<UserProfileResponse> getMyProfile();

    ApiResponse<UserProfileResponse> updateMyProfile(UpdateProfileRequest request);
}