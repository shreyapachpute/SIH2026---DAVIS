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


/* =========================================================
   NAVIGATION
   ========================================================= */

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
  document.querySelectorAll('.nav-item')
    .forEach(el => el.classList.remove('active'));

  document.querySelectorAll('.tab-pane')
    .forEach(el => el.classList.remove('active'));

  const navEl = document.querySelector(
    `.nav-item[data-tab="${tabId}"]`
  );

  const paneEl = document.getElementById(tabId);

  if (navEl) {
    navEl.classList.add('active');
  }

  if (paneEl) {
    paneEl.classList.add('active');
  }

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


/* =========================================================
   EVENT LISTENERS
   ========================================================= */

function initEventListeners() {

  /* -------------------------------------------------------
     Case select dropdown
     ------------------------------------------------------- */

  const caseSelect = document.getElementById('caseSelect');

  if (caseSelect) {
    caseSelect.addEventListener('change', async (e) => {

      const selectedId = parseInt(e.target.value, 10);

      if (Number.isNaN(selectedId)) {
        return;
      }

      currentCaseId = selectedId;

      await loadCurrentCaseData();
    });
  }


  /* -------------------------------------------------------
     Create Case Modal
     ------------------------------------------------------- */

  const btnCreateCase =
    document.getElementById('btnCreateCase');

  const createCaseModal =
    document.getElementById('createCaseModal');

  const btnCloseCreateCase =
    document.getElementById('btnCloseCreateCase');

  const btnCancelCreateCase =
    document.getElementById('btnCancelCreateCase');

  const formCreateCase =
    document.getElementById('formCreateCase');


  function openCreateCaseModal() {

    if (!createCaseModal) {
      return;
    }

    createCaseModal.classList.add('active');

    const titleInput =
      document.getElementById('newCaseTitle');

    if (titleInput) {
      setTimeout(() => {
        titleInput.focus();
      }, 50);
    }
  }


  function closeCreateCaseModal() {

    if (!createCaseModal) {
      return;
    }

    createCaseModal.classList.remove('active');

    if (formCreateCase) {
      formCreateCase.reset();
    }
  }


  // Open Create Case modal
  if (btnCreateCase) {

    btnCreateCase.addEventListener('click', () => {
      openCreateCaseModal();
    });

  }


  // Close using X button
  if (btnCloseCreateCase) {

    btnCloseCreateCase.addEventListener('click', () => {
      closeCreateCaseModal();
    });

  }


  // Close using Cancel button
  if (btnCancelCreateCase) {

    btnCancelCreateCase.addEventListener('click', () => {
      closeCreateCaseModal();
    });

  }


  // Close by clicking outside modal
  if (createCaseModal) {

    createCaseModal.addEventListener('click', (e) => {

      if (e.target === createCaseModal) {
        closeCreateCaseModal();
      }

    });

  }


  /* -------------------------------------------------------
     Create Case form submission
     ------------------------------------------------------- */

  if (formCreateCase) {

    formCreateCase.addEventListener('submit', async (e) => {

      e.preventDefault();

      const btnSubmit =
        document.getElementById('btnSubmitCreateCase');

      const caseNumberInput =
        document.getElementById('newCaseNumber');

      const titleInput =
        document.getElementById('newCaseTitle');

      const descriptionInput =
        document.getElementById('newCaseDescription');

      const statusInput =
        document.getElementById('newCaseStatus');


      const caseNumber =
        caseNumberInput
          ? caseNumberInput.value.trim()
          : '';

      const title =
        titleInput
          ? titleInput.value.trim()
          : '';

      const description =
        descriptionInput
          ? descriptionInput.value.trim()
          : '';

      const status =
        statusInput
          ? statusInput.value
          : 'ACTIVE';


      /* -------------------------------
         Validation
         ------------------------------- */

      if (!title) {

        showNotification(
          'Case title is required.',
          'error'
        );

        if (titleInput) {
          titleInput.focus();
        }

        return;
      }


      try {

        /* -------------------------------
           Loading state
           ------------------------------- */

        if (btnSubmit) {

          btnSubmit.disabled = true;
          btnSubmit.textContent = 'Creating...';

        }


        /* -------------------------------
           Request body
           ------------------------------- */

        const caseData = {
          caseNumber: caseNumber || null,
          title: title,
          description: description,
          status: status
        };


        /* -------------------------------
           Send to backend
           ------------------------------- */

        const createdCase =
          await API.createCase(caseData);


        /* -------------------------------
           Validate response
           ------------------------------- */

        if (
          !createdCase ||
          createdCase.id === undefined ||
          createdCase.id === null
        ) {

          throw new Error(
            'The server did not return a valid case ID.'
          );

        }


        /* -------------------------------
           Switch to new case
           ------------------------------- */

        currentCaseId =
          Number(createdCase.id);


        /* -------------------------------
           Refresh case dropdown
           ------------------------------- */

        await loadCases();


        /* -------------------------------
           Explicitly select new case
           ------------------------------- */

        const refreshedCaseSelect =
          document.getElementById('caseSelect');

        if (refreshedCaseSelect) {

          refreshedCaseSelect.value =
            String(currentCaseId);

        }


        /* -------------------------------
           Load new case data
           ------------------------------- */

        await loadCurrentCaseData();


        /* -------------------------------
           Close modal
           ------------------------------- */

        closeCreateCaseModal();


        /* -------------------------------
           Success notification
           ------------------------------- */

        const displayCaseNumber =
          createdCase.caseNumber ||
          caseNumber ||
          `Case #${currentCaseId}`;


        showNotification(
          `Case "${displayCaseNumber}" created successfully!`,
          'success'
        );

      } catch (err) {

        console.error(
          'Create Case error:',
          err
        );

        showNotification(
          'Failed to create case: ' + err.message,
          'error'
        );

      } finally {

        if (btnSubmit) {

          btnSubmit.disabled = false;
          btnSubmit.textContent = 'Create Case';

        }

      }

    });

  }


  /* -------------------------------------------------------
     Load Demo Case
     ------------------------------------------------------- */

  const btnLoadDemo =
    document.getElementById('btnLoadDemo');

  if (btnLoadDemo) {

    btnLoadDemo.addEventListener('click', async () => {

      try {

        btnLoadDemo.disabled = true;
        btnLoadDemo.textContent = 'Loading Demo...';

        const demoCase =
          await API.loadDemoCase();


        /*
         * If backend returns the demo case,
         * switch directly to it.
         */
        if (
          demoCase &&
          demoCase.id !== undefined &&
          demoCase.id !== null
        ) {

          currentCaseId =
            Number(demoCase.id);

        }


        await loadCases();
        await loadCurrentCaseData();


        showNotification(
          'Demo case loaded successfully!',
          'success'
        );

      } catch (err) {

        showNotification(
          'Failed to load demo case: ' + err.message,
          'error'
        );

      } finally {

        btnLoadDemo.disabled = false;
        btnLoadDemo.textContent = 'Load Demo Case';

      }

    });

  }


  /* -------------------------------------------------------
     Ingest Indicator Form
     ------------------------------------------------------- */

  const formIndicator =
    document.getElementById('formIndicator');

  if (formIndicator) {

    formIndicator.addEventListener(
      'submit',
      async (e) => {

        e.preventDefault();

        const type =
          document.getElementById('indType').value;

        const value =
          document
            .getElementById('indValue')
            .value
            .trim();

        const source =
          document
            .getElementById('indSource')
            .value
            .trim() ||
          'Manual Analyst Entry';


        if (!value) {
          return;
        }


        try {

          await API.addIndicator(
            currentCaseId,
            {
              type,
              value,
              source,
              confidence: 0.90
            }
          );


          document.getElementById(
            'indValue'
          ).value = '';


          showNotification(
            `Indicator ${type} added. Ready for collection.`,
            'success'
          );


          await loadIndicatorsList();

        } catch (err) {

          showNotification(
            'Failed to add indicator: ' +
            err.message,
            'error'
          );

        }

      }
    );

  }


  /* -------------------------------------------------------
     Start Controlled Investigation
     ------------------------------------------------------- */

  const btnStartInvestigation =
    document.getElementById(
      'btnStartInvestigation'
    );

  if (btnStartInvestigation) {

    btnStartInvestigation.addEventListener(
      'click',
      async () => {

        await runInvestigationPipeline();

      }
    );

  }


  /* -------------------------------------------------------
     Run Stress Test
     ------------------------------------------------------- */

  const formStressTest =
    document.getElementById('formStressTest');

  if (formStressTest) {

    formStressTest.addEventListener(
      'submit',
      async (e) => {

        e.preventDefault();

        const dep =
          document.getElementById(
            'stressDependency'
          ).value;

        await runStressTest(dep);

      }
    );

  }


  /* -------------------------------------------------------
     Analyst Review
     ------------------------------------------------------- */

  const formReview =
    document.getElementById('formReview');

  if (formReview) {

    formReview.addEventListener(
      'submit',
      async (e) => {

        e.preventDefault();

        const analyst =
          document
            .getElementById('reviewAnalyst')
            .value
            .trim();

        const decision =
          document
            .getElementById('reviewDecision')
            .value;

        const comments =
          document
            .getElementById('reviewComments')
            .value
            .trim();


        if (!analyst) {
          return;
        }


        try {

          await API.submitReview(
            currentCaseId,
            {
              analyst,
              decision,
              comments
            }
          );


          showNotification(
            'Analyst review submitted and anchored into audit trail.',
            'success'
          );


          await loadReviewData();

        } catch (err) {

          showNotification(
            'Failed to submit review: ' +
            err.message,
            'error'
          );

        }

      }
    );

  }


  /* -------------------------------------------------------
     SHA-256 Verifier
     ------------------------------------------------------- */

  const formVerify =
    document.getElementById('formVerify');

  if (formVerify) {

    formVerify.addEventListener(
      'submit',
      async (e) => {

        e.preventDefault();

        const data =
          document.getElementById(
            'verifyData'
          ).value;

        const hash =
          document
            .getElementById('verifyHash')
            .value
            .trim();


        try {

          const res =
            await API.verifyIntegrity(
              currentCaseId,
              data,
              hash
            );


          const resultEl =
            document.getElementById(
              'verifyResult'
            );


          if (!resultEl) {
            return;
          }


          if (res.match) {

            resultEl.innerHTML =
              `<span class="badge badge-green">
                STATUS: VALID (SHA-256 MATCH)
              </span>
              <br>
              <span class="mono">
                ${res.computedHash}
              </span>`;

          } else {

            resultEl.innerHTML =
              `<span class="badge badge-red">
                STATUS: TAMPERED / MODIFIED
              </span>
              <br>
              <span class="mono">
                Computed: ${res.computedHash}
              </span>`;

          }

        } catch (err) {

          showNotification(
            'Verification error: ' +
            err.message,
            'error'
          );

        }

      }
    );

  }


  /* -------------------------------------------------------
     Export Buttons
     ------------------------------------------------------- */

  const btnExportPdf =
    document.getElementById('btnExportPdf');

  if (btnExportPdf) {

    btnExportPdf.addEventListener(
      'click',
      () => {

        window.open(
          API.getPdfReportUrl(currentCaseId),
          '_blank'
        );

      }
    );

  }


  const btnExportCert =
    document.getElementById('btnExportCert');

  if (btnExportCert) {

    btnExportCert.addEventListener(
      'click',
      () => {

        window.open(
          API.getCertificateUrl(currentCaseId),
          '_blank'
        );

      }
    );

  }


  const btnExportCsv =
    document.getElementById('btnExportCsv');

  if (btnExportCsv) {

    btnExportCsv.addEventListener(
      'click',
      () => {

        window.open(
          API.getCsvExportUrl(currentCaseId),
          '_blank'
        );

      }
    );

  }


  const btnExportJson =
    document.getElementById('btnExportJson');

  if (btnExportJson) {

    btnExportJson.addEventListener(
      'click',
      () => {

        window.open(
          API.getJsonExportUrl(currentCaseId),
          '_blank'
        );

      }
    );

  }

}


