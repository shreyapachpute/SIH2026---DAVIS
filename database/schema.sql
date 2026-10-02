-- ==============================================================================
-- DAVIS: Digital Attribution & Verification Intelligence System
-- Database Schema (MySQL 8.0+ compatible / H2 compatible)
-- SIH 2026 | PS ID: SIH26151 | Team: JugaaduSloths (185528)
-- ==============================================================================
DROP DATABASE IF EXISTS DAVIS_DB;
CREATE DATABASE DAVIS_DB;
USE DAVIS_DB;
-- Drop tables in reverse order of dependencies
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS stress_tests;
DROP TABLE IF EXISTS analysis_results;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS text_samples;
DROP TABLE IF EXISTS relationships;
DROP TABLE IF EXISTS evidence;
DROP TABLE IF EXISTS entities;
DROP TABLE IF EXISTS indicators;
DROP TABLE IF EXISTS cases;

-- 1. CASES
CREATE TABLE cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_number VARCHAR(64) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. INDICATORS
CREATE TABLE indicators (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    type VARCHAR(64) NOT NULL,
    `value` VARCHAR(512) NOT NULL,
    source VARCHAR(255),
    confidence DOUBLE DEFAULT 0.9,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_indicators_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
);

-- 3. ENTITIES
CREATE TABLE entities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    type VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    normalized_value VARCHAR(512) NOT NULL,
    confidence DOUBLE DEFAULT 0.85,
    CONSTRAINT fk_entities_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
);

-- 4. EVIDENCE
CREATE TABLE evidence (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    type VARCHAR(64) NOT NULL,
    source VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    hash VARCHAR(64) NOT NULL,
    reliability DOUBLE DEFAULT 0.9,
    supports VARCHAR(255),
    contradicts VARCHAR(255),
    related_entities VARCHAR(512),
    CONSTRAINT fk_evidence_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
);

-- 5. RELATIONSHIPS
CREATE TABLE relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    source_entity_id BIGINT NOT NULL,
    target_entity_id BIGINT NOT NULL,
    relationship_type VARCHAR(64) NOT NULL,
    confidence DOUBLE DEFAULT 0.85,
    evidence_id BIGINT,
    CONSTRAINT fk_rel_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE,
    CONSTRAINT fk_rel_source FOREIGN KEY (source_entity_id) REFERENCES entities(id) ON DELETE CASCADE,
    CONSTRAINT fk_rel_target FOREIGN KEY (target_entity_id) REFERENCES entities(id) ON DELETE CASCADE,
    CONSTRAINT fk_rel_evidence FOREIGN KEY (evidence_id) REFERENCES evidence(id) ON DELETE SET NULL
);

-- 6. TEXT_SAMPLES (Stylometric Analysis)
CREATE TABLE text_samples (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    entity_id BIGINT,
    sample_label VARCHAR(128),
    text TEXT NOT NULL,
    language VARCHAR(32) DEFAULT 'en',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ai_rewrite_indicator BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_text_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE,
    CONSTRAINT fk_text_entity FOREIGN KEY (entity_id) REFERENCES entities(id) ON DELETE SET NULL
);

-- 7. TRANSACTIONS
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    wallet_from VARCHAR(128) NOT NULL,
    wallet_to VARCHAR(128) NOT NULL,
    amount DOUBLE NOT NULL,
    asset VARCHAR(32) DEFAULT 'BTC',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_hash VARCHAR(128) NOT NULL,
    CONSTRAINT fk_trans_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
);

-- 8. ANALYSIS_RESULTS
CREATE TABLE analysis_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    score DOUBLE NOT NULL,
    confidence DOUBLE NOT NULL,
    methodology VARCHAR(255) NOT NULL,
    breakdown_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_analysis_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
);

-- 9. STRESS_TESTS
CREATE TABLE stress_tests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    removed_dependency VARCHAR(128) NOT NULL,
    original_score DOUBLE NOT NULL,
    modified_score DOUBLE NOT NULL,
    score_delta DOUBLE NOT NULL,
    result VARCHAR(64) NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stress_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
);

-- 10. REVIEWS
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    analyst VARCHAR(128) NOT NULL,
    decision VARCHAR(64) NOT NULL,
    comments TEXT,
    reviewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
);

-- 11. AUDIT_LOGS
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    action VARCHAR(128) NOT NULL,
    actor VARCHAR(128) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT,
    hash VARCHAR(64) NOT NULL,
    CONSTRAINT fk_audit_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_cases_num ON cases(case_number);
CREATE INDEX idx_indicators_case ON indicators(case_id);
CREATE INDEX idx_entities_case ON entities(case_id);
CREATE INDEX idx_evidence_case ON evidence(case_id);
CREATE INDEX idx_rel_case ON relationships(case_id);
CREATE INDEX idx_audit_case ON audit_logs(case_id);
