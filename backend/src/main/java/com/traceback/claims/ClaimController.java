package com.traceback.claims;

import com.traceback.claims.dto.ClaimResponseDto;
import com.traceback.claims.dto.CreateClaimRequest;
import com.traceback.claims.dto.ReviewClaimRequest;
import com.traceback.common.ApiResponse;
import com.traceback.common.PagedResponse;
import com.traceback.security.UserPrincipal;
import com.traceback.users.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@Tag(name = "Claims", description = "Ownership recovery claims and verification workflow")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    @Operation(summary = "Submit an ownership recovery claim for an item")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> submitClaim(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateClaimRequest request) {
        ClaimResponseDto created = claimService.submitClaim(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Ownership claim submitted successfully", created));
    }

    @GetMapping("/my-claims")
    @Operation(summary = "List all claims submitted by the current authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<ClaimResponseDto>>> getMyClaims(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedResponse<ClaimResponseDto> claims = claimService.getMyClaims(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok(claims));
    }

    @GetMapping("/report/{reportId}")
    @Operation(summary = "List all claims submitted against a specific report (Owner or Mod only)")
    public ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getClaimsForReport(
            @PathVariable String reportId,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isPrivileged = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        List<ClaimResponseDto> claims = claimService.getClaimsForReport(reportId, principal.getId(), isPrivileged);
        return ResponseEntity.ok(ApiResponse.ok(claims));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single claim details (evidence viewable only by authorized parties)")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> getClaimById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isPrivileged = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        ClaimResponseDto claim = claimService.getClaimById(id, principal.getId(), isPrivileged);
        return ResponseEntity.ok(ApiResponse.ok(claim));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Review claim status (Approve, Reject, or Request Info)")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> reviewClaim(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReviewClaimRequest request) {
        boolean isPrivileged = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        ClaimResponseDto reviewed = claimService.reviewClaim(id, principal.getId(), request, isPrivileged);
        return ResponseEntity.ok(ApiResponse.ok("Claim status updated", reviewed));
    }

    @GetMapping("/moderation/all")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "Moderator/Admin: View all claims across platform for review")
    public ResponseEntity<ApiResponse<PagedResponse<ClaimResponseDto>>> getModeratorClaims(
            @RequestParam(required = false) ClaimStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedResponse<ClaimResponseDto> claims = claimService.getModeratorClaims(status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(claims));
    }
}
