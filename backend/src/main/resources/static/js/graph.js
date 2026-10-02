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

        /* =========================
           DEFAULT NODE
           ========================= */
        {
          selector: 'node',
          style: {
            'label': 'data(name)',
            'color': '#e2e8f0',
            'font-size': '11px',
            'font-family': 'Inter, sans-serif',
            'font-weight': '500',

            'text-valign': 'bottom',
            'text-margin-y': 7,

            'background-color': '#2563eb',
            'border-width': 2,
            'border-color': '#60a5fa',

            'width': 38,
            'height': 38,

            'transition-property':
              'background-color, border-color, width, height',
            'transition-duration': '0.2s'
          }
        },

        /* =========================
           PERSONA
           ========================= */
        {
          selector: 'node[type = "PERSONA"]',
          style: {
            'shape': 'diamond',
            'background-color': '#06b6d4',
            'border-color': '#a5f3fc',
            'border-width': 3,

            'width': 48,
            'height': 48,

            'font-weight': 'bold',
            'color': '#67e8f9'
          }
        },

        /* =========================
           ALIAS
           ========================= */
        {
          selector: 'node[type = "ALIAS"]',
          style: {
            'shape': 'hexagon',
            'background-color': '#8b5cf6',
            'border-color': '#c4b5fd',

            'width': 40,
            'height': 40
          }
        },

        /* =========================
           PGP KEY
           ========================= */
        {
          selector: 'node[type = "PGP_KEY"]',
          style: {
            'shape': 'round-rectangle',
            'background-color': '#10b981',
            'border-color': '#6ee7b7',

            'width': 42,
            'height': 42
          }
        },

        /* =========================
           WALLET
           ========================= */
        {
          selector: 'node[type = "WALLET"]',
          style: {
            'shape': 'octagon',
            'background-color': '#f59e0b',
            'border-color': '#fde68a',

            'width': 44,
            'height': 44
          }
        },

        /* =========================
           EMAIL
           ========================= */
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

        /* =========================
           DOMAIN
           ========================= */
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

        /* =========================
           ONION SERVICE
           ========================= */
        {
          selector: 'node[type = "ONION_SERVICE"]',
          style: {
            'shape': 'star',
            'background-color': '#ef4444',
            'border-color': '#fca5a5',

            'width': 46,
            'height': 46
          }
        },

        /* =========================
           FORUM ACCOUNT
           ========================= */
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

        /* =========================
           SELECTED NODE
           ========================= */
        {
          selector: 'node:selected',
          style: {
            'border-color': '#00e5ff',
            'border-width': 4,

            'shadow-blur': 18,
            'shadow-color': '#00e5ff',
            'shadow-opacity': 0.85
          }
        },

        /* =========================
           NORMAL EDGE
           ========================= */
        {
          selector: 'edge',
          style: {
            'width': 2,

            'line-color': '#475569',
            'target-arrow-color': '#94a3b8',
            'target-arrow-shape': 'triangle',

            'curve-style': 'bezier',

            'label': 'data(label)',
            'font-size': '9px',
            'font-family': 'Inter, sans-serif',
            'color': '#cbd5e1',

            'text-rotation': 'autorotate',
            'text-margin-y': -8
          }
        },

        /* =========================
           CONFLICT EDGE
           ========================= */
        {
          selector: 'edge[label *= "CONFLICT"]',
          style: {
            'line-color': '#ef4444',
            'target-arrow-color': '#ef4444',

            'line-style': 'dashed',

            'color': '#fca5a5',
            'width': 2.5
          }
        }
      ],

      /* =========================
         INITIAL GRAPH LAYOUT
         ========================= */
      layout: {
        name: 'cose',
        animate: false,

        nodeOverlap: 20,

        idealEdgeLength: 150,
        nodeRepulsion: 650000
      }
    });

    /* =========================
       NODE CLICK
       ========================= */

    cy.on('tap', 'node', (evt) => {
      const node = evt.target;

      GraphController.showNodeDetails(node.data());
    });

    /* =========================
       CLICK EMPTY GRAPH
       ========================= */

    cy.on('tap', (evt) => {
      if (evt.target === cy) {

        const inspector =
          document.getElementById('node-inspector');

        if (inspector) {
          inspector.style.display = 'none';
        }
      }
    });
  },


  /* =========================
     RENDER GRAPH
     ========================= */

  render(elements) {

    if (!cy) {
      this.init();
    }

    if (!cy) return;

    cy.elements().remove();

    cy.add(elements.nodes || []);
    cy.add(elements.edges || []);

    const layout = cy.layout({
      name: 'cose',

      animate: true,
      animationDuration: 500,

      padding: 40,

      nodeRepulsion: 650000,
      idealEdgeLength: 150
    });

    layout.run();
  },


  /* =========================
     NODE INSPECTOR
     ========================= */

  showNodeDetails(data) {

    const inspector =
      document.getElementById('node-inspector');

    if (!inspector) return;

    document.getElementById('insp-name').textContent =
      data.name || 'Unknown';

    document.getElementById('insp-type').textContent =
      data.type || 'N/A';

    document.getElementById('insp-norm').textContent =
      data.normalizedValue || 'N/A';

    document.getElementById('insp-conf').textContent =
      (
        data.confidence
          ? (data.confidence * 100).toFixed(0) + '%'
          : 'N/A'
      );

    inspector.style.display = 'block';
  },


  /* =========================
     ZOOM CONTROLS
     ========================= */

  zoomIn() {
    if (cy) {
      cy.zoom(cy.zoom() * 1.25);
    }
  },

  zoomOut() {
    if (cy) {
      cy.zoom(cy.zoom() * 0.8);
    }
  },

  fit() {
    if (cy) {
      cy.fit(40);
    }
  },


  /* =========================
     FILTER BY NODE TYPE
     ========================= */

  filterByType(type) {

    if (!cy) return;

    if (!type || type === 'ALL') {

      cy.elements().show();

    } else {

      cy.elements().hide();

      const selectedNodes =
        cy.elements(`node[type = "${type}"]`);

      selectedNodes.show();

      selectedNodes.connectedEdges().show();
    }
  }
};