/* =========================================================
   CASE MANAGEMENT
   ========================================================= */

async function loadCases() {

  try {

    const cases =
      await API.getCases();

    const select =
      document.getElementById('caseSelect');

    if (!select) {
      return;
    }


    select.innerHTML = '';


    cases.forEach(c => {

      const opt =
        document.createElement('option');

      opt.value = c.id;


      const title =
        c.title || 'Untitled Case';

      const shortTitle =
        title.length > 30
          ? `${title.substring(0, 30)}...`
          : title;


      opt.textContent =
        `${c.caseNumber} - ${shortTitle}`;


      select.appendChild(opt);

    });


    if (cases.length > 0) {

      /*
       * Keep current case if it still exists.
       * Otherwise select the first available case.
       */
      if (!cases.some(
        c => Number(c.id) === Number(currentCaseId)
      )) {

        currentCaseId =
          Number(cases[0].id);

      }


      select.value =
        String(currentCaseId);

    }

  } catch (err) {

    console.error(
      'Failed to load cases:',
      err
    );

  }

}


async function loadCurrentCaseData() {

  await loadDashboardMetrics();
  await loadIndicatorsList();
  await loadReviewData();

}


/* =========================================================
   DASHBOARD
   ========================================================= */

async function loadDashboardMetrics() {

  try {

    const caseDetails =
      await API.getCase(currentCaseId);

    const indicators =
      await API.getIndicators(currentCaseId);

    const entities =
      await API.getEntities(currentCaseId);

    const evidence =
      await API.getEvidence(currentCaseId);


    /* -------------------------------
       Basic case information
       ------------------------------- */

    const dashCaseNum =
      document.getElementById('dashCaseNum');

    const dashCaseTitle =
      document.getElementById('dashCaseTitle');

    const dashStatus =
      document.getElementById('dashStatus');


    if (dashCaseNum) {
      dashCaseNum.textContent =
        caseDetails.caseNumber || 'N/A';
    }

    if (dashCaseTitle) {
      dashCaseTitle.textContent =
        caseDetails.title || 'N/A';
    }

    if (dashStatus) {
      dashStatus.textContent =
        caseDetails.status || 'ACTIVE';
    }


    /* -------------------------------
       Counts
       ------------------------------- */

    const indicatorsEl =
      document.getElementById('m-indicators');

    const entitiesEl =
      document.getElementById('m-entities');

    const evidenceEl =
      document.getElementById('m-evidence');


    if (indicatorsEl) {
      indicatorsEl.textContent =
        indicators.length;
    }

    if (entitiesEl) {
      entitiesEl.textContent =
        entities.length;
    }

    if (evidenceEl) {
      evidenceEl.textContent =
        evidence.length;
    }


    /* -------------------------------
       Analysis
       ------------------------------- */

    const scoreEl =
      document.getElementById('m-score');

    const scoreCard =
      document.getElementById('m-score-card');


    /*
     * Always reset the score before checking
     * the currently selected case.
     *
     * This is important when switching from
     * an analyzed case to a fresh case.
     */
    if (scoreEl) {
      scoreEl.textContent = 'N/A';
    }

    if (scoreCard) {
      scoreCard.style.borderColor = '';
    }


    try {

      const analysis =
        await API.getAnalysis(currentCaseId);


      /*
       * A fresh case has no analysis yet.
       *
       * API.getAnalysis() returns null when
       * backend responds with HTTP 204.
       */
      if (
        analysis &&
        analysis.totalScore !== undefined &&
        analysis.totalScore !== null
      ) {

        const numericScore =
          Number(analysis.totalScore);


        if (scoreEl) {

          scoreEl.textContent =
            `${numericScore.toFixed(1)}/100`;

        }


        if (scoreCard) {

          if (numericScore >= 70) {

            scoreCard.style.borderColor =
              'rgba(0, 230, 118, 0.4)';

          } else if (numericScore >= 45) {

            scoreCard.style.borderColor =
              'rgba(255, 179, 0, 0.4)';

          } else {

            scoreCard.style.borderColor =
              'rgba(255, 23, 68, 0.4)';

          }

        }

      } else {

        /*
         * No saved analysis.
         * Keep dashboard explicitly at N/A.
         */
        if (scoreEl) {
          scoreEl.textContent = 'N/A';
        }

        if (scoreCard) {
          scoreCard.style.borderColor = '';
        }

      }

    } catch (analysisError) {

      /*
       * Analysis lookup failing should not
       * break the entire dashboard.
       */
      console.warn(
        'Dashboard analysis load notice:',
        analysisError.message
      );

      if (scoreEl) {
        scoreEl.textContent = 'N/A';
      }

      if (scoreCard) {
        scoreCard.style.borderColor = '';
      }

    }

  } catch (err) {

    console.error(
      'Failed to load dashboard metrics:',
      err
    );

  }

}


