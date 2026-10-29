package com.traceback.claims;

import com.traceback.claims.dto.ClaimResponseDto;
import com.traceback.claims.dto.CreateClaimRequest;
import com.traceback.claims.dto.ReviewClaimRequest;
import com.traceback.common.BadRequestException;
import com.traceback.notifications.NotificationService;
import com.traceback.reports.ItemReport;
import com.traceback.reports.ItemReportRepository;
import com.traceback.reports.ReportStatus;
import com.traceback.reports.ReportType;
import com.traceback.users.User;
import com.traceback.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ItemReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    private ClaimService claimService;

    @BeforeEach
    void setUp() {
        claimService = new ClaimService(claimRepository, reportRepository, userRepository, notificationService);
    }

    @Test
    @DisplayName("Should prevent user from filing an ownership claim on their own report")
    void testSubmitClaim_SelfClaimForbidden() {
        String userId = "user-123";

        User claimant = new User();
        claimant.setId(userId);
        claimant.setName("Self Claimant");
        when(userRepository.findById(userId)).thenReturn(Optional.of(claimant));

        ItemReport report = new ItemReport();
        report.setId("report-abc");
        report.setOwnerId(userId); // Same user owns report
        report.setTitle("My Lost Watch");
        report.setStatus(ReportStatus.ACTIVE);
        when(reportRepository.findById("report-abc")).thenReturn(Optional.of(report));

        CreateClaimRequest request = new CreateClaimRequest();
        request.setReportId("report-abc");
        request.setClaimDescription("Trying to claim own report");

        assertThrows(BadRequestException.class, () -> claimService.submitClaim(userId, request));
        verify(claimRepository, never()).save(any(Claim.class));
    }

    @Test
    @DisplayName("Should successfully submit a valid claim and update report status to CLAIM_PENDING")
    void testSubmitClaim_Success() {
        String claimantId = "user-claimant";
        String ownerId = "user-owner";

        User claimant = new User();
        claimant.setId(claimantId);
        claimant.setName("Legit Claimant");
        when(userRepository.findById(claimantId)).thenReturn(Optional.of(claimant));

        ItemReport report = new ItemReport();
        report.setId("report-100");
        report.setOwnerId(ownerId);
        report.setTitle("Found Sony Headphones");
        report.setStatus(ReportStatus.ACTIVE);
        when(reportRepository.findById("report-100")).thenReturn(Optional.of(report));

        when(claimRepository.findByReportIdAndClaimantIdAndStatus("report-100", claimantId, ClaimStatus.PENDING))
                .thenReturn(Optional.empty());

        Claim savedClaim = new Claim("report-100", "Found Sony Headphones", claimantId, "Legit Claimant",
                "These are my XM5 headphones", new PrivateVerificationEvidence("Custom case", "12345", null, null));
        savedClaim.setId("claim-xyz");
        when(claimRepository.save(any(Claim.class))).thenReturn(savedClaim);

        CreateClaimRequest request = new CreateClaimRequest();
        request.setReportId("report-100");
        request.setClaimDescription("These are my XM5 headphones");

        ClaimResponseDto response = claimService.submitClaim(claimantId, request);

        assertNotNull(response);
        assertEquals("claim-xyz", response.getId());
        assertEquals(ClaimStatus.PENDING, response.getStatus());

        // Verify report was transitioned to CLAIM_PENDING
        assertEquals(ReportStatus.CLAIM_PENDING, report.getStatus());
        verify(reportRepository).save(report);
        verify(notificationService).createNotification(eq(ownerId), any(), any(), any(), eq("claim-xyz"), eq("CLAIM"));
    }
}
