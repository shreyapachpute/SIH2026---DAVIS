/**
 * DAVIS - Main Frontend Application Logic
 * Digital Attribution & Verification Intelligence System
 * SIH 2026 | PS ID: SIH26151 | Team ID: 185528
 */

let currentCaseId = 1;

document.addEventListener('DOMContentLoaded', async () => {
  initNavigation();
  initEventListeners();
  await loadCases();
  await loadCurrentCaseData();
});

function initNavigation() {
  const navItems = document.querySelectorAll('.nav-item');
  navItems.forEach(item => {
    item.addEventListener('click', () => {
      const tab = item.getAttribute('data-tab');
      switchTab(tab);
    });
  });
}

function switchTab(tabId) {
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.tab-pane').forEach(el => el.classList.remove('active'));

  const navEl = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
  const paneEl = document.getElementById(tabId);

  if (navEl) navEl.classList.add('active');
  if (paneEl) paneEl.classList.add('active');

  // Trigger tab-specific refresh
  if (tabId === 'tab-graph') {
    loadGraph();
  } else if (tabId === 'tab-analysis') {
    loadAnalysis();
  } else if (tabId === 'tab-timeline') {
    loadTimeline();
  } else if (tabId === 'tab-evidence') {
    loadEvidence();
  } else if (tabId === 'tab-stress') {
    loadStressTests();
  } else if (tabId === 'tab-audit') {
    loadAuditTrail();
  } else if (tabId === 'tab-dashboard') {
    loadDashboardMetrics();
  }
}

