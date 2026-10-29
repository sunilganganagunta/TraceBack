package com.traceback.matching;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends MongoRepository<Match, String> {

    Optional<Match> findByLostReportIdAndFoundReportId(String lostReportId, String foundReportId);

    List<Match> findByLostReportIdOrderByScoreDesc(String lostReportId);

    List<Match> findByFoundReportIdOrderByScoreDesc(String foundReportId);

    Page<Match> findByLostReportIdInOrFoundReportIdInOrderByScoreDesc(
            Collection<String> lostReportIds,
            Collection<String> foundReportIds,
            Pageable pageable
    );

    long countByStatus(MatchStatus status);
}
