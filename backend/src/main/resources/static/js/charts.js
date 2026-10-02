/**
 * DAVIS - Chart.js Visualizations
 */

let attributionChart = null;

const ChartsController = {
  renderAttributionRadar(breakdown) {
    const canvas = document.getElementById('attributionChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (attributionChart) {
      attributionChart.destroy();
    }

    const labels = [
      'Cryptographic (PGP)',
      'Financial (Blockchain)',
      'Infrastructure (DNS/Tor)',
      'Alias / Identity',
      'Stylometric Match',
      'Temporal Overlap'
    ];

    const dataValues = [
      breakdown.cryptographicContribution || 0,
      breakdown.financialContribution || 0,
      breakdown.infrastructureContribution || 0,
      breakdown.aliasContribution || 0,
      breakdown.stylometricContribution || 0,
      breakdown.temporalContribution || 0
    ];

    attributionChart = new Chart(ctx, {
      type: 'radar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Attribution Vector Strength',
          data: dataValues,
          backgroundColor: 'rgba(0, 229, 255, 0.25)',
          borderColor: '#00e5ff',
          pointBackgroundColor: '#00e5ff',
          pointBorderColor: '#fff',
          pointHoverBackgroundColor: '#fff',
          pointHoverBorderColor: '#00e5ff',
          borderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          r: {
            angleLines: { color: '#24324d' },
            grid: { color: '#1a2234' },
            pointLabels: {
              color: '#94a3b8',
              font: { size: 10, family: 'Inter, sans-serif' }
            },
            ticks: {
              display: false,
              backdropColor: 'transparent',
              max: 25,
              min: 0
            }
          }
        },
        plugins: {
          legend: { display: false }
        }
      }
    });
  }
};