/* =========================================================
   INDICATORS
   ========================================================= */

async function loadIndicatorsList() {

  try {

    const indicators =
      await API.getIndicators(currentCaseId);

    const tbody =
      document.getElementById(
        'indicatorsTableBody'
      );

    if (!tbody) {
      return;
    }


    tbody.innerHTML = '';


    if (indicators.length === 0) {

      tbody.innerHTML =
        '<tr><td colspan="4" class="text-muted">' +
        'No indicators ingested for this case yet.' +
        '</td></tr>';

      return;
    }


    indicators.forEach(ind => {

      const tr =
        document.createElement('tr');

      tr.innerHTML = `
        <td>
          <span class="badge badge-cyan">
            ${ind.type}
          </span>
        </td>

        <td class="mono font-bold">
          ${ind.value}
        </td>

        <td>
          ${ind.source || 'N/A'}
        </td>

        <td>
          ${(ind.confidence * 100).toFixed(0)}%
        </td>
      `;

      tbody.appendChild(tr);

    });

  } catch (err) {

    console.error(
      'Error loading indicators:',
      err
    );

  }

}


/* =========================================================
   INVESTIGATION PIPELINE
   ========================================================= */

async function runInvestigationPipeline() {

  const btn =
    document.getElementById(
      'btnStartInvestigation'
    );

  const logsEl =
    document.getElementById(
      'pipelineLogs'
    );


  if (!btn || !logsEl) {
    return;
  }


  btn.disabled = true;

  btn.innerHTML =
    '<span class="step-status active"></span> ' +
    'Running OPSEC-gated collection...';


  const steps = [

    {
      id: 'step-opsec',
      text:
        'OPSEC Gating: Verifying simulated sandboxed environment...'
    },

    {
      id: 'step-crawl',
      text:
        'Simulated Collection: Querying darknet mirror indices...'
    },

    {
      id: 'step-extract',
      text:
        'Entity Extraction: Extracting PGP, wallets, aliases...'
    },

    {
      id: 'step-correlate',
      text:
        'Graph Correlation: Correlating multi-vector telemetry...'
    },

    {
      id: 'step-score',
      text:
        'Attribution Engine: Calculating dynamic support score...'
    }

  ];


  logsEl.innerHTML = '';


  for (const s of steps) {

    const p =
      document.createElement('p');

    p.className = 'mono';

    p.textContent =
      `> ${s.text}`;

    logsEl.appendChild(p);

    await new Promise(
      r => setTimeout(r, 350)
    );

  }


  try {

    /*
     * Collection creates the controlled
     * synthetic intelligence network.
     */
    const result =
      await API.runCollection(
        currentCaseId
      );


    /*
     * Analysis is deliberately executed
     * ONLY here after explicit user action.
     */
    const analysis =
      await API.runAnalysis(
        currentCaseId
      );


    const doneP =
      document.createElement('p');

    doneP.className = 'mono';

    doneP.style.color =
      '#00e676';

    doneP.textContent =
      `✓ [COMPLETE] Processed ` +
      `${result.indicatorsProcessed} indicators. ` +
      `Discovered ${result.entitiesDiscovered} entities. ` +
      `Attribution Score: ${analysis.totalScore}/100.`;


    logsEl.appendChild(doneP);


    showNotification(
      'Controlled collection & correlation completed.',
      'success'
    );


    await loadCurrentCaseData();

  } catch (err) {

    showNotification(
      'Collection error: ' +
      err.message,
      'error'
    );

  } finally {

    btn.disabled = false;

    btn.textContent =
      'START CONTROLLED INVESTIGATION';

  }

}


