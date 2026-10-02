-- ==============================================================================
-- DAVIS: Synthetic Controlled Demonstration Seed Data
-- Case ID: DAVIS-DEMO-001 (Threat Actor Persona: "NightFalcon")
-- ALL DATA IS ENTIRELY FICTIONAL AND SYNTHETIC FOR DEMONSTRATION PURPOSES ONLY
-- ==============================================================================

-- 1. Demo Case
INSERT INTO cases (id, case_number, title, description, status, created_at, updated_at) VALUES 
(1, 'DAVIS-DEMO-001', 'Operation Falcon Trace: Dark Web Extortion & Attribution Lead', 
 'Investigation into synthetic dark-web ransomware broker operating under pseudonym NightFalcon across decentralized forums and Tor onion services.', 
 'ACTIVE', '2026-08-01 09:00:00', '2026-08-12 16:30:00');

-- 2. Indicators
-- normalized_value is used to prevent duplicate indicators
-- within the same case while preserving the original 'value'.
INSERT INTO indicators
    (id, case_id, type, value, normalized_value, source, confidence, created_at)
VALUES 
(1, 1, 'USERNAME', 'NightFalcon', 'nightfalcon',
 'Darknet Market Extortion Post #8419', 0.95, '2026-08-01 09:30:00'),

(2, 1, 'EMAIL', 'nightfalcon.demo@example.invalid', 'nightfalcon.demo@example.invalid',
 'Encrypted paste leak metadata', 0.88, '2026-08-02 11:15:00'),

(3, 1, 'PGP_KEY',
 '7F9A 4B2C 8E1D 0F3A 5C6B 9D2E 1A4F 3B8C 7E0D 2F1A',
 '7f9a 4b2c 8e1d 0f3a 5c6b 9d2e 1a4f 3b8c 7e0d 2f1a',
 'Keyserver HKP query (synthetic)', 0.92, '2026-08-03 14:00:00'),

(4, 1, 'CRYPTO_WALLET',
 'bc1q9davisdemo7falcon9synthetic3trans001',
 'bc1q9davisdemo7falcon9synthetic3trans001',
 'Ransom negotiation note leak', 0.96, '2026-08-05 10:20:00'),

(5, 1, 'DOMAIN',
 'falcon-ops.example.invalid',
 'falcon-ops.example.invalid',
 'WHOIS historical certificate telemetry', 0.82, '2026-08-06 17:45:00'),

(6, 1, 'ONION_ADDRESS',
 'falconsec7synthx3darkdemo.onion',
 'falconsec7synthx3darkdemo.onion',
 'Tor directory index crawl', 0.90, '2026-08-07 13:10:00');
-- 3. Discovered Entities
INSERT INTO entities (id, case_id, type, name, normalized_value, confidence) VALUES 
(1, 1, 'PERSONA', 'NightFalcon Primary', 'persona:nightfalcon', 0.95),
(2, 1, 'ALIAS', 'NF_27 (Clearnet)', 'alias:nf_27', 0.82),
(3, 1, 'EMAIL', 'nightfalcon.demo@example.invalid', 'email:nightfalcon.demo@example.invalid', 0.90),
(4, 1, 'PGP_KEY', 'PGP Fingerprint 7F9A...2F1A', 'pgp:7f9a4b2c8e1d0f3a5c6b9d2e1a4f3b8c7e0d2f1a', 0.94),
(5, 1, 'WALLET', 'BTC Vault (bc1q9davis...001)', 'wallet:bc1q9davisdemo7falcon9synthetic3trans001', 0.96),
(6, 1, 'DOMAIN', 'falcon-ops.example.invalid', 'domain:falcon-ops.example.invalid', 0.84),
(7, 1, 'ONION_SERVICE', 'falconsec...onion', 'onion:falconsec7synthx3darkdemo.onion', 0.91),
(8, 1, 'FORUM_ACCOUNT', 'ShadowForum UID: 10482', 'forum:shadowforum_10482', 0.89),
(9, 1, 'ALIAS', 'CypherSentinel (Conflicting)', 'alias:cyphersentinel', 0.65),
(10, 1, 'WALLET', 'Secondary Cashout bc1q9interm2', 'wallet:bc1q9interm2cashout999synth', 0.88);

