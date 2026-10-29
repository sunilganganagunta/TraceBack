package com.traceback.users;

import com.traceback.common.ApiResponse;
import com.traceback.common.PagedResponse;
import com.traceback.security.UserPrincipal;
import com.traceback.users.dto.UpdateUserRequest;
import com.traceback.users.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User profile and administrative user management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated user's profile")
    public ResponseEntity<ApiResponse<UserDto>> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserDto profile = userService.getUserProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PatchMapping("/me")
    @Operation(summary = "Update currently authenticated user's profile details")
    public ResponseEntity<ApiResponse<UserDto>> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDto updated = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Get all registered users (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<UserDto>>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedResponse<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Update user account status (ACTIVE or SUSPENDED)")
    public ResponseEntity<ApiResponse<UserDto>> updateUserStatus(
            @PathVariable String id,
            @RequestParam AccountStatus status) {
        UserDto updated = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("User status updated", updated));
    }
}