/* =========================================================
   GRAPH
   ========================================================= */

async function loadGraph() {

  try {

    const graphData =
      await API.getGraph(currentCaseId);

    GraphController.render(
      graphData
    );

  } catch (err) {

    console.error(
      'Failed to load graph:',
      err
    );

  }

}


/* =========================================================
   EVIDENCE
   ========================================================= */

async function loadEvidence() {

  try {

    const evidenceList =
      await API.getEvidence(
        currentCaseId
      );

    const tbody =
      document.getElementById(
        'evidenceTableBody'
      );

    if (!tbody) {
      return;
    }


    tbody.innerHTML = '';


    if (evidenceList.length === 0) {

      tbody.innerHTML =
        '<tr><td colspan="6" class="text-muted">' +
        'No evidence available for this case yet.' +
        '</td></tr>';

      return;
    }


    evidenceList.forEach(ev => {

      const tr =
        document.createElement('tr');

      const isSupporting =
        ev.supports &&
        ev.supports.trim().length > 0;


      const badgeCls =
        isSupporting
          ? 'badge-green'
          : 'badge-red';


      const claim =
        isSupporting
          ? `Supports: ${ev.supports}`
          : `Contradicts: ${
              ev.contradicts ||
              'Inconsistent signal'
            }`;


      tr.innerHTML = `
        <td>
          <span class="badge ${badgeCls}">
            ${ev.type}
          </span>
        </td>

        <td>
          ${ev.content}
        </td>

        <td>
          ${ev.source}
        </td>

        <td>
          <span class="badge ${badgeCls}">
            ${claim}
          </span>
        </td>

        <td>
          ${(ev.reliability * 100).toFixed(0)}%
        </td>

        <td
          class="mono"
          title="${ev.hash}"
        >
          ${ev.hash ? ev.hash.substring(0, 16) + '...' : 'N/A'}
        </td>
      `;

      tbody.appendChild(tr);

    });

  } catch (err) {

    console.error(
      'Failed to load evidence:',
      err
    );

  }

}


