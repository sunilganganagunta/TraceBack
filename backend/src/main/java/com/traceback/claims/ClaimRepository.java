package com.traceback.claims;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends MongoRepository<Claim, String> {

    List<Claim> findByReportId(String reportId);

    Page<Claim> findByClaimantId(String claimantId, Pageable pageable);

    Page<Claim> findByStatus(ClaimStatus status, Pageable pageable);

    Optional<Claim> findByReportIdAndClaimantIdAndStatus(String reportId, String claimantId, ClaimStatus status);

    long countByStatus(ClaimStatus status);
}
