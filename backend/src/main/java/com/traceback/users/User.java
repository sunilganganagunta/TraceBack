package com.traceback.users;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    @Indexed(unique = true)
    private String normalizedEmail;

    private String passwordHash;

    private Set<Role> roles = new HashSet<>();

    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    private boolean emailVerified = true;

    private String phone;

    private String profileImage;

    private PrivacyPreferences privacyPreferences = new PrivacyPreferences();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Instant lastLoginAt;

    public User() {}

    public User(String name, String email, String normalizedEmail, String passwordHash, Set<Role> roles) {
        this.name = name;
        this.email = email;
        this.normalizedEmail = normalizedEmail;
        this.passwordHash = passwordHash;
        this.roles = roles != null ? roles : new HashSet<>();
        this.accountStatus = AccountStatus.ACTIVE;
        this.emailVerified = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNormalizedEmail() { return normalizedEmail; }
    public void setNormalizedEmail(String normalizedEmail) { this.normalizedEmail = normalizedEmail; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

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

    public PrivacyPreferences getPrivacyPreferences() { return privacyPreferences; }
    public void setPrivacyPreferences(PrivacyPreferences privacyPreferences) { this.privacyPreferences = privacyPreferences; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public static class PrivacyPreferences {
        private boolean showEmailToClaimants = false;
        private boolean showPhoneToClaimants = false;

        public PrivacyPreferences() {}

        public boolean isShowEmailToClaimants() { return showEmailToClaimants; }
        public void setShowEmailToClaimants(boolean showEmailToClaimants) { this.showEmailToClaimants = showEmailToClaimants; }

        public boolean isShowPhoneToClaimants() { return showPhoneToClaimants; }
        public void setShowPhoneToClaimants(boolean showPhoneToClaimants) { this.showPhoneToClaimants = showPhoneToClaimants; }
    }
}