/* =========================================================
   ANALYSIS
   ========================================================= */

async function loadAnalysis() {

  try {

    const analysis =
      await API.getAnalysis(
        currentCaseId
      );


    /*
     * Fresh case / no analysis yet.
     *
     * API.getAnalysis() returns null when
     * backend responds with HTTP 204.
     */
    if (!analysis) {

      resetAnalysisView();

      return;
    }


    /* -----------------------------------------------------
       Score
       ----------------------------------------------------- */

    const scoreVal =
      document.getElementById(
        'analysisScoreVal'
      );

    const totalScore =
      Number(analysis.totalScore);


    if (scoreVal) {

      if (Number.isFinite(totalScore)) {

        scoreVal.textContent =
          `${totalScore.toFixed(1)}/100`;

      } else {

        scoreVal.textContent =
          'N/A';

      }

    }


    /* -----------------------------------------------------
       Score Meter
       ----------------------------------------------------- */

    const scoreMeter =
      document.getElementById(
        'scoreMeter'
      );

    if (scoreMeter) {

      const meterValue =
        Number.isFinite(totalScore)
          ? Math.max(0, Math.min(100, totalScore))
          : 0;

      scoreMeter.style.width =
        `${meterValue}%`;

    }


    /* -----------------------------------------------------
       Radar Chart / Breakdown
       ----------------------------------------------------- */

    if (
      analysis.breakdown &&
      typeof analysis.breakdown === 'object'
    ) {

      if (
        typeof ChartsController !== 'undefined' &&
        ChartsController.renderAttributionRadar
      ) {

        ChartsController.renderAttributionRadar(
          analysis.breakdown
        );

      }


      renderBreakdownTable(
        analysis.breakdown
      );

    } else {

      clearBreakdownView();

    }


    /* -----------------------------------------------------
       Explanation
       ----------------------------------------------------- */

    const analysisExplanation =
      document.getElementById(
        'analysisExplanation'
      );

    if (analysisExplanation) {

      analysisExplanation.textContent =
        analysis.explanation ||
        'Analysis completed. Detailed explanation is not available in the stored result.';

    }


    /* -----------------------------------------------------
       Primary Dependency
       ----------------------------------------------------- */

    const primaryDependency =
      document.getElementById(
        'primaryDependency'
      );

    if (primaryDependency) {

      primaryDependency.textContent =
        analysis.primaryDependency || 'N/A';

    }


    /* -----------------------------------------------------
       Stylometry
       ----------------------------------------------------- */

    if (analysis.stylometricAnalysis) {

      const sty =
        analysis.stylometricAnalysis;


      const stySimPercent =
        document.getElementById(
          'stySimPercent'
        );

      if (stySimPercent) {

        stySimPercent.textContent =
          `${sty.similarityPercentage ?? 'N/A'}%`;

      }


      const aiBadge =
        document.getElementById(
          'styAiBadge'
        );


      if (aiBadge) {

        if (sty.aiRewriteDetected) {

          aiBadge.innerHTML =
            '<span class="badge badge-amber">' +
            '⚠ POTENTIAL AI-ASSISTED REWRITING DETECTED' +
            '</span>';

        } else {

          aiBadge.innerHTML =
            '<span class="badge badge-green">' +
            '✓ Natural Human Authorship Metrics' +
            '</span>';

        }

      }


      const styAiExpl =
        document.getElementById(
          'styAiExpl'
        );

      if (styAiExpl) {

        styAiExpl.textContent =
          sty.aiRewriteExplanation || 'N/A';

      }

    } else {

      resetStylometryView();

    }


    /* -----------------------------------------------------
       Evidence
       ----------------------------------------------------- */

    renderEvidenceClaimList(
      'supportingClaimsList',
      analysis.supportingEvidence,
      'badge-green'
    );

    renderEvidenceClaimList(
      'contradictoryClaimsList',
      analysis.contradictoryEvidence,
      'badge-red'
    );


  } catch (err) {

    console.error(
      'Failed to load analysis:',
      err
    );

    /*
     * Do not leave stale analysis visible
     * if the request fails.
     */
    resetAnalysisView();

  }

}


/* =========================================================
   ANALYSIS RESET
   ========================================================= */