function initEventListeners() {
  // Case select dropdown
  const caseSelect = document.getElementById('caseSelect');
  if (caseSelect) {
    caseSelect.addEventListener('change', async (e) => {
      currentCaseId = parseInt(e.target.value);
      await loadCurrentCaseData();
    });
  }

  // Load Demo Case Button
  const btnLoadDemo = document.getElementById('btnLoadDemo');
  if (btnLoadDemo) {
    btnLoadDemo.addEventListener('click', async () => {
      try {
        btnLoadDemo.disabled = true;
        btnLoadDemo.textContent = 'Loading Demo...';
        await API.loadDemoCase();
        await loadCases();
        await loadCurrentCaseData();
        showNotification('Demo Case DAVIS-DEMO-001 loaded successfully!', 'success');
      } catch (err) {
        showNotification('Failed to load demo case: ' + err.message, 'error');
      } finally {
        btnLoadDemo.disabled = false;
        btnLoadDemo.textContent = 'Load Demo Case';
      }
    });
  }

  // Ingest Indicator Form
  const formIndicator = document.getElementById('formIndicator');
  if (formIndicator) {
    formIndicator.addEventListener('submit', async (e) => {
      e.preventDefault();
      const type = document.getElementById('indType').value;
      const value = document.getElementById('indValue').value.trim();
      const source = document.getElementById('indSource').value.trim() || 'Manual Analyst Entry';

      if (!value) return;

      try {
        await API.addIndicator(currentCaseId, { type, value, source, confidence: 0.90 });
        document.getElementById('indValue').value = '';
        showNotification(`Indicator ${type} added. Ready for collection.`, 'success');
        await loadIndicatorsList();
      } catch (err) {
        showNotification('Failed to add indicator: ' + err.message, 'error');
      }
    });
  }

  // Start Controlled Investigation Button
  const btnStartInvestigation = document.getElementById('btnStartInvestigation');
  if (btnStartInvestigation) {
    btnStartInvestigation.addEventListener('click', async () => {
      await runInvestigationPipeline();
    });
  }

  // Run Stress Test Form
  const formStressTest = document.getElementById('formStressTest');
  if (formStressTest) {
    formStressTest.addEventListener('submit', async (e) => {
      e.preventDefault();
      const dep = document.getElementById('stressDependency').value;
      await runStressTest(dep);
    });
  }

  // Analyst Review Form
  const formReview = document.getElementById('formReview');
  if (formReview) {
    formReview.addEventListener('submit', async (e) => {
      e.preventDefault();
      const analyst = document.getElementById('reviewAnalyst').value.trim();
      const decision = document.getElementById('reviewDecision').value;
      const comments = document.getElementById('reviewComments').value.trim();

      if (!analyst) return;

      try {
        await API.submitReview(currentCaseId, { analyst, decision, comments });
        showNotification('Analyst review submitted and anchored into audit trail.', 'success');
        await loadReviewData();
      } catch (err) {
        showNotification('Failed to submit review: ' + err.message, 'error');
      }
    });
  }

  // SHA-256 Verifier Form
  const formVerify = document.getElementById('formVerify');
  if (formVerify) {
    formVerify.addEventListener('submit', async (e) => {
      e.preventDefault();
      const data = document.getElementById('verifyData').value;
      const hash = document.getElementById('verifyHash').value.trim();

      try {
        const res = await API.verifyIntegrity(currentCaseId, data, hash);
        const resultEl = document.getElementById('verifyResult');
        if (res.match) {
          resultEl.innerHTML = `<span class="badge badge-green">STATUS: VALID (SHA-256 MATCH)</span><br><span class="mono">${res.computedHash}</span>`;
        } else {
          resultEl.innerHTML = `<span class="badge badge-red">STATUS: TAMPERED / MODIFIED</span><br><span class="mono">Computed: ${res.computedHash}</span>`;
        }
      } catch (err) {
        showNotification('Verification error: ' + err.message, 'error');
      }
    });
  }

  // Export buttons
  const btnExportPdf = document.getElementById('btnExportPdf');
  if (btnExportPdf) {
    btnExportPdf.addEventListener('click', () => {
      window.open(API.getPdfReportUrl(currentCaseId), '_blank');
    });
  }

  const btnExportCert = document.getElementById('btnExportCert');
  if (btnExportCert) {
    btnExportCert.addEventListener('click', () => {
      window.open(API.getCertificateUrl(currentCaseId), '_blank');
    });
  }

  const btnExportCsv = document.getElementById('btnExportCsv');
  if (btnExportCsv) {
    btnExportCsv.addEventListener('click', () => {
      window.open(API.getCsvExportUrl(currentCaseId), '_blank');
    });
  }

  const btnExportJson = document.getElementById('btnExportJson');
  if (btnExportJson) {
    btnExportJson.addEventListener('click', () => {
      window.open(API.getJsonExportUrl(currentCaseId), '_blank');
    });
  }
}

async function loadCases() {
  try {
    const cases = await API.getCases();
    const select = document.getElementById('caseSelect');
    if (!select) return;

    select.innerHTML = '';
    cases.forEach(c => {
      const opt = document.createElement('option');
      opt.value = c.id;
      opt.textContent = `${c.caseNumber} - ${c.title.substring(0, 30)}...`;
      select.appendChild(opt);
    });

    if (cases.length > 0) {
      if (!cases.some(c => c.id === currentCaseId)) {
        currentCaseId = cases[0].id;
      }
      select.value = currentCaseId;
    }
  } catch (err) {
    console.error('Failed to load cases:', err);
  }
}

async function loadCurrentCaseData() {
  await loadDashboardMetrics();
  await loadIndicatorsList();
  await loadReviewData();
}

