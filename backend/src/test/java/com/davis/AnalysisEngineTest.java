package com.davis;

import com.davis.analysis.AttributionScoreEngine;
import com.davis.analysis.StylometryEngine;
import com.davis.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisEngineTest {

    private StylometryEngine stylometryEngine;
    private AttributionScoreEngine scoreEngine;

    @BeforeEach
    void setUp() {
        stylometryEngine = new StylometryEngine();
        scoreEngine = new AttributionScoreEngine();
    }

    @Test
    void testStylometricComparison_NaturalTexts() {
        String docA = "Payment must be remitted immediately -- non-negotiable terms. Protocol enforces zero exceptions; delay results in public repository exposure.";
        String docB = "We strictly deploy custom payload packers -- avoiding default entropy metrics is trivial. The target infrastructure crumbled within twenty minutes; no compromise.";

        StylometryEngine.StylometryResult result = stylometryEngine.compareSamples(docA, docB);

        assertNotNull(result);
        assertTrue(result.getSimilarityPercentage() > 0.0);
        assertFalse(result.isAiRewriteDetected());
        assertEquals(1.0, result.getConfidenceMultiplier());
    }

    @Test
    void testStylometricComparison_AiRewrittenText() {
        String natural = "Payment must be remitted immediately -- non-negotiable terms. Protocol enforces zero exceptions.";
        String aiRewritten = "Kindly be advised that the designated cryptocurrency funds are strictly required to proceed with the restoration of the encrypted databases. Should the compliance window lapse, comprehensive disclosure of internal records will inevitably follow.";

        StylometryEngine.StylometryResult result = stylometryEngine.compareSamples(natural, aiRewritten);

        assertNotNull(result);
        assertTrue(result.isAiRewriteDetected(), "Expected AI rewrite to be flagged due to formal transition markers");
        assertEquals(0.50, result.getConfidenceMultiplier(), "Expected stylometric contribution to be reduced by 50%");
    }

    @Test
    void testAttributionScoreComputation_DynamicAndExplainable() {
        Long caseId = 1L;
        List<Evidence> evidence = new ArrayList<>();
        evidence.add(new Evidence(caseId, "CRYPTO_FORENSIC", "Explorer", "1.45 BTC transfer", LocalDateTime.now(), "hash1", 0.95, "Financial attribution", null, "1,5"));
        evidence.add(new Evidence(caseId, "PGP_KEY_SIGNATURE", "Keyserver", "PGP key signature", LocalDateTime.now(), "hash2", 0.94, "Cryptographic link", null, "1,4"));
        evidence.add(new Evidence(caseId, "CONTRADICTORY_TEMPORAL", "Forum Logs", "Active session collision", LocalDateTime.now(), "hash3", 0.87, null, "Concurrent clearnet session", "1,2"));

        List<DiscoveredEntity> entities = new ArrayList<>();
        entities.add(new DiscoveredEntity(caseId, "PERSONA", "NightFalcon", "persona:nightfalcon", 0.95));
        entities.add(new DiscoveredEntity(caseId, "PGP_KEY", "PGP 7F9A", "pgp:7f9a", 0.94));
        entities.add(new DiscoveredEntity(caseId, "WALLET", "BTC Wallet", "wallet:bc1q", 0.96));

        List<Relationship> relationships = new ArrayList<>();
        relationships.add(new Relationship(caseId, 1L, 2L, "USES_PGP", 0.94, 2L));

        List<CryptoTransaction> txs = new ArrayList<>();
        txs.add(new CryptoTransaction(caseId, "from", "to", 1.45, "BTC", LocalDateTime.now(), "tx1"));

        StylometryEngine.StylometryResult sty = new StylometryEngine.StylometryResult(78.0, 0.78, false, "Normal", 1.0, null, null);

        // Compute baseline score
        AttributionScoreEngine.ScoreComputation baseline = scoreEngine.computeScore(evidence, entities, relationships, txs, sty, null);
        assertNotNull(baseline);
        assertTrue(baseline.getTotalScore() > 40.0 && baseline.getTotalScore() <= 100.0);
        assertTrue(baseline.getBreakdown().containsKey("cryptographicContribution"));
        assertTrue(baseline.getBreakdown().containsKey("financialContribution"));
        assertTrue(baseline.getBreakdown().get("contradictionDeduction") < 0, "Contradiction deduction should be negative penalty");

        // Compute stress test with PGP removed
        AttributionScoreEngine.ScoreComputation stressPgp = scoreEngine.computeScore(evidence, entities, relationships, txs, sty, "PGP_KEY");
        assertTrue(stressPgp.getTotalScore() < baseline.getTotalScore(), "Score must decrease when PGP dependency is removed");
        assertEquals(0.0, stressPgp.getBreakdown().get("cryptographicContribution"), "PGP contribution must be 0 when removed in stress test");
    }
}