function resetAnalysisView() {

  /* Score */

  const scoreVal =
    document.getElementById(
      'analysisScoreVal'
    );

  if (scoreVal) {
    scoreVal.textContent = 'N/A';
  }


  /* Meter */

  const scoreMeter =
    document.getElementById(
      'scoreMeter'
    );

  if (scoreMeter) {
    scoreMeter.style.width = '0%';
  }


  /* Explanation */

  const analysisExplanation =
    document.getElementById(
      'analysisExplanation'
    );

  if (analysisExplanation) {

    analysisExplanation.textContent =
      'No attribution analysis has been executed for this case yet. Run the Controlled Investigation to calculate the attribution score.';

  }


  /* Primary dependency */

  const primaryDependency =
    document.getElementById(
      'primaryDependency'
    );

  if (primaryDependency) {
    primaryDependency.textContent = 'N/A';
  }


  /* Breakdown */

  clearBreakdownView();


  /* Evidence lists */

  renderEvidenceClaimList(
    'supportingClaimsList',
    [],
    'badge-green'
  );

  renderEvidenceClaimList(
    'contradictoryClaimsList',
    [],
    'badge-red'
  );


  /* Stylometry */

  resetStylometryView();

}


function clearBreakdownView() {

  const tbody =
    document.getElementById(
      'breakdownTableBody'
    );

  if (tbody) {

    tbody.innerHTML =
      `<tr>
        <td colspan="3" class="text-muted">
          No attribution analysis available yet.
        </td>
      </tr>`;

  }

}


function resetStylometryView() {

  const stySimPercent =
    document.getElementById(
      'stySimPercent'
    );

  if (stySimPercent) {
    stySimPercent.textContent = 'N/A';
  }


  const aiBadge =
    document.getElementById(
      'styAiBadge'
    );

  if (aiBadge) {

    aiBadge.innerHTML =
      '<span class="badge badge-amber">' +
      'ANALYSIS NOT RUN' +
      '</span>';

  }


  const styAiExpl =
    document.getElementById(
      'styAiExpl'
    );

  if (styAiExpl) {

    styAiExpl.textContent =
      'Stylometric analysis will appear after the controlled investigation is executed.';

  }

}


/* =========================================================
   BREAKDOWN TABLE
   ========================================================= */

function renderBreakdownTable(breakdown) {

  const tbody =
    document.getElementById(
      'breakdownTableBody'
    );

  if (!tbody) {
    return;
  }


  const rows = [

    {
      dim: 'Cryptographic (PGP Key)',
      val: Number(breakdown.cryptographicContribution ?? 0),
      role: 'Primary Anchor (+)'
    },

    {
      dim: 'Financial (Blockchain / Wallet)',
      val: Number(breakdown.financialContribution ?? 0),
      role: 'Forensic Transfer (+)'
    },

    {
      dim: 'Infrastructure (Domain & Onion)',
      val: Number(breakdown.infrastructureContribution ?? 0),
      role: 'Network Telemetry (+)'
    },

    {
      dim: 'Alias / Persona Correlation',
      val: Number(breakdown.aliasContribution ?? 0),
      role: 'Identity Normalization (+)'
    },

    {
      dim: 'Stylometric Linguistic Match',
      val: Number(breakdown.stylometricContribution ?? 0),
      role: 'Syntax / Lexicon (+)'
    },

    {
      dim: 'Temporal Overlap (Operating Window)',
      val: Number(breakdown.temporalContribution ?? 0),
      role: 'Activity Hours (+)'
    },

    {
      dim: 'Contradiction Deductions (Conflict Penalties)',
      val: Number(breakdown.contradictionDeduction ?? 0),
      role: 'Contradiction Penalty (-)'
    }

  ];


  tbody.innerHTML = '';


  rows.forEach(r => {

    const tr =
      document.createElement('tr');

    const isDeduction =
      r.val < 0;

    const sign =
      r.val >= 0
        ? '+'
        : '';


    tr.innerHTML = `
      <td>
        ${r.dim}
      </td>

      <td class="font-bold ${
        isDeduction
          ? 'text-danger'
          : 'text-cyan'
      }">
        ${sign}${r.val.toFixed(1)}
      </td>

      <td>
        <span class="badge ${
          isDeduction
            ? 'badge-red'
            : 'badge-cyan'
        }">
          ${r.role}
        </span>
      </td>
    `;


    tbody.appendChild(tr);

  });

}


/* =========================================================
   EVIDENCE CLAIM LIST
   ========================================================= */

function renderEvidenceClaimList(
  elementId,
  items,
  badgeClass
) {

  const container =
    document.getElementById(
      elementId
    );

  if (!container) {
    return;
  }


  container.innerHTML = '';


  if (!items || items.length === 0) {

    container.innerHTML =
      '<p class="text-muted">None identified.</p>';

    return;
  }


  items.forEach(it => {

    const div =
      document.createElement('div');

    div.className =
      'claim-card';

    div.style.padding =
      '8px';

    div.style.marginBottom =
      '6px';

    div.style.background =
      'rgba(255,255,255,0.02)';

    div.style.borderRadius =
      '4px';

    div.style.border =
      '1px solid var(--border-color)';


    div.innerHTML = `
      <div style="
        display:flex;
        justify-content:space-between;
        margin-bottom:4px;
      ">

        <span class="badge ${badgeClass}">
          ${it.type || 'EVIDENCE'}
        </span>

        <span
          class="mono"
          style="
            font-size:10px;
            color:var(--text-muted);
          "
        >
          ${(it.hash || '').substring(0, 12)}...
        </span>

      </div>

      <p style="
        font-size:12px;
        margin-bottom:2px;
      ">
        ${it.content || ''}
      </p>

      <p style="
        font-size:11px;
        font-weight:600;
        color:var(--text-secondary);
      ">
        ${it.claim || ''}
      </p>
    `;


    container.appendChild(div);

  });

}


