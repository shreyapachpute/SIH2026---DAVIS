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
    return res.json();
  },

  async getCases() {
    const res = await fetch(`${API_BASE}/cases`);
    return res.json();
  },

  async createCase(caseData) {
    const res = await fetch(`${API_BASE}/cases`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(caseData)
    });
    return res.json();
  },

  async loadDemoCase() {
    const res = await fetch(`${API_BASE}/cases/demo`, { method: 'POST' });
    return res.json();
  },

  async getCase(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}`);
    return res.json();
  },

  async getIndicators(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/indicators`);
    return res.json();
  },

  async addIndicator(caseId, indicator) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/indicators`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(indicator)
    });
    return res.json();
  },

  async runCollection(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/collect`, { method: 'POST' });
    return res.json();
  },

  async getGraph(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/graph`);
    return res.json();
  },

  async getEntities(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/entities`);
    return res.json();
  },

  async getEvidence(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/evidence`);
    return res.json();
  },

  async addEvidence(caseId, evidenceData) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/evidence`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(evidenceData)
    });
    return res.json();
  },

  async runAnalysis(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/analyze`, { method: 'POST' });
    return res.json();
  },

  async getAnalysis(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/analysis`);
    return res.json();
  },

  async runStressTest(caseId, dependency) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/stress-test`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ removedDependency: dependency })
    });
    return res.json();
  },

  async getStressTests(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/stress-test`);
    return res.json();
  },

  async submitReview(caseId, reviewData) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/review`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(reviewData)
    });
    return res.json();
  },

  async getReview(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/review`);
    if (res.status === 204) return null;
    return res.json();
  },

  async getTimeline(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/timeline`);
    return res.json();
  },

  async getAuditLogs(caseId) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/audit`);
    return res.json();
  },

  async verifyIntegrity(caseId, data, hash) {
    const res = await fetch(`${API_BASE}/cases/${caseId}/verify-integrity`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ data, hash })
    });
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