-- 4. Evidence (Supporting & Contradictory)
INSERT INTO evidence (id, case_id, type, source, content, timestamp, hash, reliability, supports, contradicts, related_entities) VALUES 
(1, 1, 'CRYPTO_FORENSIC', 'Synthetic Blockchain Explorer', 'Direct inbound transfer of 1.45 BTC from ransom deposit wallet to bc1q9davisdemo7falcon9synthetic3trans001 matching extortion demand.', '2026-08-07 10:15:00', 'a3f5c1d89e2467b0198f2e4c9a87d1e345b67890123456789abcdef012345678', 0.95, 'Financial attribution to NightFalcon', NULL, '5,10'),
(2, 1, 'PGP_KEY_SIGNATURE', 'Keyserver HKP Archive', 'PGP public key 7F9A...2F1A associated with user-ID nightfalcon.demo@example.invalid and self-signed timestamp 2026-02-14.', '2026-08-03 14:10:00', 'b7e21a9c34d5f60871234567890abcdef1234567890abcdef1234567890abcde', 0.94, 'Cryptographic link between persona and email', NULL, '1,3,4'),
(3, 1, 'INFRASTRUCTURE_TELEMETRY', 'Synthetic Passive DNS / TLS Logs', 'TLS certificate CN falcon-ops.example.invalid matched onion service ephemeral host config via shared SSL serial 0x4A1F890C.', '2026-08-06 18:00:00', 'c8d4e2f1a90b56781234567890abcdef1234567890abcdef1234567890abcdef', 0.88, 'Network bridge between clearnet and darknet infrastructure', NULL, '6,7'),
(4, 1, 'TEMPORAL_ACTIVITY', 'ShadowForum & GitHub Telemetry', 'Burst activity between UTC 02:00-06:00 consistently observed across darknet forum posts and repository commits.', '2026-08-08 09:30:00', 'd1f2a3b4c5e678901234567890abcdef1234567890abcdef1234567890abcdef', 0.85, 'Behavioural operating-hours overlap', NULL, '1,8'),
(5, 1, 'STYLOMETRIC_MATCH', 'Extortion Notes vs Forum Posts', 'TF-IDF cosine similarity 78.4% with idiosyncratic double-hyphen punctuation and specialized jargon.', '2026-08-09 11:20:00', 'e5a4b3c2d10987651234567890abcdef1234567890abcdef1234567890abcdef', 0.80, 'Stylometric correlation between extortion text and author sample', NULL, '1,8'),
(6, 1, 'CONTRADICTORY_TEMPORAL', 'Clearnet Forum SecNet Logs', 'Active clearnet forum posts from alias NF_27 logged at UTC 14:30 while darknet persona was concurrently in live peer chat.', '2026-08-09 14:45:00', 'f9e8d7c6b5a432101234567890abcdef1234567890abcdef1234567890abcdef', 0.87, NULL, 'Contradicts singular identity assumption between NightFalcon and NF_27 (Dual Operator Hypothesis)', '1,2'),
(7, 1, 'CONTRADICTORY_CRYPTOGRAPHIC', 'PGP Web-of-Trust Analysis', 'Subkey 0x3A1C revokes primary encryption key, signed with key ID belonging to third-party researcher CypherSentinel.', '2026-08-10 16:00:00', '1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef', 0.76, NULL, 'Contradicts exclusive control of PGP keypair (potential false-flag or key reuse)', '4,9'),
(8, 1, 'AI_REWRITE_SIGNAL', 'Automated Linguistic Scanner', 'Extortion communication sample C displays flattened vocabulary distribution and uniform syntactic depth indicative of LLM paraphrasing.', '2026-08-11 08:15:00', '2345678901abcdef1234567890abcdef1234567890abcdef1234567890abcdef', 0.82, NULL, 'Downgrades stylometric reliability of Sample C due to synthetic AI rewriting', '1');

-- 5. Relationships
INSERT INTO relationships (id, case_id, source_entity_id, target_entity_id, relationship_type, confidence, evidence_id) VALUES 
(1, 1, 1, 2, 'OWNS_ALIAS', 0.72, 6),
(2, 1, 1, 3, 'REGISTERED_WITH', 0.92, 2),
(3, 1, 1, 4, 'USES_PGP', 0.94, 2),
(4, 1, 1, 5, 'CONTROLS_WALLET', 0.95, 1),
(5, 1, 1, 7, 'OPERATES_SERVICE', 0.89, 3),
(6, 1, 7, 6, 'BRIDGED_TO_DOMAIN', 0.86, 3),
(7, 1, 1, 8, 'POSTS_ON_FORUM', 0.91, 4),
(8, 1, 5, 10, 'TRANSFERS_TO', 0.88, 1),
(9, 1, 4, 9, 'SIGNED_BY_CONFLICT', 0.70, 7);

-- 6. Text Samples (Stylometry & AI Rewrite)
INSERT INTO text_samples (id, case_id, entity_id, sample_label, text, language, timestamp, ai_rewrite_indicator) VALUES 
(1, 1, 1, 'Extortion Demand Note (Darknet)', 
 'Payment of 1.45 BTC must be remitted immediately -- non-negotiable terms. Data decryption mirrors will be shredded irrevocably upon block 894000. Protocol enforces zero exceptions; delay results in public repository exposure.', 
 'en', '2026-08-01 10:00:00', FALSE),
(2, 1, 8, 'ShadowForum Technical Discussion', 
 'We strictly deploy custom payload packers -- avoiding default entropy metrics is trivial. The target infrastructure crumbled within twenty minutes once persistence was attained; no compromise on operational secrecy.', 
 'en', '2026-08-04 03:15:00', FALSE),
