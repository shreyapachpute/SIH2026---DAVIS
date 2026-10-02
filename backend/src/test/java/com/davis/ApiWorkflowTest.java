package com.davis;

import com.davis.dto.*;
import com.davis.model.CaseEntity;
import com.davis.model.Indicator;
import com.davis.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApiWorkflowTest {

    @Autowired
    private CaseService caseService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private EntityResolutionService entityResolutionService;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private StressTestService stressTestService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private IntegrityService integrityService;

    @Test
    void testCompleteInvestigationWorkflow() {
        // 1. Create / Verify Demo Case exists
        List<CaseDto> cases = caseService.getAllCases();
        assertFalse(cases.isEmpty(), "Demo case should be automatically seeded on startup");
        Long caseId = cases.get(0).getId();

        // 2. Query Graph
        GraphResponseDto graph = entityResolutionService.getGraph(caseId);
        assertNotNull(graph);
        assertFalse(graph.getNodes().isEmpty(), "Graph must contain discovered entity nodes");
        assertFalse(graph.getEdges().isEmpty(), "Graph must contain relationship edges");

        // 3. Run Controlled Collection
        CollectionResultDto collection = collectionService.runControlledCollection(caseId);
        assertNotNull(collection);
        assertEquals("SUCCESS", collection.getStatus());
        assertEquals("SYNTHETIC CONTROLLED INVESTIGATION DATA", collection.getCollectionScope());

        // 4. Compute Dynamic Attribution Score
        AnalysisScoreResponse analysis = analysisService.runAnalysis(caseId);
        assertNotNull(analysis);
        assertTrue(analysis.getTotalScore() > 0.0);
        assertNotNull(analysis.getBreakdown());
        assertTrue(analysis.getBreakdown().get("totalScore") > 0.0);

        // 5. Run Dependency Stress Test
        StressTestRequest stressReq = new StressTestRequest("PGP_KEY");
        StressTestResponse stressResp = stressTestService.runStressTest(caseId, stressReq);
        assertNotNull(stressResp);
        assertEquals("PGP_KEY", stressResp.getRemovedDependency());
        assertTrue(stressResp.getOriginalScore() > stressResp.getModifiedScore(), "Original score must be higher than score after removing PGP");
        assertTrue(stressResp.getScoreDelta() < 0, "Score delta must be negative");

        // 6. Test PDF Report Generation
        byte[] pdfReport = exportService.exportPdfReport(caseId);
        assertNotNull(pdfReport);
        assertTrue(pdfReport.length > 500, "PDF report should be generated with substantial byte size");

        // 7. Test PDF Evidence Integrity Certificate Generation
        byte[] certPdf = exportService.exportIntegrityCertificate(caseId);
        assertNotNull(certPdf);
        assertTrue(certPdf.length > 500, "Evidence certificate should be generated");

        // 8. Test CSV and JSON Export
        String csv = exportService.exportCsv(caseId);
        assertNotNull(csv);
        assertTrue(csv.contains("CaseNumber,EntityType,EntityName"));

        String json = exportService.exportJson(caseId);
        assertNotNull(json);
        assertTrue(json.contains("DAVIS-DEMO-001"));
        assertTrue(json.contains("masterSha256"));

        // 9. Verify SHA-256 integrity calculation
        String testData = "TEST_INTEGRITY_DATA";
        String hash = integrityService.computeSha256(testData);
        var verify = integrityService.verifyIntegrity(testData, hash);
        assertEquals("VALID", verify.get("status"));
        assertTrue((Boolean) verify.get("match"));
    }
}
