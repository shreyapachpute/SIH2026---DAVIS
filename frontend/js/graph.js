/**
 * DAVIS - Cytoscape.js Relationship Graph Controller
 */

let cy = null;

const GraphController = {
  init() {
    if (!document.getElementById('cy')) return;

    cy = cytoscape({
      container: document.getElementById('cy'),
      style: [
        {
          selector: 'node',
          style: {
            'label': 'data(name)',
            'color': '#f0f6fc',
            'font-size': '11px',
            'font-family': 'Inter, sans-serif',
            'text-valign': 'bottom',
            'text-margin-y': 6,
            'background-color': '#3b82f6',
            'border-width': 2,
            'border-color': '#60a5fa',
            'width': 38,
            'height': 38,
            'transition-property': 'background-color, border-color, width, height',
            'transition-duration': '0.2s'
          }
        },
        {
          selector: 'node[type = "PERSONA"]',
          style: {
            'shape': 'diamond',
            'background-color': '#00e5ff',
            'border-color': '#a5f3fc',
            'border-width': 3,
            'width': 48,
            'height': 48,
            'font-weight': 'bold',
            'color': '#00e5ff'
          }
        },
        {
          selector: 'node[type = "ALIAS"]',
          style: {
            'shape': 'hexagon',
            'background-color': '#a855f7',
            'border-color': '#e9d5ff',
            'width': 40,
            'height': 40
          }
        },
        {
          selector: 'node[type = "PGP_KEY"]',
          style: {
            'shape': 'round-rectangle',
            'background-color': '#00e676',
            'border-color': '#86efac',
            'width': 42,
            'height': 42
          }
        },
        {
          selector: 'node[type = "WALLET"]',
          style: {
            'shape': 'octagon',
            'background-color': '#ffb300',
            'border-color': '#fde68a',
            'width': 44,
            'height': 44
          }
        },
        {
          selector: 'node[type = "EMAIL"]',
          style: {
            'shape': 'rectangle',
            'background-color': '#f97316',
            'border-color': '#fed7aa',
            'width': 36,
            'height': 36
          }
        },
        {
          selector: 'node[type = "DOMAIN"]',
          style: {
            'shape': 'barrel',
            'background-color': '#3b82f6',
            'border-color': '#93c5fd',
            'width': 40,
            'height': 40
          }
        },
        {
          selector: 'node[type = "ONION_SERVICE"]',
          style: {
            'shape': 'star',
            'background-color': '#ff1744',
            'border-color': '#fecdd3',
            'width': 46,
            'height': 46
          }
        },
        {
          selector: 'node[type = "FORUM_ACCOUNT"]',
          style: {
            'shape': 'ellipse',
            'background-color': '#64748b',
            'border-color': '#cbd5e1',
            'width': 36,
            'height': 36
          }
        },
        {
          selector: 'node:selected',
          style: {
            'border-color': '#00e5ff',
            'border-width': 4,
            'shadow-blur': 15,
            'shadow-color': '#00e5ff',
            'shadow-opacity': 0.8
          }
        },
        {
          selector: 'edge',
          style: {
            'width': 2,
            'line-color': '#334155',
            'target-arrow-color': '#64748b',
            'target-arrow-shape': 'triangle',
            'curve-style': 'bezier',
            'label': 'data(label)',
            'font-size': '9px',
            'color': '#94a3b8',
            'text-rotation': 'autorotate',
            'text-margin-y': -8
          }
        },
        {
          selector: 'edge[label *= "CONFLICT"]',
          style: {
            'line-color': '#ff1744',
            'target-arrow-color': '#ff1744',
            'line-style': 'dashed',
            'color': '#ff1744'
          }
        }
      ],
      layout: {
        name: 'cose',
        animate: false,
        nodeOverlap: 20,
        idealEdgeLength: 100,
        nodeRepulsion: 400000
      }
    });

    // Node click handler -> Open node inspector
    cy.on('tap', 'node', (evt) => {
      const node = evt.target;
      GraphController.showNodeDetails(node.data());
    });

    cy.on('tap', (evt) => {
      if (evt.target === cy) {
        document.getElementById('node-inspector').style.display = 'none';
      }
    });
  },

  render(elements) {
    if (!cy) this.init();
    if (!cy) return;

    cy.elements().remove();
    cy.add(elements.nodes || []);
    cy.add(elements.edges || []);

    const layout = cy.layout({
      name: 'cose',
      animate: true,
      animationDuration: 500,
      padding: 40,
      nodeRepulsion: 500000,
      idealEdgeLength: 120
    });
    layout.run();
  },

  showNodeDetails(data) {
    const inspector = document.getElementById('node-inspector');
    if (!inspector) return;

    document.getElementById('insp-name').textContent = data.name || 'Unknown';
    document.getElementById('insp-type').textContent = data.type || 'N/A';
    document.getElementById('insp-norm').textContent = data.normalizedValue || 'N/A';
    document.getElementById('insp-conf').textContent = (data.confidence ? (data.confidence * 100).toFixed(0) + '%' : 'N/A');

    inspector.style.display = 'block';
  },

  zoomIn() { if (cy) cy.zoom(cy.zoom() * 1.25); },
  zoomOut() { if (cy) cy.zoom(cy.zoom() * 0.8); },
  fit() { if (cy) cy.fit(40); },

  filterByType(type) {
    if (!cy) return;
    if (!type || type === 'ALL') {
      cy.elements().show();
    } else {
      cy.elements().hide();
      cy.elements(`node[type = "${type}"]`).show();
      cy.elements(`node[type = "${type}"]`).connectedEdges().show();
    }
  }
};
