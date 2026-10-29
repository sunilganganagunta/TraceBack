package com.traceback.users.dto;

import com.traceback.users.AccountStatus;
import com.traceback.users.Role;
import com.traceback.users.User;

import java.time.Instant;
import java.util.Set;

public class UserDto {
    private String id;
    private String name;
    private String email;
    private Set<Role> roles;
    private AccountStatus accountStatus;
    private boolean emailVerified;
    private String phone;
    private String profileImage;
    private Instant createdAt;
    private Instant lastLoginAt;

    public UserDto() {}

    public static UserDto from(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setAccountStatus(user.getAccountStatus());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setPhone(user.getPhone());
        dto.setProfileImage(user.getProfileImage());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