(3, 1, 1, 'Paraphrased Extortion Follow-up', 
 'Kindly be advised that the designated cryptocurrency funds are strictly required to proceed with the restoration of the encrypted databases. Should the compliance window lapse, comprehensive disclosure of internal proprietary records will inevitably follow.', 
 'en', '2026-08-09 12:00:00', TRUE);

-- 7. Transactions
INSERT INTO transactions (id, case_id, wallet_from, wallet_to, amount, asset, timestamp, transaction_hash) VALUES 
(1, 1, '1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa_demo', 'bc1q9davisdemo7falcon9synthetic3trans001', 1.45000000, 'BTC', '2026-08-07 10:14:22', 'tx7f8a9b1c2d3e4f5061728394a5b6c7d8e9f0123456789abcdef0123456789a'),
(2, 1, 'bc1q9davisdemo7falcon9synthetic3trans001', 'bc1q9interm2cashout999synth', 0.72500000, 'BTC', '2026-08-07 16:45:10', 'tx8b9c0d1e2f3a4b5c6d7e8f90123456789abcdef0123456789abcdef012345b'),
(3, 1, 'bc1q9davisdemo7falcon9synthetic3trans001', 'bc1qcoldstoragefalconholding99synth', 0.71000000, 'BTC', '2026-08-07 18:20:05', 'tx9c0d1e2f3a4b5c6d7e8f9a0123456789abcdef0123456789abcdef012345c');

-- 8. Analysis Result (Dynamic Baseline)
INSERT INTO analysis_results (id, case_id, score, confidence, methodology, breakdown_json, created_at) VALUES 
(1, 1, 74.0, 0.88, 'DAVIS Multi-Vector Attribution Model v2.6 (Weighted Evidence - Contradiction Penalties)', 
 '{"aliasContribution":12.5,"cryptographicContribution":23.5,"financialContribution":19.2,"infrastructureContribution":13.8,"stylometricContribution":11.0,"temporalContribution":8.0,"contradictionDeduction":-14.0,"totalScore":74.0}', 
 '2026-08-11 10:00:00');

-- 9. Stress Test Baseline
INSERT INTO stress_tests (id, case_id, removed_dependency, original_score, modified_score, score_delta, result, explanation, created_at) VALUES 
(1, 1, 'PGP_KEY', 74.0, 50.5, -23.5, 'MODERATE_ROBUSTNESS', 'Removing PGP cryptographic correlation drops attribution support to 50.5/100. Financial and infrastructure vectors maintain secondary lead.', '2026-08-11 14:30:00'),
(2, 1, 'CRYPTO_WALLET', 74.0, 54.8, -19.2, 'MODERATE_ROBUSTNESS', 'Removing blockchain transaction correlation retains cryptographic and infrastructure vectors with score 54.8/100.', '2026-08-11 14:35:00');

-- 10. Human Review Record
INSERT INTO reviews (id, case_id, analyst, decision, comments, reviewed_at) VALUES 
(1, 1, 'Analyst Sharma (SIH-CyberSec-Lead)', 'NEEDS_FURTHER_INVESTIGATION', 
 'Strong cryptographic and financial correlation observed. However, temporal contradiction between NF_27 and NightFalcon live sessions suggests secondary operative or proxy operator. Recommend pivoting to subkey issuer CypherSentinel before concluding attribution lead.', 
 '2026-08-12 11:30:00');

-- 11. Audit Logs (with SHA-256 integrity linking)
INSERT INTO audit_logs (id, case_id, action, actor, timestamp, details, hash) VALUES 
(1, 1, 'CASE_CREATED', 'System', '2026-08-01 09:00:00', 'Synthetic case DAVIS-DEMO-001 initialized for demonstration.', '4a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b'),
(2, 1, 'INDICATOR_ADDED', 'Analyst Sharma', '2026-08-01 09:30:00', 'Added seed username NightFalcon from extortion post.', '7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c'),
(3, 1, 'COLLECTION_SIMULATED', 'System Agent', '2026-08-07 13:30:00', 'Executed OPSEC-gated synthetic collection across darknet and clearnet sources.', '1c2d3e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d'),
(4, 1, 'ANALYSIS_EXECUTED', 'DAVIS Core Engine', '2026-08-11 10:00:00', 'Computed Attribution Support Score: 74.0/100 with active contradiction deduction.', '8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e'),
(5, 1, 'STRESS_TEST_RUN', 'Analyst Sharma', '2026-08-11 14:30:00', 'Dependency stress test: PGP removed. Delta: -23.5.', '3e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f'),
(6, 1, 'ANALYST_REVIEW_SAVED', 'Analyst Sharma', '2026-08-12 11:30:00', 'Review saved with decision NEEDS_FURTHER_INVESTIGATION.', '5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b');
