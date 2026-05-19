package com.tosin.koins.user.service;

import com.tosin.koins.common.exception.ConflictException;
import com.tosin.koins.common.security.CurrentUserProvider;
import com.tosin.koins.user.dto.UpdateProfileRequest;
import com.tosin.koins.user.dto.UserProfileResponse;
import com.tosin.koins.user.entity.User;
import com.tosin.koins.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User profile business logic.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public UserProfileResponse getMyProfile() {
        User currentUser = currentUserProvider.getCurrentUser();
        return UserProfileResponse.from(currentUser);
    }

    @Override
    @Transactional
    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        String newPhoneNumber = request.phoneNumber().trim();

        boolean phoneChanged = currentUser.getPhoneNumber() == null
                || !currentUser.getPhoneNumber().equals(newPhoneNumber);

        if (phoneChanged && userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new ConflictException("Phone number already exists");
        }

        currentUser.setFullName(request.fullName().trim());
        currentUser.setPhoneNumber(newPhoneNumber);

        User savedUser = userRepository.save(currentUser);

        return UserProfileResponse.from(savedUser);
    }
}