# DAVIS

## Digital Attribution & Verification Intelligence System

**Smart India Hackathon 2026 — SIH26151**

> An investigation-support platform for correlating digital indicators, analysing relationships, evaluating attribution evidence, and generating integrity-preserved investigation reports.

### Live Prototype

**Prototype:**
https://davis-production-3823.up.railway.app

**Team:** JugaaduSloths
**Version:** v1.0.0
**PS ID:** SIH26151

---

## Overview

Modern cyber investigations often involve digital identities and traces scattered across multiple sources, including usernames, email identities, PGP keys, cryptocurrency wallets, onion services, domains, messages, and public-web profiles.

Manually correlating these fragmented indicators can be time-consuming and may make it difficult for investigators to understand relationships between entities and evaluate the strength of available evidence.

**DAVIS (Digital Attribution & Verification Intelligence System)** is an investigation-support platform designed to help analysts:

* Ingest known digital indicators
* Correlate related personas and infrastructure
* Visualize relationships through an interactive graph
* Organize and classify digital evidence
* Calculate a dynamic Attribution Support Score
* Analyse stylometric similarities
* Detect potential AI-assisted text rewriting
* Perform dependency stress testing
* Maintain human-in-the-loop review
* Preserve evidence integrity using SHA-256
* Generate structured investigation reports and evidence exports

DAVIS is designed as an **analytical decision-support system**, not an autonomous identity or guilt determination system.

---

# Problem Statement

Cyber investigations frequently encounter fragmented digital traces distributed across different platforms and time periods.

Investigators may need to correlate:

```text
Username
   ↓
PGP Key
   ↓
Email / Persona
   ↓
Wallet
   ↓
Domain / Onion Service
   ↓
Infrastructure
   ↓
Messages / Evidence
```

The challenge is not only collecting these indicators, but determining whether apparently separate digital traces can be meaningfully connected while preserving evidence integrity and allowing analysts to understand why a particular attribution lead is supported.

---

# Proposed Solution

DAVIS provides a unified investigation workflow:

```text
Known Indicator
       ↓
Controlled Discovery
       ↓
Entity Extraction
       ↓
Relationship Correlation
       ↓
Evidence Collection
       ↓
Graph Construction
       ↓
Multi-Vector Analysis
       ↓
Attribution Support Score
       ↓
Dependency Stress Testing
       ↓
Human Analyst Review
       ↓
Evidence-Preserved Report
```

The system combines multiple evidence dimensions rather than relying on a single indicator.

---

# Key Features

## 1. Indicator Ingestion

Investigators can add known indicators such as:

* Usernames
* PGP keys
* Cryptocurrency wallets
* Onion services
* Domains
* Other digital identifiers

Each indicator can be associated with its source/context and confidence.

---

## 2. Entity & Relationship Correlation

DAVIS identifies and correlates entities across the investigation.

The system represents relationships between:

* Personas
* Usernames
* Cryptographic identities
* Wallets
* Infrastructure
* Domains
* Other investigation entities

An interactive **Cytoscape.js relationship graph** allows analysts to explore the correlation network.

---

## 3. Evidence Inventory

Evidence records can be classified as:

* Supporting
* Contradictory
* Corroborating
* Conflicting

Each evidence record can contain:

* Evidence content
* Source
* Classification
* Reliability
* SHA-256 hash

This helps analysts understand both supporting and conflicting evidence instead of relying only on positive matches.

---

## 4. Dynamic Attribution Support Score

DAVIS generates a normalized **Attribution Support Score from 0–100**.

The score represents the **strength of the available evidence for an attribution lead**, not proof of identity.

The scoring process considers multiple evidence dimensions and applies penalties when contradictory evidence is present.

### Example

```text
Attribution Support Score
          74.0 / 100
```

The score is dynamically recalculated when evidence dependencies are modified.

---

## 5. Stylometric Analysis

DAVIS includes text-based analysis to identify similarities between writing samples.

The prototype considers features such as:

* Vocabulary characteristics
* Sentence-length variation
* Text similarity
* Stylometric patterns

The system also considers the effect of AI-assisted paraphrasing.

When AI-rewrite behaviour is detected, the stylometric contribution can be reduced to avoid giving excessive weight to potentially transformed text.

---

## 6. Dependency Stress Testing

A major feature of DAVIS is **evidence dependency testing**.

Instead of only asking:

> "How strong is the attribution?"

DAVIS also asks:

> "How much does the conclusion change if an important dependency is removed?"

Example:

```text
Original Score
     74.0
       ↓
Remove PGP Dependency
       ↓
Modified Score
     50.5
```

This helps identify potential **single points of failure** in an attribution lead.

---

## 7. Human-in-the-Loop Review

DAVIS does not treat automated analysis as a final decision.

The workflow includes mandatory analyst review where investigators can:

* Review evidence
* Examine contradictions
* Add findings
* Record investigation decisions
* Document disposition notes

The analyst remains responsible for the final interpretation of the evidence.

---

## 8. Evidence Integrity

DAVIS uses **SHA-256 hashing** to preserve the integrity of investigation artifacts.

Hashing is applied to evidence and audit information to help detect unauthorized changes.

The system also provides a SHA-256 verification interface for checking digital artifacts against their expected digest.

---

## 9. Audit Trail

DAVIS maintains an investigation activity log containing information such as:

* Timestamp
* Action
* Actor
* Details
* Audit SHA-256

This creates a chronological record of important investigation activities.

---

## 10. Investigation Reports & Exports

The prototype supports multiple export formats:

### PDF

* Investigation report
* Evidence tables