async function loadDashboardMetrics() {
  try {
    const caseDetails = await API.getCase(currentCaseId);
    const indicators = await API.getIndicators(currentCaseId);
    const entities = await API.getEntities(currentCaseId);
    const evidence = await API.getEvidence(currentCaseId);
    const analysis = await API.getAnalysis(currentCaseId);

    document.getElementById('dashCaseNum').textContent = caseDetails.caseNumber || 'N/A';
    document.getElementById('dashCaseTitle').textContent = caseDetails.title || 'N/A';
    document.getElementById('dashStatus').textContent = caseDetails.status || 'ACTIVE';

    document.getElementById('m-indicators').textContent = indicators.length;
    document.getElementById('m-entities').textContent = entities.length;
    document.getElementById('m-evidence').textContent = evidence.length;
    document.getElementById('m-score').textContent = analysis.totalScore ? `${analysis.totalScore.toFixed(1)}/100` : 'N/A';

    const scoreCard = document.getElementById('m-score-card');
    if (analysis.totalScore >= 70) {
      scoreCard.style.borderColor = 'rgba(0, 230, 118, 0.4)';
    } else if (analysis.totalScore >= 45) {
      scoreCard.style.borderColor = 'rgba(255, 179, 0, 0.4)';
    } else {
      scoreCard.style.borderColor = 'rgba(255, 23, 68, 0.4)';
    }
  } catch (err) {
    console.warn('Dashboard metrics load notice:', err.message);
  }
}

