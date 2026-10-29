package com.traceback.users.dto;

import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    private String phone;
    private String profileImage;
    private Boolean showEmailToClaimants;
    private Boolean showPhoneToClaimants;

    public UpdateUserRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Boolean getShowEmailToClaimants() { return showEmailToClaimants; }
    public void setShowEmailToClaimants(Boolean showEmailToClaimants) { this.showEmailToClaimants = showEmailToClaimants; }

    public Boolean getShowPhoneToClaimants() { return showPhoneToClaimants; }
    public void setShowPhoneToClaimants(Boolean showPhoneToClaimants) { this.showPhoneToClaimants = showPhoneToClaimants; }
}