/* =========================================================
   STRESS TEST
   ========================================================= */

async function runStressTest(dependency) {

  const btn =
    document.getElementById(
      'btnRunStress'
    );

  if (!btn) {
    return;
  }


  btn.disabled = true;

  btn.textContent =
    'Calculating Re-evaluation...';


  try {

    const res =
      await API.runStressTest(
        currentCaseId,
        dependency
      );


    const originalScore =
      Number(res.originalScore);

    const modifiedScore =
      Number(res.modifiedScore);

    const scoreDelta =
      Number(res.scoreDelta);


    const originalEl =
      document.getElementById(
        'stressOriginalScore'
      );

    const modifiedEl =
      document.getElementById(
        'stressModifiedScore'
      );

    const deltaEl =
      document.getElementById(
        'stressDelta'
      );


    if (originalEl) {

      originalEl.textContent =
        `${originalScore.toFixed(1)}/100`;

    }


    if (modifiedEl) {

      modifiedEl.textContent =
        `${modifiedScore.toFixed(1)}/100`;

    }


    if (deltaEl) {

      deltaEl.textContent =
        `${scoreDelta.toFixed(1)}`;

    }


    const robustBadge =
      document.getElementById(
        'stressRobustnessBadge'
      );


    if (robustBadge) {

      robustBadge.textContent =
        res.robustnessRating;


      if (
        res.robustnessRating ===
        'HIGH_ROBUSTNESS'
      ) {

        robustBadge.className =
          'badge badge-green';

      } else if (
        res.robustnessRating ===
        'MODERATE_ROBUSTNESS'
      ) {

        robustBadge.className =
          'badge badge-amber';

      } else {

        robustBadge.className =
          'badge badge-red';

      }

    }


    const stressExplanation =
      document.getElementById(
        'stressExplanation'
      );

    if (stressExplanation) {

      stressExplanation.textContent =
        res.explanation || 'N/A';

    }


    const stressRecommendation =
      document.getElementById(
        'stressRecommendation'
      );

    if (stressRecommendation) {

      stressRecommendation.textContent =
        res.pivotRecommendation || 'N/A';

    }


    const stressResultCard =
      document.getElementById(
        'stressResultCard'
      );

    if (stressResultCard) {

      stressResultCard.style.display =
        'block';

    }


    showNotification(
      `Stress test complete for dependency: ${dependency}`,
      'success'
    );


    await loadStressTests();


  } catch (err) {

    showNotification(
      'Stress test error: ' +
      err.message,
      'error'
    );

  } finally {

    btn.disabled = false;

    btn.textContent =
      'RUN STRESS TEST';

  }

}


/* =========================================================
   STRESS TEST HISTORY
   ========================================================= */

async function loadStressTests() {

  try {

    const tests =
      await API.getStressTests(
        currentCaseId
      );

    const tbody =
      document.getElementById(
        'stressHistoryTableBody'
      );

    if (!tbody) {
      return;
    }


    tbody.innerHTML = '';


    if (!tests || tests.length === 0) {

      tbody.innerHTML =
        '<tr><td colspan="4" class="text-muted">' +
        'No stress tests have been executed for this case yet.' +
        '</td></tr>';

      return;
    }


    tests.forEach(t => {

      const tr =
        document.createElement('tr');


      const originalScore =
        Number(t.originalScore);

      const modifiedScore =
        Number(t.modifiedScore);

      const scoreDelta =
        Number(t.scoreDelta);


      tr.innerHTML = `
        <td>
          <span class="badge badge-purple">
            ${t.removedDependency}
          </span>
        </td>

        <td>
          ${originalScore.toFixed(1)}
          →
          <span class="font-bold">
            ${modifiedScore.toFixed(1)}
          </span>
          (${scoreDelta.toFixed(1)})
        </td>

        <td>
          <span class="badge ${
            t.result && t.result.includes('HIGH')
              ? 'badge-green'
              : (
                t.result && t.result.includes('MOD')
                  ? 'badge-amber'
                  : 'badge-red'
              )
          }">
            ${t.result || 'N/A'}
          </span>
        </td>

        <td style="font-size:12px;">
          ${t.explanation || 'N/A'}
        </td>
      `;


      tbody.appendChild(tr);

    });

  } catch (err) {

    console.error(
      'Failed to load stress tests:',
      err
    );

  }

}


/* =========================================================
   REVIEW
   ========================================================= */