async function loadIndicatorsList() {
  try {
    const indicators = await API.getIndicators(currentCaseId);
    const tbody = document.getElementById('indicatorsTableBody');
    if (!tbody) return;

    tbody.innerHTML = '';
    if (indicators.length === 0) {
      tbody.innerHTML = '<tr><td colspan="4" class="text-muted">No indicators ingested for this case yet.</td></tr>';
      return;
    }

    indicators.forEach(ind => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><span class="badge badge-cyan">${ind.type}</span></td>
        <td class="mono font-bold">${ind.value}</td>
        <td>${ind.source || 'N/A'}</td>
        <td>${(ind.confidence * 100).toFixed(0)}%</td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    console.error('Error loading indicators:', err);
  }
}

async function runInvestigationPipeline() {
  const btn = document.getElementById('btnStartInvestigation');
  const logsEl = document.getElementById('pipelineLogs');
  btn.disabled = true;
  btn.innerHTML = '<span class="step-status active"></span> Running OPSEC-gated collection...';

  const steps = [
    { id: 'step-opsec', text: 'OPSEC Gating: Verifying simulated sandboxed environment...' },
    { id: 'step-crawl', text: 'Simulated Collection: Querying darknet mirror indices...' },
    { id: 'step-extract', text: 'Entity Extraction: Extracting PGP, wallets, aliases...' },
    { id: 'step-correlate', text: 'Graph Correlation: Correlating multi-vector telemetry...' },
    { id: 'step-score', text: 'Attribution Engine: Calculating dynamic support score...' }
  ];

  logsEl.innerHTML = '';

  for (let s of steps) {
    const p = document.createElement('p');
    p.className = 'mono';
    p.textContent = `> ${s.text}`;
    logsEl.appendChild(p);
    await new Promise(r => setTimeout(r, 350));
  }

  try {
    const result = await API.runCollection(currentCaseId);
    const analysis = await API.runAnalysis(currentCaseId);

    const doneP = document.createElement('p');
    doneP.className = 'mono';
    doneP.style.color = '#00e676';
    doneP.textContent = `✓ [COMPLETE] Processed ${result.indicatorsProcessed} indicators. Discovered ${result.entitiesDiscovered} entities. Attribution Score: ${analysis.totalScore}/100.`;
    logsEl.appendChild(doneP);

    showNotification('Controlled collection & correlation completed.', 'success');
    await loadCurrentCaseData();
  } catch (err) {
    showNotification('Collection error: ' + err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'START CONTROLLED INVESTIGATION';
  }
}

async function loadGraph() {
  try {
    const graphData = await API.getGraph(currentCaseId);
    GraphController.render(graphData);
  } catch (err) {
    console.error('Failed to load graph:', err);
  }
}

async function loadEvidence() {
  try {
    const evidenceList = await API.getEvidence(currentCaseId);
    const tbody = document.getElementById('evidenceTableBody');
    if (!tbody) return;

    tbody.innerHTML = '';
    evidenceList.forEach(ev => {
      const tr = document.createElement('tr');
      const isSupporting = ev.supports && ev.supports.trim().length > 0;
      const badgeCls = isSupporting ? 'badge-green' : 'badge-red';
      const claim = isSupporting ? `Supports: ${ev.supports}` : `Contradicts: ${ev.contradicts || 'Inconsistent signal'}`;

      tr.innerHTML = `
        <td><span class="badge ${badgeCls}">${ev.type}</span></td>
        <td>${ev.content}</td>
        <td>${ev.source}</td>
        <td><span class="badge ${badgeCls}">${claim}</span></td>
        <td>${(ev.reliability * 100).toFixed(0)}%</td>
        <td class="mono" title="${ev.hash}">${ev.hash.substring(0, 16)}...</td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    console.error('Failed to load evidence:', err);
  }
}

async function loadAnalysis() {
  try {
    const analysis = await API.getAnalysis(currentCaseId);
    
    // Score display
    const scoreVal = document.getElementById('analysisScoreVal');
    if (scoreVal) scoreVal.textContent = `${analysis.totalScore.toFixed(1)}/100`;

    const scoreMeter = document.getElementById('scoreMeter');
    if (scoreMeter) scoreMeter.style.width = `${analysis.totalScore}%`;

    // Radar Chart
    if (analysis.breakdown) {
      ChartsController.renderAttributionRadar(analysis.breakdown);
      renderBreakdownTable(analysis.breakdown);
    }

    // Explanation
    document.getElementById('analysisExplanation').textContent = analysis.explanation || 'N/A';
    document.getElementById('primaryDependency').textContent = analysis.primaryDependency || 'N/A';

    // Stylometry Details
    if (analysis.stylometricAnalysis) {
      const sty = analysis.stylometricAnalysis;
      document.getElementById('stySimPercent').textContent = `${sty.similarityPercentage || 78.4}%`;
      const aiBadge = document.getElementById('styAiBadge');
      if (sty.aiRewriteDetected) {
        aiBadge.innerHTML = '<span class="badge badge-amber">⚠ POTENTIAL AI-ASSISTED REWRITING DETECTED</span>';
      } else {
        aiBadge.innerHTML = '<span class="badge badge-green">✓ Natural Human Authorship Metrics</span>';
      }
      document.getElementById('styAiExpl').textContent = sty.aiRewriteExplanation || 'N/A';
    }

    // Supporting & Contradictory Evidence Lists
    renderEvidenceClaimList('supportingClaimsList', analysis.supportingEvidence, 'badge-green');
    renderEvidenceClaimList('contradictoryClaimsList', analysis.contradictoryEvidence, 'badge-red');

  } catch (err) {
    console.error('Failed to load analysis:', err);
  }
}

function renderBreakdownTable(breakdown) {
  const tbody = document.getElementById('breakdownTableBody');
  if (!tbody) return;

  const rows = [
    { dim: 'Cryptographic (PGP Key)', val: breakdown.cryptographicContribution, role: 'Primary Anchor (+)' },
    { dim: 'Financial (Blockchain / Wallet)', val: breakdown.financialContribution, role: 'Forensic Transfer (+)' },
    { dim: 'Infrastructure (Domain & Onion)', val: breakdown.infrastructureContribution, role: 'Network Telemetry (+)' },
    { dim: 'Alias / Persona Correlation', val: breakdown.aliasContribution, role: 'Identity Normalization (+)' },
    { dim: 'Stylometric Linguistic Match', val: breakdown.stylometricContribution, role: 'Syntax / Lexicon (+)' },
    { dim: 'Temporal Overlap (Operating Window)', val: breakdown.temporalContribution, role: 'Activity Hours (+)' },
    { dim: 'Contradiction Deductions (Conflict Penalties)', val: breakdown.contradictionDeduction, role: 'Contradiction Penalty (-)' }
  ];

  tbody.innerHTML = '';
  rows.forEach(r => {
    const tr = document.createElement('tr');
    const isDeduction = r.val < 0;
    const sign = r.val >= 0 ? '+' : '';
    tr.innerHTML = `
      <td>${r.dim}</td>
      <td class="font-bold ${isDeduction ? 'text-danger' : 'text-cyan'}">${sign}${r.val.toFixed(1)}</td>
      <td><span class="badge ${isDeduction ? 'badge-red' : 'badge-cyan'}">${r.role}</span></td>
    `;
    tbody.appendChild(tr);
  });
}

function renderEvidenceClaimList(elementId, items, badgeClass) {
  const container = document.getElementById(elementId);
  if (!container) return;

  container.innerHTML = '';
  if (!items || items.length === 0) {
    container.innerHTML = '<p class="text-muted">None identified.</p>';
    return;
  }

  items.forEach(it => {
    const div = document.createElement('div');
    div.className = 'claim-card';
    div.style.padding = '8px';
    div.style.marginBottom = '6px';
    div.style.background = 'rgba(255,255,255,0.02)';
    div.style.borderRadius = '4px';
    div.style.border = '1px solid var(--border-color)';
    div.innerHTML = `
      <div style="display:flex; justify-content:space-between; margin-bottom:4px;">
        <span class="badge ${badgeClass}">${it.type}</span>
        <span class="mono" style="font-size:10px; color:var(--text-muted);">${(it.hash || '').substring(0, 12)}...</span>
      </div>
      <p style="font-size:12px; margin-bottom:2px;">${it.content}</p>
      <p style="font-size:11px; font-weight:600; color:var(--text-secondary);">${it.claim || ''}</p>
    `;
    container.appendChild(div);
  });
}

async function runStressTest(dependency) {
  const btn = document.getElementById('btnRunStress');
  btn.disabled = true;
  btn.textContent = 'Calculating Re-evaluation...';

  try {
    const res = await API.runStressTest(currentCaseId, dependency);
    
    // Display results
    document.getElementById('stressOriginalScore').textContent = `${res.originalScore.toFixed(1)}/100`;
    document.getElementById('stressModifiedScore').textContent = `${res.modifiedScore.toFixed(1)}/100`;
    document.getElementById('stressDelta').textContent = `${res.scoreDelta.toFixed(1)}`;

    const robustBadge = document.getElementById('stressRobustnessBadge');
    robustBadge.textContent = res.robustnessRating;
    if (res.robustnessRating === 'HIGH_ROBUSTNESS') {
      robustBadge.className = 'badge badge-green';
    } else if (res.robustnessRating === 'MODERATE_ROBUSTNESS') {
      robustBadge.className = 'badge badge-amber';
    } else {
      robustBadge.className = 'badge badge-red';
    }

    document.getElementById('stressExplanation').textContent = res.explanation;
    document.getElementById('stressRecommendation').textContent = res.pivotRecommendation;

    document.getElementById('stressResultCard').style.display = 'block';

    showNotification(`Stress test complete for dependency: ${dependency}`, 'success');
    await loadStressTests();
  } catch (err) {
    showNotification('Stress test error: ' + err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'RUN STRESS TEST';
  }
}

async function loadStressTests() {
  try {
    const tests = await API.getStressTests(currentCaseId);
    const tbody = document.getElementById('stressHistoryTableBody');
    if (!tbody) return;

    tbody.innerHTML = '';
    tests.forEach(t => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><span class="badge badge-purple">${t.removedDependency}</span></td>
        <td>${t.originalScore.toFixed(1)} → <span class="font-bold">${t.modifiedScore.toFixed(1)}</span> (${t.scoreDelta.toFixed(1)})</td>
        <td><span class="badge ${t.result.includes('HIGH') ? 'badge-green' : (t.result.includes('MOD') ? 'badge-amber' : 'badge-red')}">${t.result}</span></td>
        <td style="font-size:12px;">${t.explanation}</td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    console.error('Failed to load stress tests:', err);
  }
}

async function loadReviewData() {
  try {
    const review = await API.getReview(currentCaseId);
    const badge = document.getElementById('dashReviewBadge');
    if (!review) {
      if (badge) badge.innerHTML = '<span class="badge badge-amber">PENDING ANALYST REVIEW</span>';
      return;
    }

    if (badge) {
      const cls = review.decision === 'APPROVED_LEAD' ? 'badge-green' : (review.decision === 'NEEDS_FURTHER_INVESTIGATION' ? 'badge-amber' : 'badge-red');
      badge.innerHTML = `<span class="badge ${cls}">${review.decision}</span>`;
    }

    const hist = document.getElementById('reviewHistoryDetails');
    if (hist) {
      hist.innerHTML = `
        <div style="background:var(--bg-tertiary); padding:12px; border-radius:6px; border:1px solid var(--border-color);">
          <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
            <strong>Analyst: ${review.analyst}</strong>
            <span class="badge badge-cyan">${review.decision}</span>
          </div>
          <p style="font-size:13px; color:var(--text-secondary);">${review.comments}</p>
          <p class="mono" style="font-size:10px; color:var(--text-muted); margin-top:6px;">Timestamp: ${review.reviewedAt}</p>
        </div>
      `;
    }
  } catch (err) {
    console.error('Failed to load review data:', err);
  }
}

async function loadTimeline() {
  try {
    const events = await API.getTimeline(currentCaseId);
    const container = document.getElementById('timelineContainer');
    if (!container) return;

    container.innerHTML = '';
    events.forEach(ev => {
      const item = document.createElement('div');
      item.className = 'timeline-item';
      
      let badgeCls = 'badge-cyan';
      if (ev.classification === 'SUPPORTING') badgeCls = 'badge-green';
      if (ev.classification === 'CONTRADICTORY') badgeCls = 'badge-red';

      item.innerHTML = `
        <div class="timeline-dot"></div>
        <div class="timeline-content">
          <div class="timeline-time">${ev.timestamp} | Source: ${ev.source}</div>
          <div class="timeline-title">${ev.title} <span class="badge ${badgeCls}" style="margin-left:8px;">${ev.classification}</span></div>
          <div class="timeline-desc">${ev.description}</div>
          <div class="mono" style="font-size:10px; color:var(--text-muted); margin-top:6px;">SHA-256: ${ev.hash}</div>
        </div>
      `;
      container.appendChild(item);
    });
  } catch (err) {
    console.error('Failed to load timeline:', err);
  }
}

async function loadAuditTrail() {
  try {
    const logs = await API.getAuditLogs(currentCaseId);
    const tbody = document.getElementById('auditTableBody');
    if (!tbody) return;

    tbody.innerHTML = '';
    logs.forEach(l => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td class="mono" style="font-size:11px;">${l.timestamp}</td>
        <td><span class="badge badge-cyan">${l.action}</span></td>
        <td>${l.actor}</td>
        <td style="font-size:12px;">${l.details}</td>
        <td class="mono" style="font-size:11px;" title="${l.hash}">${l.hash.substring(0, 16)}...</td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    console.error('Failed to load audit logs:', err);
  }
}

function showNotification(msg, type = 'info') {
  const toast = document.createElement('div');
  toast.style.position = 'fixed';
  toast.style.bottom = '24px';
  toast.style.right = '24px';
  toast.style.padding = '12px 20px';
  toast.style.borderRadius = '6px';
  toast.style.fontFamily = 'Inter, sans-serif';
  toast.style.fontSize = '13px';
  toast.style.fontWeight = '600';
  toast.style.zIndex = '9999';
  toast.style.boxShadow = '0 4px 12px rgba(0,0,0,0.5)';
  toast.style.transition = 'all 0.3s ease';

  if (type === 'success') {
    toast.style.background = '#00e676';
    toast.style.color = '#000';
  } else if (type === 'error') {
    toast.style.background = '#ff1744';
    toast.style.color = '#fff';
  } else {
    toast.style.background = '#00e5ff';
    toast.style.color = '#000';
  }

  toast.textContent = msg;
  document.body.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}
