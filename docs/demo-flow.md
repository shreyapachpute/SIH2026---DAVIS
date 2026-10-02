# DAVIS — Required SIH 2026 Demonstration Flow

## Problem Statement: Dark Web Threat Actor Deanonymization (PS ID: SIH26151)
## Team: JugaaduSloths | Team ID: 185528

This document outlines the step-by-step procedure to execute the complete end-to-end evaluation flow during jury and hackathon evaluations.

---

## 21-Step Demonstration Procedure

### Step 1: Open DAVIS Interface
1. Launch backend using `scripts\start.bat` or `mvn spring-boot:run`.
2. Open browser to `http://localhost:8080`.
3. Confirm the dark cyber-forensic UI loads with the header:
   `DAVIS INTEL | Digital Attribution & Verification Intelligence System | SIH26151`
   and the top badge: `Synthetic Controlled Data`.

### Step 2: Load Synthetic Demo Case
1. Click **"Load Demo Case"** in the top navigation bar.
2. Confirm the case changes to:
   `DAVIS-DEMO-001: Operation Falcon Trace: Dark Web Extortion & Attribution Lead`.

### Step 3: Inspect Ingested Indicators
1. Navigate to the **"Investigation"** tab.
2. Review the ingested seed indicators:
   * **Username**: `NightFalcon`
   * **Email**: `nightfalcon.demo@example.invalid`
   * **PGP Key**: `7F9A 4B2C 8E1D 0F3A 5C6B 9D2E 1A4F 3B8C 7E0D 2F1A`
   * **Crypto Wallet**: `bc1q9davisdemo7falcon9synthetic3trans001`
   * **Domain**: `falcon-ops.example.invalid`
   * **Onion Service**: `falconsec7synthx3darkdemo.onion`

### Step 4: Run OPSEC-Gated Controlled Investigation
1. Click **"START CONTROLLED INVESTIGATION"**.
2. Observe the terminal logger progress through:
   * `[OPSEC GATE]` Verifying sandbox safeguards.
   * `[SIMULATED FEED]` Querying darknet mirror indices and HKP archives.
   * `[ENTITY RESOLUTION]` Correlating identities and normalising aliases.
   * `[INTEGRITY]` Computing SHA-256 evidence digests.

### Step 5: Review Discovered Entities
1. Inspect the extracted personas:
   * `NightFalcon Primary` (Persona)
   * `NF_27 (Clearnet)` (Alias)
   * `CypherSentinel` (Conflicting Subkey Holder)
   * `BTC Vault` (Wallet)

### Step 6: Explore Interactive Relationship Graph
1. Navigate to the **"Relationship Graph"** tab.
2. View the Cytoscape.js force-directed network diagram.
3. Observe color-coded nodes:
   * **Cyan Diamond**: Persona (`NightFalcon`)
   * **Purple Hexagon**: Aliases (`NF_27`, `CypherSentinel`)
   * **Green Box**: PGP Key
   * **Gold Octagon**: Crypto Wallets
   * **Red Star**: Tor Onion Service
   * **Red Dashed Edge**: `SIGNED_BY_CONFLICT` / Contradiction link

### Step 7: Select Node Inspector
1. Click on node `NightFalcon Primary`.
2. Inspect the floating Node Details Inspector showing normalized value and confidence level.

### Step 8: Examine Supporting & Contradictory Evidence
1. Navigate to **"Evidence Inventory"**.
2. Verify:
   * **Supporting evidence**: 1.45 BTC transfer matching ransom extortion note, PGP signature to email, TLS serial match.
   * **Contradictory evidence**:
     1. Clearnet active session from `NF_27` concurrent with darknet peer chat (temporal contradiction).
     2. PGP subkey signed by independent researcher pseudonym `CypherSentinel`.

### Step 9: Review Investigation Timeline
1. Navigate to **"Timeline"**.
2. Examine the chronologically sequenced events from indicator ingestion to blockchain transfers.

