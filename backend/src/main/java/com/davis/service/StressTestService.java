package com.davis.service;

import com.davis.dto.AnalysisScoreResponse;
import com.davis.dto.StressTestRequest;
import com.davis.dto.StressTestResponse;
import com.davis.model.StressTestRecord;
import com.davis.repository.StressTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StressTestService {

    private final AnalysisService analysisService;
    private final StressTestRepository stressTestRepository;
    private final AuditService auditService;

    @Autowired
    public StressTestService(AnalysisService analysisService,
                             StressTestRepository stressTestRepository,
                             AuditService auditService) {
        this.analysisService = analysisService;
        this.stressTestRepository = stressTestRepository;
        this.auditService = auditService;
    }

    public StressTestResponse runStressTest(Long caseId, StressTestRequest request) {
        String dep = request.getRemovedDependency().toUpperCase();

        // 1. Calculate baseline original score
        AnalysisScoreResponse baseline = analysisService.runAnalysisWithDependencyExclusion(caseId, null);
        double originalScore = baseline.getTotalScore();

        // 2. Calculate modified score with dependency removed
        AnalysisScoreResponse modified = analysisService.runAnalysisWithDependencyExclusion(caseId, dep);
        double modifiedScore = modified.getTotalScore();
        double delta = Math.round((modifiedScore - originalScore) * 10.0) / 10.0;

        // 3. Robustness classification and explanation
        String rating;
        String explanation;
        String pivotRecommendation;

        if (Math.abs(delta) <= 10.0) {
            rating = "HIGH_ROBUSTNESS";
            explanation = String.format("Score shifted by %.1f points (from %.1f to %.1f). The attribution lead remains stable; remaining independent vectors sufficiently corroborate the persona.", delta, originalScore, modifiedScore);
            pivotRecommendation = "Continue lead verification; secondary evidence holds independent probative value.";
        } else if (Math.abs(delta) <= 22.0) {
            rating = "MODERATE_ROBUSTNESS";
            explanation = String.format("Score dropped by %.1f points (from %.1f to %.1f). Removing '%s' significantly weakens attribution certainty, but secondary channels preserve a viable investigative lead.", Math.abs(delta), originalScore, modifiedScore, dep);
            pivotRecommendation = "Pivot to cross-verifying secondary vectors (blockchain wallet transactions and infrastructure certificates).";
        } else {
            rating = "CRITICAL_DEPENDENCY";
            explanation = String.format("Score collapsed by %.1f points (from %.1f to %.1f). '%s' serves as a critical single point of failure in this attribution lead.", Math.abs(delta), originalScore, modifiedScore, dep);
            pivotRecommendation = "Do NOT proceed on this lead alone without corroborating independent cryptographic or forensic telemetry.";
        }

        // 4. Save record to DB
        StressTestRecord record = new StressTestRecord(caseId, dep, originalScore, modifiedScore, delta, rating, explanation);
        stressTestRepository.save(record);

        // 5. Audit log
        auditService.logAction(caseId, "STRESS_TEST_EXECUTED", "DAVIS Stress Engine", 
                "Dependency removed: " + dep + " | Original: " + originalScore + " | Modified: " + modifiedScore + " | Delta: " + delta + " | Rating: " + rating);

        // 6. Build response
        StressTestResponse response = new StressTestResponse();
        response.setRemovedDependency(dep);
        response.setOriginalScore(originalScore);
        response.setModifiedScore(modifiedScore);
        response.setScoreDelta(delta);
        response.setRobustnessRating(rating);
        response.setExplanation(explanation);
        response.setRemainingBreakdown(modified.getBreakdown());
        response.setPivotRecommendation(pivotRecommendation);

        return response;
    }

    public List<StressTestRecord> getStressTests(Long caseId) {
        return stressTestRepository.findByCaseIdOrderByCreatedAtDesc(caseId);
    }
}
