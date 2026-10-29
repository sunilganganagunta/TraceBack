package com.traceback.matching;

import com.traceback.reports.ItemReport;
import com.traceback.reports.ItemReportRepository;
import com.traceback.reports.ReportStatus;
import com.traceback.reports.ReportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MatchingEngineServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private ItemReportRepository reportRepository;

    private MatchingEngineService matchingEngineService;

    @BeforeEach
    void setUp() {
        matchingEngineService = new MatchingEngineService(matchRepository, reportRepository);
    }

    @Test
    @DisplayName("Should score high similarity for compatible electronics pair with matching title, color, brand, and nearby location")
    void testScoreReportPair_HighMatch() {
        ItemReport lost = new ItemReport();
        lost.setId("lost-1");
        lost.setOwnerId("user-1");
        lost.setReportType(ReportType.LOST);
        lost.setCategoryId("cat-elec");
        lost.setCategoryName("Electronics");
        lost.setTitle("Apple MacBook Pro M2 Space Gray");
        lost.setDescription("Lost in university central library near 2nd floor desk");
        lost.setColor("Space Gray");
        lost.setBrand("Apple");
        lost.setDateLostOrFound(LocalDate.of(2026, 10, 20));
        lost.setLocation(new GeoJsonPoint(77.5946, 12.9716));
        lost.setStatus(ReportStatus.ACTIVE);

        ItemReport found = new ItemReport();
        found.setId("found-1");
        found.setOwnerId("user-2");
        found.setReportType(ReportType.FOUND);
        found.setCategoryId("cat-elec");
        found.setCategoryName("Electronics");
        found.setTitle("Found Apple MacBook Pro Space Gray in Library");
        found.setDescription("Found near study desks in central library second floor");
        found.setColor("Space Gray");
        found.setBrand("Apple");
        found.setDateLostOrFound(LocalDate.of(2026, 10, 21));
        found.setLocation(new GeoJsonPoint(77.5947, 12.9718)); // ~25 meters away
        found.setStatus(ReportStatus.ACTIVE);

        Match match = matchingEngineService.scoreReportPair(lost, found);

        assertNotNull(match);
        assertTrue(match.getScore() >= 75.0, "Score should be at least 75 for matching attributes, got: " + match.getScore());
        assertTrue(match.getScore() <= 100.0, "Score should never exceed 100");
        assertNotNull(match.getScoreBreakdown());
        assertEquals(30.0, match.getScoreBreakdown().getCategoryScore(), "Category match should yield 30 pts");
        assertTrue(match.getReasons().size() >= 3, "Should explain reasons for matching");
    }

    @Test
    @DisplayName("Should score low similarity when categories and keywords do not match")
    void testScoreReportPair_IncompatibleReports() {
        ItemReport lost = new ItemReport();
        lost.setId("lost-2");
        lost.setOwnerId("user-1");
        lost.setReportType(ReportType.LOST);
        lost.setCategoryId("cat-keys");
        lost.setCategoryName("Keys");
        lost.setTitle("Set of Honda Bike Keys");
        lost.setDescription("Lost brass keys on red keychain");
        lost.setColor("Brass");
        lost.setDateLostOrFound(LocalDate.of(2026, 10, 10));
        lost.setLocation(new GeoJsonPoint(77.5000, 12.9000));

        ItemReport found = new ItemReport();
        found.setId("found-2");
        found.setOwnerId("user-2");
        found.setReportType(ReportType.FOUND);
        found.setCategoryId("cat-clothing");
        found.setCategoryName("Clothing");
        found.setTitle("Blue Denim Jacket");
        found.setDescription("Found warm jacket in cafeteria");
        found.setColor("Blue");
        found.setDateLostOrFound(LocalDate.of(2026, 10, 25));
        found.setLocation(new GeoJsonPoint(77.8000, 13.2000)); // Far away

        Match match = matchingEngineService.scoreReportPair(lost, found);

        assertNotNull(match);
        assertEquals(0.0, match.getScoreBreakdown().getCategoryScore());
        assertTrue(match.getScore() < 25.0, "Score should be very low for completely different items");
    }
}
