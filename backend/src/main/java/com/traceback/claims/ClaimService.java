package com.traceback.claims;

import com.traceback.claims.dto.ClaimResponseDto;
import com.traceback.claims.dto.CreateClaimRequest;
import com.traceback.claims.dto.ReviewClaimRequest;
import com.traceback.common.BadRequestException;
import com.traceback.common.ConflictException;
import com.traceback.common.ForbiddenException;
import com.traceback.common.PagedResponse;
import com.traceback.common.ResourceNotFoundException;
import com.traceback.notifications.NotificationService;
import com.traceback.notifications.NotificationType;
import com.traceback.reports.ItemReport;
import com.traceback.reports.ItemReportRepository;
import com.traceback.reports.ReportStatus;
import com.traceback.users.User;
import com.traceback.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemReportRepository reportRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ClaimService(ClaimRepository claimRepository,
                        ItemReportRepository reportRepository,
                        UserRepository userRepository,
                        NotificationService notificationService) {
        this.claimRepository = claimRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public ClaimResponseDto submitClaim(String claimantId, CreateClaimRequest request) {
        User claimant = userRepository.findById(claimantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", claimantId));

        ItemReport report = reportRepository.findById(request.getReportId())
                .orElseThrow(() -> new ResourceNotFoundException("ItemReport", "id", request.getReportId()));

        if (report.getOwnerId().equals(claimantId)) {
            throw new BadRequestException("You cannot file an ownership claim on your own report");
        }

        if (report.getStatus() == ReportStatus.WITHDRAWN || report.getStatus() == ReportStatus.CLOSED
                || report.getStatus() == ReportStatus.RETURNED) {
            throw new BadRequestException("This report is no longer accepting claims (Current status: " + report.getStatus() + ")");
        }

        // Check for existing pending claim by this user
        claimRepository.findByReportIdAndClaimantIdAndStatus(report.getId(), claimantId, ClaimStatus.PENDING)
                .ifPresent(existing -> {
                    throw new ConflictException("You already have an active pending claim for this report");
                });

        Claim claim = new Claim(
                report.getId(),
                report.getTitle(),
                claimantId,
                claimant.getName(),
                request.getClaimDescription().trim(),
                request.getPrivateEvidence()
        );

        Claim savedClaim = claimRepository.save(claim);

        // Transition report status to CLAIM_PENDING
        if (report.getStatus() == ReportStatus.ACTIVE || report.getStatus() == ReportStatus.MATCHED) {
            report.setStatus(ReportStatus.CLAIM_PENDING);
            report.setUpdatedAt(Instant.now());
            reportRepository.save(report);
        }

        // Notify report owner
        notificationService.createNotification(
                report.getOwnerId(),
                NotificationType.CLAIM_SUBMITTED,
                "New Recovery Claim Submitted",
                String.format("%s submitted an ownership claim for your report '%s'.", claimant.getName(), report.getTitle()),
                savedClaim.getId(),
                "CLAIM"
        );

        return ClaimResponseDto.from(savedClaim, claimantId, true);
    }

    public PagedResponse<ClaimResponseDto> getMyClaims(String claimantId, Pageable pageable) {
        Page<Claim> page = claimRepository.findByClaimantId(claimantId, pageable);
        Page<ClaimResponseDto> dtoPage = page.map(c -> ClaimResponseDto.from(c, claimantId, true));
        return PagedResponse.from(dtoPage);
    }

    public List<ClaimResponseDto> getClaimsForReport(String reportId, String userId, boolean isPrivileged) {
        ItemReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("ItemReport", "id", reportId));

        boolean isOwner = report.getOwnerId().equals(userId);
        if (!isOwner && !isPrivileged) {
            throw new ForbiddenException("Only the report owner or a moderator can inspect claims filed for this report");
        }

        List<Claim> claims = claimRepository.findByReportId(reportId);
        return claims.stream()
                .map(c -> ClaimResponseDto.from(c, userId, true))
                .toList();
    }

    public ClaimResponseDto getClaimById(String claimId, String userId, boolean isPrivileged) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim", "id", claimId));

        ItemReport report = reportRepository.findById(claim.getReportId())
                .orElseThrow(() -> new ResourceNotFoundException("ItemReport", "id", claim.getReportId()));

        boolean isOwner = report.getOwnerId().equals(userId);
        boolean isClaimant = claim.getClaimantId().equals(userId);

        if (!isOwner && !isClaimant && !isPrivileged) {
            throw new ForbiddenException("You do not have permission to view this claim");
        }

        return ClaimResponseDto.from(claim, userId, isOwner || isPrivileged);
    }

    public ClaimResponseDto reviewClaim(String claimId, String reviewerId, ReviewClaimRequest request, boolean isPrivileged) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim", "id", claimId));

        ItemReport report = reportRepository.findById(claim.getReportId())
                .orElseThrow(() -> new ResourceNotFoundException("ItemReport", "id", claim.getReportId()));

        boolean isOwner = report.getOwnerId().equals(reviewerId);
        if (!isOwner && !isPrivileged) {
            throw new ForbiddenException("Only the report owner or an assigned moderator can review this claim");
        }

        claim.setStatus(request.getStatus());
        claim.setReviewerId(reviewerId);
        claim.setReviewNotes(request.getReviewNotes());
        claim.setUpdatedAt(Instant.now());

        if (request.getStatus() == ClaimStatus.APPROVED) {
            claim.setResolvedAt(Instant.now());
            report.setStatus(ReportStatus.RETURNED);
            report.setUpdatedAt(Instant.now());
            reportRepository.save(report);

            // Reject any competing pending claims on this report
            List<Claim> otherClaims = claimRepository.findByReportId(report.getId());
            for (Claim other : otherClaims) {
                if (!other.getId().equals(claim.getId()) && other.getStatus() == ClaimStatus.PENDING) {
                    other.setStatus(ClaimStatus.REJECTED);
                    other.setReviewNotes("Another verified claim was approved by the reviewer.");
                    other.setUpdatedAt(Instant.now());
                    claimRepository.save(other);
                }
            }

            notificationService.createNotification(
                    claim.getClaimantId(),
                    NotificationType.CLAIM_STATUS_UPDATED,
                    "Claim Approved! Item Recovered",
                    String.format("Congratulations! Your claim for '%s' was APPROVED by the reporter.", report.getTitle()),
                    claim.getId(),
                    "CLAIM"
            );

        } else if (request.getStatus() == ClaimStatus.REJECTED) {
            claim.setResolvedAt(Instant.now());

            // Check if any other pending claims remain; if not, return report to ACTIVE
            List<Claim> remaining = claimRepository.findByReportId(report.getId()).stream()
                    .filter(c -> !c.getId().equals(claim.getId()) && c.getStatus() == ClaimStatus.PENDING)
                    .toList();

            if (remaining.isEmpty()) {
                report.setStatus(ReportStatus.ACTIVE);
                report.setUpdatedAt(Instant.now());
                reportRepository.save(report);
            }

            notificationService.createNotification(
                    claim.getClaimantId(),
                    NotificationType.CLAIM_STATUS_UPDATED,
                    "Claim Update: Needs Review / Rejected",
                    String.format("Your claim for '%s' was reviewed: %s", report.getTitle(),
                            request.getReviewNotes() != null ? request.getReviewNotes() : "Rejected"),
                    claim.getId(),
                    "CLAIM"
            );
        }

        Claim saved = claimRepository.save(claim);
        return ClaimResponseDto.from(saved, reviewerId, true);
    }

    public PagedResponse<ClaimResponseDto> getModeratorClaims(ClaimStatus status, Pageable pageable) {
        Page<Claim> page = (status != null)
                ? claimRepository.findByStatus(status, pageable)
                : claimRepository.findAll(pageable);
        return PagedResponse.from(page.map(c -> ClaimResponseDto.from(c, null, true)));
    }
}
