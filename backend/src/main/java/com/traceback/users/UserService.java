package com.traceback.users;

import com.traceback.common.PagedResponse;
import com.traceback.common.ResourceNotFoundException;
import com.traceback.users.dto.UpdateUserRequest;
import com.traceback.users.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return UserDto.from(user);
    }

    public UserDto updateProfile(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
        if (request.getShowEmailToClaimants() != null) {
            user.getPrivacyPreferences().setShowEmailToClaimants(request.getShowEmailToClaimants());
        }
        if (request.getShowPhoneToClaimants() != null) {
            user.getPrivacyPreferences().setShowPhoneToClaimants(request.getShowPhoneToClaimants());
        }
        user.setUpdatedAt(Instant.now());

        User saved = userRepository.save(user);
        return UserDto.from(saved);
    }

    public PagedResponse<UserDto> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return PagedResponse.from(page.map(UserDto::from));
    }

    public UserDto updateUserStatus(String userId, AccountStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setAccountStatus(status);
        user.setUpdatedAt(Instant.now());
        return UserDto.from(userRepository.save(user));
    }

    public UserDto updateUserRoles(String userId, Set<Role> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setRoles(roles);
        user.setUpdatedAt(Instant.now());
        return UserDto.from(userRepository.save(user));
    }
}