### Step 10: Run Attribution Support Analysis
1. Navigate to **"Attribution Analysis"**.
2. Observe the calculated **Attribution Support Score: 74.0 / 100**.
3. Point out that the system generates an analytical lead, not a "100% Guaranteed Identity".

### Step 11: Inspect Vector Strength Radar & Breakdown Table
1. Review the 6 analytical dimensions in the Chart.js Radar Chart:
   * Cryptographic (PGP) Correlation: **+23.5**
   * Financial (Blockchain) Correlation: **+19.2**
   * Infrastructure (Tor & DNS) Correlation: **+13.8**
   * Alias / Identity Correlation: **+12.5**
   * Stylometric Match: **+11.0**
   * Temporal Overlap: **+8.0**
2. Point out the **Contradiction Deduction: -14.0** penalty applied for conflicting clearnet activity and subkey conflicts.

### Step 12: Demonstrate AI-Rewrite Awareness
1. In the Stylometric panel, show the badge:
   `⚠ POTENTIAL AI-ASSISTED REWRITING DETECTED`
2. Explain that Sample C was detected with formal paraphrasing markers; stylometric weighting was automatically halved to prevent false-attribution artifacts.

### Step 13: Open Stress Testing Engine
1. Navigate to **"Stress Testing"**.
2. Select **"PGP Cryptographic Correlation"** from the dependency dropdown.

### Step 14: Execute Dependency Stress Test
1. Click **"RUN STRESS TEST"**.
2. Observe the dynamic recalculation:
   * **Original Score**: `74.0 / 100`
   * **Modified Score**: `50.5 / 100`
   * **Delta**: `-23.5`
   * **Robustness Rating**: `MODERATE_ROBUSTNESS`
3. Point out that financial and infrastructure telemetry still maintain a secondary lead even without PGP.

### Step 15: Repeat with Stylometry Dependency
1. Select **"Stylometric & Linguistic Evidence"** and run stress test.
2. Note that score drops only from `74.0` to `63.0` (`-11.0`), evaluated as `HIGH_ROBUSTNESS` because cryptographic and financial anchors dominate.

### Step 16: Human Analyst Review
1. Navigate to **"Analyst Review"**.
2. Observe the analyst sign-off interface:
   * Analyst: `Analyst Sharma (SIH-CyberSec-Lead)`
   * Decision: `NEEDS_FURTHER_INVESTIGATION`
   * Comments: Noting the temporal conflict with alias `NF_27` before concluding lead.
3. Submit review to anchor it into the audit trail.

### Step 17: Generate Final PDF Investigation Report
1. Navigate to **"Reports & Exports"**.
2. Click **"Download PDF Report"**.
3. Open the downloaded PDF:
   * Clean, professional government-investigation layout.
   * Case metadata, Attribution Support breakdown, evidence tables with SHA-256 hashes, stress test results, and legal disclaimers referencing the Bharatiya Sakshya Adhiniyam (BSA) 2023.

### Step 18: Download Evidence Integrity Certificate
1. Click **"Download Certificate"**.
2. View the formal digital chain-of-custody document with cryptographic attestation and master SHA-256 seal.

### Step 19: Export CSV & Structured JSON
1. Click **"Download CSV"** and **"Download JSON"**.
2. Inspect the structured JSON output with case details, entities, relationships, evidence, timeline, and master integrity seal.

### Step 20: Test SHA-256 Tamper Verification Engine
1. In the "SHA-256 Tamper Verification Engine" box:
   * Enter test content.
   * Click "Verify Cryptographic Integrity".
   * Observe `VALID (SHA-256 MATCH)` or `TAMPERED / MODIFIED`.

### Step 21: Review Immutable Audit Trail
1. Navigate to **"Audit Trail"**.
2. Verify that every single action (case creation, indicator addition, collection, analysis, stress test, review, exports) is permanently logged with an individual SHA-256 hash.
