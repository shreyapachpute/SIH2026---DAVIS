package com.davis.controller;

import com.davis.dto.AnalysisScoreResponse;
import com.davis.dto.ReviewRequest;
import com.davis.dto.StressTestRequest;
import com.davis.dto.StressTestResponse;
import com.davis.model.ReviewRecord;
import com.davis.model.StressTestRecord;
import com.davis.repository.ReviewRepository;
import com.davis.service.AnalysisService;
import com.davis.service.AuditService;
import com.davis.service.StressTestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final StressTestService stressTestService;
    private final ReviewRepository reviewRepository;
    private final AuditService auditService;

    @Autowired
    public AnalysisController(AnalysisService analysisService,
                              StressTestService stressTestService,
                              ReviewRepository reviewRepository,
                              AuditService auditService) {
        this.analysisService = analysisService;
        this.stressTestService = stressTestService;
        this.reviewRepository = reviewRepository;
        this.auditService = auditService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisScoreResponse> runAnalysis(@PathVariable Long caseId) {
        return ResponseEntity.ok(analysisService.runAnalysis(caseId));
    }

    @GetMapping("/analysis")
    public ResponseEntity<AnalysisScoreResponse> getAnalysis(@PathVariable Long caseId) {
        return ResponseEntity.ok(analysisService.getLatestAnalysis(caseId));
    }

    @PostMapping("/stress-test")
    public ResponseEntity<StressTestResponse> runStressTest(@PathVariable Long caseId,
                                                            @Valid @RequestBody StressTestRequest request) {
        return ResponseEntity.ok(stressTestService.runStressTest(caseId, request));
    }

    @GetMapping("/stress-test")
    public ResponseEntity<List<StressTestRecord>> getStressTests(@PathVariable Long caseId) {
        return ResponseEntity.ok(stressTestService.getStressTests(caseId));
    }

    @PostMapping("/review")
    public ResponseEntity<ReviewRecord> submitReview(@PathVariable Long caseId,
                                                     @Valid @RequestBody ReviewRequest request) {
        ReviewRecord record = new ReviewRecord(caseId, request.getAnalyst(), request.getDecision(), request.getComments());
        ReviewRecord saved = reviewRepository.save(record);
        auditService.logAction(caseId, "ANALYST_REVIEW_SUBMITTED", request.getAnalyst(), 
                "Decision: " + request.getDecision() + " | Comments: " + request.getComments());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/review")
    public ResponseEntity<ReviewRecord> getLatestReview(@PathVariable Long caseId) {
        return reviewRepository.findFirstByCaseIdOrderByReviewedAtDesc(caseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