async function loadReviewData() {

  try {

    const review =
      await API.getReview(
        currentCaseId
      );


    const badge =
      document.getElementById(
        'dashReviewBadge'
      );


    if (!review) {

      if (badge) {

        badge.innerHTML =
          '<span class="badge badge-amber">' +
          'PENDING ANALYST REVIEW' +
          '</span>';

      }

      const hist =
        document.getElementById(
          'reviewHistoryDetails'
        );

      if (hist) {
        hist.innerHTML = '';
      }

      return;
    }


    if (badge) {

      const cls =
        review.decision === 'APPROVED_LEAD'
          ? 'badge-green'
          : (
            review.decision ===
            'NEEDS_FURTHER_INVESTIGATION'
              ? 'badge-amber'
              : 'badge-red'
          );


      badge.innerHTML =
        `<span class="badge ${cls}">
          ${review.decision}
        </span>`;

    }


    const hist =
      document.getElementById(
        'reviewHistoryDetails'
      );


    if (hist) {

      hist.innerHTML = `

        <div style="
          background:var(--bg-tertiary);
          padding:12px;
          border-radius:6px;
          border:1px solid var(--border-color);
        ">

          <div style="
            display:flex;
            justify-content:space-between;
            margin-bottom:6px;
          ">

            <strong>
              Analyst: ${review.analyst}
            </strong>

            <span class="badge badge-cyan">
              ${review.decision}
            </span>

          </div>

          <p style="
            font-size:13px;
            color:var(--text-secondary);
          ">
            ${review.comments || ''}
          </p>

          <p class="mono" style="
            font-size:10px;
            color:var(--text-muted);
            margin-top:6px;
          ">
            Timestamp: ${review.reviewedAt}
          </p>

        </div>

      `;

    }

  } catch (err) {

    console.error(
      'Failed to load review data:',
      err
    );

  }

}


/* =========================================================
   TIMELINE
   ========================================================= */

async function loadTimeline() {

  try {

    const events =
      await API.getTimeline(
        currentCaseId
      );


    const container =
      document.getElementById(
        'timelineContainer'
      );


    if (!container) {
      return;
    }


    container.innerHTML = '';


    if (!events || events.length === 0) {

      container.innerHTML =
        '<p class="text-muted">No timeline events available for this case yet.</p>';

      return;
    }


    events.forEach(ev => {

      const item =
        document.createElement('div');

      item.className =
        'timeline-item';


      let badgeCls =
        'badge-cyan';


      if (
        ev.classification ===
        'SUPPORTING'
      ) {

        badgeCls =
          'badge-green';

      }


      if (
        ev.classification ===
        'CONTRADICTORY'
      ) {

        badgeCls =
          'badge-red';

      }


      item.innerHTML = `

        <div class="timeline-dot"></div>

        <div class="timeline-content">

          <div class="timeline-time">
            ${ev.timestamp}
            |
            Source: ${ev.source}
          </div>

          <div class="timeline-title">

            ${ev.title}

            <span
              class="badge ${badgeCls}"
              style="margin-left:8px;"
            >
              ${ev.classification}
            </span>

          </div>

          <div class="timeline-desc">
            ${ev.description}
          </div>

          <div
            class="mono"
            style="
              font-size:10px;
              color:var(--text-muted);
              margin-top:6px;
            "
          >
            SHA-256: ${ev.hash}
          </div>

        </div>

      `;


      container.appendChild(item);

    });

  } catch (err) {

    console.error(
      'Failed to load timeline:',
      err
    );

  }

}


/* =========================================================
   AUDIT TRAIL
   ========================================================= */

async function loadAuditTrail() {

  try {

    const logs =
      await API.getAuditLogs(
        currentCaseId
      );


    const tbody =
      document.getElementById(
        'auditTableBody'
      );


    if (!tbody) {
      return;
    }


    tbody.innerHTML = '';


    if (!logs || logs.length === 0) {

      tbody.innerHTML =
        '<tr><td colspan="5" class="text-muted">' +
        'No audit activity recorded for this case yet.' +
        '</td></tr>';

      return;
    }


    logs.forEach(l => {

      const tr =
        document.createElement('tr');


      tr.innerHTML = `

        <td
          class="mono"
          style="font-size:11px;"
        >
          ${l.timestamp}
        </td>

        <td>
          <span class="badge badge-cyan">
            ${l.action}
          </span>
        </td>

        <td>
          ${l.actor}
        </td>

        <td style="font-size:12px;">
          ${l.details}
        </td>

        <td
          class="mono"
          style="font-size:11px;"
          title="${l.hash}"
        >
          ${l.hash ? l.hash.substring(0, 16) + '...' : 'N/A'}
        </td>

      `;


      tbody.appendChild(tr);

    });

  } catch (err) {

    console.error(
      'Failed to load audit logs:',
      err
    );

  }

}


/* =========================================================
   NOTIFICATIONS
   ========================================================= */

function showNotification(
  msg,
  type = 'info'
) {

  const toast =
    document.createElement('div');


  toast.style.position =
    'fixed';

  toast.style.bottom =
    '24px';

  toast.style.right =
    '24px';

  toast.style.padding =
    '12px 20px';

  toast.style.borderRadius =
    '6px';

  toast.style.fontFamily =
    'Inter, sans-serif';

  toast.style.fontSize =
    '13px';

  toast.style.fontWeight =
    '600';

  toast.style.zIndex =
    '9999';

  toast.style.boxShadow =
    '0 4px 12px rgba(0,0,0,0.5)';

  toast.style.transition =
    'all 0.3s ease';


  if (type === 'success') {

    toast.style.background =
      '#00e676';

    toast.style.color =
      '#000';

  } else if (type === 'error') {

    toast.style.background =
      '#ff1744';

    toast.style.color =
      '#fff';

  } else {

    toast.style.background =
      '#00e5ff';

    toast.style.color =
      '#000';

  }


  toast.textContent =
    msg;


  document.body.appendChild(
    toast
  );


  setTimeout(() => {

    toast.style.opacity =
      '0';


    setTimeout(
      () => toast.remove(),
      300
    );

  }, 3500);

}