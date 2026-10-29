package com.traceback.matching;

import com.traceback.common.ApiResponse;
import com.traceback.matching.dto.MatchResponseDto;
import com.traceback.security.UserPrincipal;
import com.traceback.users.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@Tag(name = "Matches", description = "Explainable item matching results and recalculation")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/report/{reportId}")
    @Operation(summary = "Get ranked matches for a specific report")
    public ResponseEntity<ApiResponse<List<MatchResponseDto>>> getMatchesForReport(
            @PathVariable String reportId,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isPrivileged = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        List<MatchResponseDto> matches = matchService.getMatchesForReport(reportId, principal.getId(), isPrivileged);
        return ResponseEntity.ok(ApiResponse.ok(matches));
    }

    @GetMapping("/my-matches")
    @Operation(summary = "Get top ranked matches across all reports owned by authenticated user")
    public ResponseEntity<ApiResponse<List<MatchResponseDto>>> getMyMatches(@AuthenticationPrincipal UserPrincipal principal) {
        List<MatchResponseDto> matches = matchService.getMyMatches(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(matches));
    }

    @PostMapping("/recalculate/{reportId}")
    @Operation(summary = "Trigger on-demand recalculation of matches for a report")
    public ResponseEntity<ApiResponse<List<MatchResponseDto>>> recalculateMatches(
            @PathVariable String reportId,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isPrivileged = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        List<MatchResponseDto> matches = matchService.recalculateMatches(reportId, principal.getId(), isPrivileged);
        return ResponseEntity.ok(ApiResponse.ok("Matches recalculated successfully", matches));
    }
}
