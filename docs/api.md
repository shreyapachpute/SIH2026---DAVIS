# DAVIS REST API Documentation

## Digital Attribution & Verification Intelligence System
**SIH 2026 | Problem Statement ID: SIH26151 | Team: JugaaduSloths (185528)**

Base URL: `http://localhost:8080/api`

---

## 1. System & Health

### `GET /api/health`
Returns system health, database status, and SIH project metadata.
```json
{
  "status": "UP",
  "application": "DAVIS - Digital Attribution & Verification Intelligence System",
  "sihProblemStatement": "SIH26151",
  "theme": "Blockchain & Cybersecurity",
  "team": "JugaaduSloths (185528)",
  "database": "UP"
}
```

---

## 2. Cases

### `GET /api/cases`
Returns list of all investigation cases.

### `POST /api/cases`
Creates a new investigation case.
Request:
```json
{
  "caseNumber": "DAVIS-CASE-002",
  "title": "Operation Cipher Shadow",
  "description": "Investigation into ransomware infrastructure",
  "status": "ACTIVE"
}
```

### `POST /api/cases/demo`
Loads or resets the synthetic demo case `DAVIS-DEMO-001`.

### `GET /api/cases/{id}`
Returns details for a single case.

### `PUT /api/cases/{id}`
Updates title, description, or status of an investigation case.

---

## 3. Indicators & Collection

### `GET /api/cases/{id}/indicators`
Lists all ingested indicators (USERNAME, EMAIL, PGP_KEY, CRYPTO_WALLET, DOMAIN, ONION_ADDRESS, MESSAGE).

### `POST /api/cases/{id}/indicators`
Ingests a new indicator.
```json
{
  "type": "CRYPTO_WALLET",
  "value": "bc1q9davisdemo7falcon9synthetic3trans001",
  "source": "Ransom Demand Paste",
  "confidence": 0.95
}
```

### `POST /api/cases/{id}/collect`
Executes OPSEC-gated synthetic collection, parses public-source telemetry, extracts entities, and anchors SHA-256 evidence digests.

---

## 4. Graph & Evidence

### `GET /api/cases/{id}/graph`
Returns Cytoscape.js compatible JSON payload with `nodes` and `edges`.

### `GET /api/cases/{id}/entities`
Lists all discovered entities.

### `GET /api/cases/{id}/evidence`
Lists all evidence records with classifications (`supports`, `contradicts`), source, and SHA-256 digests.

### `GET /api/cases/{id}/timeline`
Returns chronologically sequenced timeline events.

---

## 5. Intelligence Analysis & Stress Testing

### `POST /api/cases/{id}/analyze`
Computes the dynamic Attribution Support Score across 6 dimensions with contradiction deductions.

### `POST /api/cases/{id}/stress-test`
Executes dynamic dependency removal and evaluates attribution robustness.
Request:
```json
{
  "removedDependency": "PGP_KEY"
}
```
Response:
```json
{
  "removedDependency": "PGP_KEY",
  "originalScore": 74.0,
  "modifiedScore": 50.5,
  "scoreDelta": -23.5,
  "robustnessRating": "MODERATE_ROBUSTNESS",
  "explanation": "Score dropped by 23.5 points... secondary channels preserve a viable investigative lead.",
  "pivotRecommendation": "Pivot to cross-verifying secondary vectors..."
}
```

---

## 6. Human Review

### `POST /api/cases/{id}/review`
Submits analyst sign-off and decision.
```json
{
  "analyst": "Analyst Sharma (SIH-CyberSec-Lead)",
  "decision": "NEEDS_FURTHER_INVESTIGATION",
  "comments": "Temporal conflict noted between clearnet and darknet sessions."
}
```

---

## 7. Reports, Exports & Verification

### `GET /api/cases/{id}/report/pdf`
Downloads the official investigation report PDF (OpenPDF generated).

### `GET /api/cases/{id}/certificate`
Downloads the formal Evidence Integrity Certificate PDF.

### `GET /api/cases/{id}/export/csv`
Downloads evidence and entity matrix as CSV.

### `GET /api/cases/{id}/export/json`
Downloads complete investigation package as structured JSON.

### `POST /api/cases/{id}/verify-integrity`
Verifies raw data against an expected SHA-256 digest.
```json
{
  "data": "Sample text or payload",
  "hash": "a3f5c1d89..."
}
```
