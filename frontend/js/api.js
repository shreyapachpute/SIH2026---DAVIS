/**
 * DAVIS - REST API Client
 * Digital Attribution & Verification Intelligence System
 */

const API_BASE = window.location.origin.includes(':3000')
  ? 'http://localhost:8080/api'
  : '/api';

const API = {

  async getHealth() {
    const res = await fetch(`${API_BASE}/health`);

    if (!res.ok) {
      throw new Error(`Health check failed (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getCases() {
    const res = await fetch(`${API_BASE}/cases`);

    if (!res.ok) {
      throw new Error(`Failed to load cases (HTTP ${res.status})`);
    }

    return res.json();
  },

  async createCase(caseData) {
    const res = await fetch(`${API_BASE}/cases`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(caseData)
    });

    if (!res.ok) {
      let message = `Failed to create case (HTTP ${res.status})`;

      try {
        const errorData = await res.json();
        message =
          errorData.message ||
          errorData.error ||
          message;
      } catch (_) {
        // Keep default message if response is not JSON.
      }

      throw new Error(message);
    }

    return res.json();
  },

  async loadDemoCase() {
    const res = await fetch(`${API_BASE}/cases/demo`, {
      method: 'POST'
    });

    if (!res.ok) {
      throw new Error(`Failed to load demo case (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getCase(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}`);

    if (!res.ok) {
      throw new Error(`Failed to load case (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getIndicators(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/indicators`);

    if (!res.ok) {
      throw new Error(`Failed to load indicators (HTTP ${res.status})`);
    }

    return res.json();
  },

  async addIndicator(caseId, indicator) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/indicators`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(indicator)
    });

    if (!res.ok) {
      throw new Error(`Failed to add indicator (HTTP ${res.status})`);
    }

    return res.json();
  },

  async runCollection(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/collect`, {
      method: 'POST'
    });

    if (!res.ok) {
      throw new Error(`Collection failed (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getGraph(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/graph`);

    if (!res.ok) {
      throw new Error(`Failed to load graph (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getEntities(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/entities`);

    if (!res.ok) {
      throw new Error(`Failed to load entities (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getEvidence(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/evidence`);

    if (!res.ok) {
      throw new Error(`Failed to load evidence (HTTP ${res.status})`);
    }

    return res.json();
  },

  async addEvidence(caseId, evidenceData) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/evidence`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(evidenceData)
    });

    if (!res.ok) {
      throw new Error(`Failed to add evidence (HTTP ${res.status})`);
    }

    return res.json();
  },

  async runAnalysis(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/analyze`, {
      method: 'POST'
    });

    if (!res.ok) {
      throw new Error(`Analysis failed (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getAnalysis(caseId) {
  const res = await fetch(`${API_BASE}/cases/${caseId}/analysis`);

  if (res.status === 204) {
    return null;
  }

  if (!res.ok) {
    let message = `Failed to get analysis (HTTP ${res.status})`;

    try {
      const errorData = await res.json();
      message = errorData.message || errorData.error || message;
    } catch (_) {}

    throw new Error(message);
  }

  return res.json();
},

  async runStressTest(caseId, dependency) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/stress-test`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        removedDependency: dependency
      })
    });

    if (!res.ok) {
      throw new Error(`Stress test failed (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getStressTests(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/stress-test`);

    if (!res.ok) {
      throw new Error(`Failed to load stress tests (HTTP ${res.status})`);
    }

    return res.json();
  },

  async submitReview(caseId, reviewData) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/review`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(reviewData)
    });

    if (!res.ok) {
      throw new Error(`Failed to submit review (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getReview(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/review`);

    if (res.status === 204) {
      return null;
    }

    if (!res.ok) {
      throw new Error(`Failed to load review (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getTimeline(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/timeline`);

    if (!res.ok) {
      throw new Error(`Failed to load timeline (HTTP ${res.status})`);
    }

    return res.json();
  },

  async getAuditLogs(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/audit`);

    if (!res.ok) {
      throw new Error(`Failed to load audit logs (HTTP ${res.status})`);
    }

    return res.json();
  },

  async verifyIntegrity(caseId, data, hash) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/verify-integrity`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        data,
        hash
      })
    });

    if (!res.ok) {
      throw new Error(`Integrity verification failed (HTTP ${res.status})`);
    }

    return res.json();
  },

  getPdfReportUrl(caseId) {
    return `${API_BASE}/cases/${caseId}/report/pdf`;
  },

  getCertificateUrl(caseId) {
    return `${API_BASE}/cases/${caseId}/certificate`;
  },

  getCsvExportUrl(caseId) {
    return `${API_BASE}/cases/${caseId}/export/csv`;
  },

  getJsonExportUrl(caseId) {
    return `${API_BASE}/cases/${caseId}/export/json`;
  }
};