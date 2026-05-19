package com.tosin.koins.user.service;

import com.tosin.koins.user.dto.UpdateProfileRequest;
import com.tosin.koins.user.dto.UserProfileResponse;

public interface UserService {

    UserProfileResponse getMyProfile();

    UserProfileResponse updateMyProfile(UpdateProfileRequest request);
}