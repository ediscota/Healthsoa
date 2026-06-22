// HealthSOA Presentation Generator — PptxGenJS
// 14 content slides + 3 section dividers = 17 slides total
const PptxGenJS = require("pptxgenjs");
const pptx = new PptxGenJS();
pptx.layout = "LAYOUT_16x9";

// ── Palette ────────────────────────────────────────────────────────────────
const C = {
  darkBlue:  "1F4E79",
  violet:    "534AB7",
  teal:      "0F6E56",
  white:     "FFFFFF",
  lightBg:   "F5F8FA",
  lightGrey: "E8EEF2",
  midGrey:   "9BAAB5",
  darkGrey:  "595959",
  violetBg:  "EAE8F8",
  tealBg:    "D4EDE6",
  blueBg:    "D9E8F5",
  amber:     "B8860B",
  amberBg:   "FFF3CD",
};

// ── Helper: slide title ────────────────────────────────────────────────────
function addTitle(slide, text, subtitle) {
  slide.addText(text, {
    x: 0.5, y: 0.22, w: 9, h: 0.62,
    fontSize: 34, bold: true, color: C.darkBlue,
    fontFace: "Calibri", align: "left",
  });
  if (subtitle) {
    slide.addText(subtitle, {
      x: 0.5, y: 0.86, w: 9, h: 0.3,
      fontSize: 13, color: C.midGrey, fontFace: "Calibri",
    });
  }
  // thin separator line (shape, not accent-bar)
  slide.addShape(pptx.ShapeType.rect, {
    x: 0.5, y: 1.18, w: 9, h: 0.025,
    fill: { color: C.lightGrey }, line: { type: "none" },
  });
}

// ── Helper: left-accent card ───────────────────────────────────────────────
function card(slide, x, y, w, h, accentColor) {
  slide.addShape(pptx.ShapeType.rect, {
    x, y, w, h,
    fill: { color: C.white }, line: { color: C.lightGrey, width: 1 },
  });
  slide.addShape(pptx.ShapeType.rect, {
    x, y, w: 0.07, h,
    fill: { color: accentColor }, line: { type: "none" },
  });
}

// ── Helper: section divider slide ─────────────────────────────────────────
function sectionDivider(label, presenter, subtitle) {
  const slide = pptx.addSlide();
  slide.background = { color: C.darkBlue };
  slide.addText(label, {
    x: 0.8, y: 1.6, w: 8.4, h: 1.1,
    fontSize: 42, bold: true, color: C.white,
    fontFace: "Calibri", align: "left",
  });
  slide.addText(presenter, {
    x: 0.8, y: 2.75, w: 8.4, h: 0.5,
    fontSize: 20, color: "A8C4E0", fontFace: "Calibri",
  });
  if (subtitle) {
    slide.addText(subtitle, {
      x: 0.8, y: 3.3, w: 8.4, h: 0.4,
      fontSize: 14, color: "7BA8C8", fontFace: "Calibri", italic: true,
    });
  }
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 1 — Title
// ═══════════════════════════════════════════════════════════════════════════
function slide01_title() {
  const s = pptx.addSlide();
  s.background = { color: C.darkBlue };

  // big teal accent block left
  s.addShape(pptx.ShapeType.rect, {
    x: 0, y: 0, w: 0.18, h: 5.63,
    fill: { color: C.teal }, line: { type: "none" },
  });

  s.addText("HealthSOA", {
    x: 0.55, y: 0.9, w: 8.9, h: 1.3,
    fontSize: 60, bold: true, color: C.white,
    fontFace: "Calibri",
  });
  s.addText("Hospital Platform for Pre-Procedure\nClinical Assessment", {
    x: 0.55, y: 2.25, w: 8.9, h: 1.1,
    fontSize: 24, color: "A8C4E0", fontFace: "Calibri",
  });
  s.addText("Service-Oriented Software Engineering  ·  A.A. 2025/2026", {
    x: 0.55, y: 3.55, w: 8.9, h: 0.38,
    fontSize: 15, color: "7BA8C8", fontFace: "Calibri",
  });
  s.addText("Università degli Studi dell'Aquila  ·  DISIM", {
    x: 0.55, y: 3.95, w: 8.9, h: 0.38,
    fontSize: 15, color: "7BA8C8", fontFace: "Calibri",
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SECTION DIVIDER — Block 1
// ═══════════════════════════════════════════════════════════════════════════
// (called inline)

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 2 — Domain & Motivation
// ═══════════════════════════════════════════════════════════════════════════
function slide02_domain() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "Domain & Motivation");

  const items = [
    {
      accent: C.teal,
      icon: "SOAP",
      title: "Legacy SOAP Integration",
      body: "The regional Anagrafe Sanitaria is a real-world institutional system that exposes typed contracts via WSDL (HL7-style). Contract-first SOAP is imposed by the interoperability requirement — not an aesthetic choice.",
    },
    {
      accent: C.violet,
      icon: "ASYNC",
      title: "Asynchronous Lab Processing",
      body: "A lab panel ordered urgently has real latency while the biological sample is processed. A synchronous call would hold threads and connections; asynchrony is mandated by the physics of the problem.",
    },
    {
      accent: C.teal,
      icon: "PAR",
      title: "Independent Parallel Branches",
      body: "A fitness assessment needs the diagnostic bundle (lab + imaging) AND the clinical history (registry + prescriptions) at the same time. The two data-collection branches are logically independent; sequential execution would needlessly double latency.",
    },
    {
      accent: C.violet,
      icon: "SCALE",
      title: "Bursty Peak Load",
      body: "Anagrafe and Laboratorio sit on the critical path of every workflow and are hit by many clinicians simultaneously during morning rounds and ER rushes. Load is bursty and read-heavy — replication is motivated by specific system behaviour.",
    },
  ];

  items.forEach((it, i) => {
    const y = 1.32 + i * 1.02;
    card(s, 0.5, y, 9.0, 0.9, it.accent);
    s.addText(it.title, {
      x: 0.75, y: y + 0.07, w: 8.5, h: 0.3,
      fontSize: 13, bold: true, color: C.darkBlue, fontFace: "Calibri",
    });
    s.addText(it.body, {
      x: 0.75, y: y + 0.4, w: 8.5, h: 0.42,
      fontSize: 10.5, color: C.darkGrey, fontFace: "Calibri",
    });
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 3 — System Overview (architecture diagram)
// ═══════════════════════════════════════════════════════════════════════════
function slide03_overview() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "System Overview");

  // helper: draw a rounded box with label
  function box(label, x, y, w, h, fill, border, textColor, fsize) {
    s.addShape(pptx.ShapeType.roundRect, {
      x, y, w, h,
      fill: { color: fill },
      line: { color: border, width: 1.5 },
      rectRadius: 0.04,
    });
    s.addText(label, {
      x, y, w, h,
      fontSize: fsize || 10, bold: true, color: textColor,
      fontFace: "Calibri", align: "center", valign: "middle",
    });
  }

  // helper: vertical arrow
  function varrow(x, y1, y2) {
    s.addShape(pptx.ShapeType.line, {
      x, y: y1, w: 0, h: y2 - y1,
      line: { color: C.midGrey, width: 1.5, endArrowType: "arrow" },
    });
  }

  // ── Layer: Client ──────────────────────────────
  box("Web Client  (Browser)", 3.3, 1.25, 3.4, 0.48, C.blueBg, "6A9EC4", C.darkBlue, 11);

  varrow(5.0, 1.73, 2.0);

  // ── Layer: Gateway ─────────────────────────────
  box("API Gateway  :9000", 3.3, 2.0, 3.4, 0.48, "D0DCE8", "6A9EC4", C.darkBlue, 11);

  // split arrow to prosumer row
  varrow(5.0, 2.48, 2.72);

  // ── Layer: Prosumers ───────────────────────────
  const prosumers = [
    ["Care\nCoordinator\n:9203", 0.5],
    ["Clinical\nAggregator\n:9202", 3.3],
    ["Diagnostic\nAggregator\n:9201", 6.1],
  ];
  prosumers.forEach(([lbl, x]) => {
    box(lbl, x, 2.72, 2.95, 0.9, C.violetBg, C.violet, C.violet, 10);
  });

  // arrows from prosumers to provider row
  varrow(1.98, 3.62, 3.88);
  varrow(4.78, 3.62, 3.88);
  varrow(7.58, 3.62, 3.88);

  // ── Layer: Providers ───────────────────────────
  const providers = [
    ["Anagrafe\nSOAP :9101", 0.5],
    ["Farmacia\nREST :9103", 2.85],
    ["Laboratorio\nREST :9102", 5.2],
    ["Imaging\nREST :9104", 7.55],
  ];
  providers.forEach(([lbl, x]) => {
    box(lbl, x, 3.88, 2.1, 0.8, C.tealBg, C.teal, C.teal, 9.5);
  });

  // arrows to MySQL
  varrow(1.55, 4.68, 4.92);
  varrow(3.9, 4.68, 4.92);
  varrow(6.25, 4.68, 4.92);
  varrow(8.6, 4.68, 4.92);

  // ── Layer: MySQL ───────────────────────────────
  box("MySQL 8  (anagrafe · farmacia · laboratorio · imaging)", 0.5, 4.92, 9.0, 0.45, C.lightGrey, C.midGrey, C.darkBlue, 10);

  // ── Legend ─────────────────────────────────────
  const legend = [
    [C.violetBg, C.violet, "Prosumer"],
    [C.tealBg, C.teal, "Provider"],
    ["D0DCE8", "6A9EC4", "Infrastructure"],
    [C.lightGrey, C.midGrey, "Database"],
  ];
  legend.forEach(([fill, border, lbl], i) => {
    s.addShape(pptx.ShapeType.rect, {
      x: 0.5 + i * 2.3, y: 5.42, w: 0.22, h: 0.18,
      fill: { color: fill }, line: { color: border, width: 1 },
    });
    s.addText(lbl, {
      x: 0.78 + i * 2.3, y: 5.42, w: 1.8, h: 0.18,
      fontSize: 9.5, color: C.darkGrey, fontFace: "Calibri",
    });
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 4 — Infrastructure Services
// ═══════════════════════════════════════════════════════════════════════════
function slide04_infra() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "Infrastructure Services");

  const items = [
    {
      port: ":8888",
      title: "Config Server",
      bullets: [
        "Centralises all service properties in a sibling git repository (healthsoa-properties-repository)",
        "Each microservice reads its entire config on startup via spring.config.import — no hardcoded values",
        "Runtime refresh: POST /actuator/refresh + @RefreshScope pushes config changes without restarting",
      ],
    },
    {
      port: ":8761",
      title: "Discovery Server — Eureka",
      bullets: [
        "All services self-register at startup via @EnableDiscoveryClient — no static address book",
        "Gateway and Feign clients resolve service names dynamically; load balancing is transparent",
        "Health dashboard at http://localhost:8761 shows registered instances and their status",
      ],
    },
    {
      port: ":9000",
      title: "API Gateway",
      bullets: [
        "Single public entry point — clients never call services directly, only /api/<prefix>/**",
        "Routes use lb://service-name URIs; Spring Cloud LoadBalancer picks a healthy replica",
        "StripPrefix filter rewrites paths before forwarding (e.g. /api/pharmacy → /patients/…)",
      ],
    },
  ];

  items.forEach((it, i) => {
    const y = 1.32 + i * 1.38;
    s.addShape(pptx.ShapeType.rect, {
      x: 0.5, y, w: 9.0, h: 1.25,
      fill: { color: C.white }, line: { color: C.lightGrey, width: 1 },
    });
    s.addText(it.title, {
      x: 0.7, y: y + 0.08, w: 6.5, h: 0.32,
      fontSize: 14, bold: true, color: C.darkBlue, fontFace: "Calibri",
    });
    s.addShape(pptx.ShapeType.rect, {
      x: 7.8, y: y + 0.08, w: 1.5, h: 0.3,
      fill: { color: C.darkBlue }, line: { type: "none" },
    });
    s.addText(it.port, {
      x: 7.8, y: y + 0.08, w: 1.5, h: 0.3,
      fontSize: 12, bold: true, color: C.white,
      fontFace: "Consolas", align: "center", valign: "middle",
    });
    it.bullets.forEach((b, j) => {
      s.addText("• " + b, {
        x: 0.72, y: y + 0.44 + j * 0.26, w: 8.6, h: 0.24,
        fontSize: 10.5, color: C.darkGrey, fontFace: "Calibri",
      });
    });
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 5 — Load Balancing
// ═══════════════════════════════════════════════════════════════════════════
function slide05_lb() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "Load Balancing & Scalability");

  // Left column — what scales
  s.addShape(pptx.ShapeType.rect, {
    x: 0.5, y: 1.3, w: 4.3, h: 3.9,
    fill: { color: C.white }, line: { color: C.lightGrey, width: 1 },
  });
  s.addText("Scalable Services", {
    x: 0.7, y: 1.4, w: 3.9, h: 0.35,
    fontSize: 14, bold: true, color: C.teal, fontFace: "Calibri",
  });
  const scalable = [
    ["Anagrafe  :9101", "Read-heavy on every workflow — demographics, history, allergies queried by every request"],
    ["Laboratorio  :9102", "Peak order bursts during morning rounds and ER rushes; async processing with per-instance thread pools"],
    ["Imaging  :9104", "Same bursty profile as Laboratorio; stateless — any replica handles any request"],
    ["Diagnostic Aggregator  :9201", "Critical path of UC-3; stateless trackingId design enables transparent horizontal scaling"],
  ];
  scalable.forEach(([name, desc], i) => {
    s.addText(name, {
      x: 0.72, y: 1.85 + i * 0.82, w: 3.9, h: 0.28,
      fontSize: 11.5, bold: true, color: C.teal, fontFace: "Calibri",
    });
    s.addText(desc, {
      x: 0.72, y: 2.15 + i * 0.82, w: 3.9, h: 0.44,
      fontSize: 9.5, color: C.darkGrey, fontFace: "Calibri",
    });
  });

  s.addText("Single-instance (stateful/coordinator):", {
    x: 0.72, y: 5.0, w: 3.9, h: 0.28,
    fontSize: 10.5, bold: true, color: C.violet, fontFace: "Calibri",
  });

  // Right column — implementation
  s.addShape(pptx.ShapeType.rect, {
    x: 5.2, y: 1.3, w: 4.3, h: 3.9,
    fill: { color: C.white }, line: { color: C.lightGrey, width: 1 },
  });
  s.addText("How It Works", {
    x: 5.4, y: 1.4, w: 3.9, h: 0.35,
    fontSize: 14, bold: true, color: C.darkBlue, fontFace: "Calibri",
  });
  const impl = [
    ["Eureka registration", "Every replica self-registers; gateway and Feign query the live registry at call time — no static routes."],
    ["lb:// routing", "Gateway routes contain lb://service-name; Spring Cloud LoadBalancer distributes across healthy instances."],
    ["@FeignClient(name=…)", "Feign resolves the name via Eureka — no IP, no port. Round-robin load balancing is implicit."],
    ["docker-compose --scale", "docker-compose up --scale laboratorio-service=3 spins up replicas with zero code changes."],
    ["expose vs ports", "Scalable services use expose (no fixed host port), preventing port conflicts with multiple replicas."],
  ];
  impl.forEach(([name, desc], i) => {
    s.addText(name, {
      x: 5.4, y: 1.85 + i * 0.73, w: 3.9, h: 0.26,
      fontSize: 11, bold: true, color: C.darkBlue, fontFace: "Calibri",
    });
    s.addText(desc, {
      x: 5.4, y: 2.13 + i * 0.73, w: 3.9, h: 0.38,
      fontSize: 9.5, color: C.darkGrey, fontFace: "Calibri",
    });
  });
  s.addText("Clinical Aggregator and Care Coordinator remain single-instance (stateful SOAP port / orchestrator).", {
    x: 5.4, y: 5.0, w: 3.9, h: 0.38,
    fontSize: 9.5, color: C.violet, fontFace: "Calibri", italic: true,
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 6 — Service Providers
// ═══════════════════════════════════════════════════════════════════════════
function slide06_providers() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "Service Providers");

  const rows = [
    ["Service", "Protocol", "Port", "Responsibility"],
    ["Anagrafe Pazienti", "SOAP (CXF)", "9101", "Patient demographics, medical history (conditions), known allergies — contract-first WSDL"],
    ["Laboratorio Analisi", "REST async", "9102", "Lab test orders (202 Accepted), status polling, result retrieval, callback/webhook support"],
    ["Farmacia / Prescrizioni", "REST", "9103", "Active prescriptions per patient, drug interaction check endpoint"],
    ["Diagnostica per Immagini", "REST async", "9104", "Radiology and imaging reports — same async model as Laboratorio"],
  ];

  const colW = [1.8, 1.3, 0.8, 5.6];
  const colX = [0.5, 2.3, 3.6, 4.4];

  rows.forEach((row, ri) => {
    const y = 1.3 + ri * 0.87;
    const isH = ri === 0;
    row.forEach((cell, ci) => {
      s.addShape(pptx.ShapeType.rect, {
        x: colX[ci], y, w: colW[ci], h: 0.8,
        fill: { color: isH ? C.teal : ci === 0 ? C.tealBg : C.white },
        line: { color: isH ? C.teal : C.lightGrey, width: 1 },
      });
      s.addText(cell, {
        x: colX[ci] + 0.07, y, w: colW[ci] - 0.1, h: 0.8,
        fontSize: isH ? 12 : ci === 2 ? 13 : 11,
        bold: isH || ci === 2,
        color: isH ? C.white : ci === 0 ? C.teal : ci === 2 ? C.darkBlue : C.darkGrey,
        fontFace: ci === 2 ? "Consolas" : "Calibri",
        valign: "middle",
      });
    });
  });

  s.addText("All providers: independent Maven project · @EnableDiscoveryClient · springdoc-openapi Swagger UI · own MySQL schema", {
    x: 0.5, y: 5.64, w: 9.0, h: 0.24,
    fontSize: 10, color: C.midGrey, fontFace: "Calibri", italic: true,
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 7 — Service Prosumers
// ═══════════════════════════════════════════════════════════════════════════
function slide07_prosumers() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "Service Prosumers");

  const rows = [
    ["Service", "Port", "Providers Consumed", "Use Case"],
    ["Diagnostic\nAggregator", "9201", "Laboratorio (REST async) +\nImaging (REST async)", "UC-3: Order tests, manage async lifecycle, aggregate DiagnosticBundle — stateless via Base64URL trackingId"],
    ["Clinical\nAggregator", "9202", "Anagrafe (SOAP/CXF) +\nFarmacia (REST)", "UC-2: Bridge SOAP↔REST, compose demographics + history + prescriptions → ClinicalProfile"],
    ["Care\nCoordinator", "9203", "Clinical Aggregator (REST) +\nDiagnostic Aggregator (REST)", "UC-1: Parallel orchestration, sync barrier (allOf().join()), risk analysis → FitnessReport"],
  ];

  const colW = [1.4, 0.7, 2.5, 4.9];
  const colX = [0.5, 1.9, 2.6, 5.1];

  rows.forEach((row, ri) => {
    const h = ri === 0 ? 0.48 : 1.22;
    const y = ri === 0 ? 1.3 : 1.78 + (ri - 1) * 1.3;
    const isH = ri === 0;
    row.forEach((cell, ci) => {
      s.addShape(pptx.ShapeType.rect, {
        x: colX[ci], y, w: colW[ci], h,
        fill: { color: isH ? C.violet : ci === 0 ? C.violetBg : C.white },
        line: { color: isH ? C.violet : C.lightGrey, width: 1 },
      });
      s.addText(cell, {
        x: colX[ci] + 0.07, y, w: colW[ci] - 0.1, h,
        fontSize: isH ? 12 : ci === 1 ? 14 : 10.5,
        bold: isH || ci === 1,
        color: isH ? C.white : ci === 0 ? C.violet : ci === 1 ? C.darkBlue : C.darkGrey,
        fontFace: ci === 1 ? "Consolas" : "Calibri",
        valign: "middle",
      });
    });
  });

  s.addText("Prosumers have no domain persistence — they are pure orchestration components. Each calls providers via @FeignClient(name=…).", {
    x: 0.5, y: 5.6, w: 9.0, h: 0.24,
    fontSize: 10, color: C.midGrey, fontFace: "Calibri", italic: true,
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 8 — Parallel Execution & Synchronisation
// ═══════════════════════════════════════════════════════════════════════════
function slide08_parallel() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "Parallel Execution & Synchronisation", "Care Coordinator — UC-1");

  function box(label, x, y, w, h, fill, border, tc, fsize) {
    s.addShape(pptx.ShapeType.roundRect, {
      x, y, w, h,
      fill: { color: fill }, line: { color: border, width: 1.5 }, rectRadius: 0.05,
    });
    s.addText(label, {
      x, y, w, h, fontSize: fsize || 11, bold: true, color: tc,
      fontFace: "Calibri", align: "center", valign: "middle",
    });
  }

  function arrow(x1, y1, x2, y2) {
    s.addShape(pptx.ShapeType.line, {
      x: x1, y: y1, w: x2 - x1, h: y2 - y1,
      line: { color: C.midGrey, width: 1.8, endArrowType: "arrow" },
    });
  }

  // Request in
  s.addText("Client Request", {
    x: 0.5, y: 1.45, w: 1.7, h: 0.35,
    fontSize: 10, color: C.midGrey, fontFace: "Calibri", italic: true, align: "center",
  });
  arrow(1.3, 1.63, 2.3, 1.63);

  // Care Coordinator box
  box("Care Coordinator\n:9203", 2.3, 1.35, 2.8, 0.62, C.darkBlue, C.darkBlue, C.white, 11);

  // Fork lines
  arrow(3.7, 1.97, 2.65, 2.82);   // to Clinical (left)
  arrow(3.7, 1.97, 7.05, 2.82);   // to Diagnostic (right)

  s.addText("supplyAsync()", {
    x: 1.0, y: 2.28, w: 1.8, h: 0.25,
    fontSize: 9.5, color: C.violet, fontFace: "Consolas", italic: true,
  });
  s.addText("supplyAsync()", {
    x: 6.1, y: 2.28, w: 1.8, h: 0.25,
    fontSize: 9.5, color: C.teal, fontFace: "Consolas", italic: true,
  });

  // Clinical branch (left)
  box("Clinical Aggregator\n:9202", 0.5, 2.82, 2.6, 0.62, C.violetBg, C.violet, C.violet, 10.5);
  s.addText("→ Anagrafe (SOAP)\n→ Farmacia (REST)", {
    x: 0.5, y: 3.48, w: 2.6, h: 0.45,
    fontSize: 9, color: C.violet, fontFace: "Calibri", align: "center",
  });

  // Diagnostic branch (right)
  box("Diagnostic Aggregator\n:9201", 6.4, 2.82, 2.8, 0.62, C.tealBg, C.teal, C.teal, 10.5);
  s.addText("→ Laboratorio (async)\n→ Imaging (async)", {
    x: 6.4, y: 3.48, w: 2.8, h: 0.45,
    fontSize: 9, color: C.teal, fontFace: "Calibri", align: "center",
  });

  // Join lines
  arrow(1.8, 3.95, 3.7, 4.5);
  arrow(7.8, 3.95, 5.8, 4.5);

  // allOf barrier
  box("CompletableFuture.allOf(diag, clin).join()\n— synchronisation barrier —", 3.0, 4.5, 3.7, 0.62, C.darkBlue, C.darkBlue, C.white, 10);

  // Down to risk analysis
  arrow(4.85, 5.12, 4.85, 5.38);

  // Risk analysis
  box("riskAnalyzer.analyze()  →  FitnessReport", 2.3, 5.38, 5.1, 0.48, C.amberBg, C.amber, C.darkBlue, 11);

  // FitnessReport arrow out
  arrow(8.0, 5.62, 9.3, 5.62);
  s.addText("FitnessReport\nto client", {
    x: 8.8, y: 5.4, w: 1.2, h: 0.4,
    fontSize: 9, color: C.midGrey, fontFace: "Calibri", italic: true, align: "center",
  });

  s.addText("ThreadPoolTaskExecutor (core=5, max=20)  ·  @CircuitBreaker on each Feign call (Resilience4j)", {
    x: 0.5, y: 6.05, w: 9.0, h: 0.24,
    fontSize: 9.5, color: C.midGrey, fontFace: "Calibri", italic: true, align: "center",
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 9 — Key Data Structures
// ═══════════════════════════════════════════════════════════════════════════
function slide09_data() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "Key Data Structures");

  // Left: domain objects
  s.addShape(pptx.ShapeType.rect, {
    x: 0.5, y: 1.3, w: 4.3, h: 4.2,
    fill: { color: C.white }, line: { color: C.lightGrey, width: 1 },
  });
  s.addText("Domain Objects (Providers)", {
    x: 0.7, y: 1.38, w: 3.9, h: 0.34,
    fontSize: 13, bold: true, color: C.teal, fontFace: "Calibri",
  });
  const domainObjs = [
    ["Patient", "id · fiscalCode · name · dob · gender"],
    ["Condition", "icdCode · description · diagnosisDate · status (ACTIVE/RESOLVED)"],
    ["Allergy", "allergen · severity (MILD/SEVERE/ANAPHYLACTIC) · detectedDate"],
    ["Prescription", "drugName · atcCode · dosage · frequency · start/end dates"],
    ["TestOrder", "orderId · examCode · status (PENDING/PROCESSING/COMPLETED/ERROR)"],
    ["Measurement", "parameter · value · unit · refRange · anomalyFlag"],
    ["ImagingReport", "reportId · examType · date · findings · criticalFlag"],
  ];
  domainObjs.forEach(([name, desc], i) => {
    s.addText(name, {
      x: 0.7, y: 1.8 + i * 0.5, w: 1.3, h: 0.28,
      fontSize: 11, bold: true, color: C.teal, fontFace: "Calibri",
    });
    s.addText(desc, {
      x: 2.05, y: 1.8 + i * 0.5, w: 2.6, h: 0.38,
      fontSize: 9, color: C.darkGrey, fontFace: "Calibri",
    });
  });

  // Right: composed objects
  s.addShape(pptx.ShapeType.rect, {
    x: 5.2, y: 1.3, w: 4.3, h: 4.2,
    fill: { color: C.white }, line: { color: C.lightGrey, width: 1 },
  });
  s.addText("Composed Objects (Prosumers)", {
    x: 5.4, y: 1.38, w: 3.9, h: 0.34,
    fontSize: 13, bold: true, color: C.violet, fontFace: "Calibri",
  });
  const composedObjs = [
    ["DiagnosticBundle", "List<TestResult>  +  List<ImagingReport>"],
    ["ClinicalProfile", "Patient  +  List<Condition>  +  List<Allergy>  +  List<Prescription>"],
    ["RiskFlag", "type (DRUG_CONTRAINDICATION / CRITICAL_VALUE / ALLERGY / OTHER)  ·  severity (INFO/WARNING/CRITICAL)  ·  description"],
    ["FitnessReport", "outcome:  FIT  |  FIT_WITH_RESERVATION  |  UNFIT\npatientId  ·  generatedAt\nClinicalProfile  +  DiagnosticBundle\nList<RiskFlag>"],
  ];
  const heights = [0.5, 0.65, 0.82, 0.88];
  let cy = 1.8;
  composedObjs.forEach(([name, desc], i) => {
    s.addText(name, {
      x: 5.4, y: cy, w: 1.55, h: 0.28,
      fontSize: 11, bold: true, color: C.violet, fontFace: "Calibri",
    });
    s.addText(desc, {
      x: 7.0, y: cy, w: 2.35, h: heights[i],
      fontSize: 8.5, color: C.darkGrey, fontFace: "Calibri",
    });
    cy += heights[i] + 0.22;
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 10 — Use Cases Summary
// ═══════════════════════════════════════════════════════════════════════════
function slide10_usecases() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "Use Cases Summary");

  const rows = [
    ["UC", "Name", "Components", "Interaction"],
    ["UC-1", "Full Fitness\nAssessment", "Client → Gateway → Care Coordinator →\nClinical Agg + Diagnostic Agg → providers", "Parallel branches +\nsync barrier (allOf)"],
    ["UC-2", "Clinical History\nConsultation", "Client → Gateway → Clinical Aggregator →\nAnagrafe (SOAP) + Farmacia (REST)", "Synchronous\ncomposition"],
    ["UC-3", "Diagnostic Test\nOrder & Poll", "Client → Gateway → Diagnostic Aggregator →\nLaboratorio (async) + Imaging (async)", "Async request-reply\nwith polling/callback"],
    ["UC-4", "New Prescription\n(direct)", "Client → Gateway → Farmacia\n(no prosumer involved)", "Direct client→provider\nREST CRUD"],
  ];

  const colW = [0.7, 1.5, 4.5, 2.75];
  const colX = [0.5, 1.2, 2.7, 7.25];

  rows.forEach((row, ri) => {
    const h = ri === 0 ? 0.42 : 1.0;
    const y = ri === 0 ? 1.3 : 1.72 + (ri - 1) * 1.07;
    const isH = ri === 0;
    row.forEach((cell, ci) => {
      s.addShape(pptx.ShapeType.rect, {
        x: colX[ci], y, w: colW[ci], h,
        fill: { color: isH ? C.darkBlue : ri % 2 === 1 ? "EFF3F7" : C.white },
        line: { color: C.lightGrey, width: 1 },
      });
      s.addText(cell, {
        x: colX[ci] + 0.06, y, w: colW[ci] - 0.08, h,
        fontSize: isH ? 12 : ci === 0 ? 13 : ci === 3 ? 11 : 10.5,
        bold: isH || ci === 0,
        color: isH ? C.white : ci === 0 ? C.darkBlue : C.darkGrey,
        fontFace: "Calibri", valign: "middle",
      });
    });
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 11 — UC-1 Walkthrough
// ═══════════════════════════════════════════════════════════════════════════
function slide11_uc1() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "UC-1 Walkthrough", "Full Fitness Assessment — parallel orchestration");

  // Left: narrative steps
  const steps = [
    ["1", "Client sends GET /api/coordinator/patients/{id}/fitness through the API Gateway."],
    ["2", "Care Coordinator receives the request and immediately launches two concurrent tasks via CompletableFuture.supplyAsync()."],
    ["3a", "Clinical Aggregator calls Anagrafe SOAP (getPatientById, getMedicalHistory, getAllergies) then Farmacia REST (getPrescriptions). Returns ClinicalProfile."],
    ["3b", "Diagnostic Aggregator calls Laboratorio REST (POST /tests/orders → polls status until COMPLETED → GET result) and Imaging REST (GET /reports). Returns DiagnosticBundle."],
    ["4", "Care Coordinator blocks on CompletableFuture.allOf(diag, clin).join() — synchronisation barrier."],
    ["5", "RiskAnalyzer cross-references prescriptions vs. lab values vs. allergies, emits RiskFlag objects."],
    ["6", "FitnessReport (outcome: FIT / FIT_WITH_RESERVATION / UNFIT + list of RiskFlags) returned to client."],
  ];

  steps.forEach((st, i) => {
    s.addShape(pptx.ShapeType.ellipse, {
      x: 0.5, y: 1.35 + i * 0.66, w: 0.4, h: 0.36,
      fill: { color: i < 2 ? C.darkBlue : i < 4 ? C.violet : C.teal },
      line: { type: "none" },
    });
    s.addText(st[0], {
      x: 0.5, y: 1.35 + i * 0.66, w: 0.4, h: 0.36,
      fontSize: 9, bold: true, color: C.white, fontFace: "Calibri", align: "center", valign: "middle",
    });
    s.addText(st[1], {
      x: 0.98, y: 1.37 + i * 0.66, w: 5.3, h: 0.52,
      fontSize: 10, color: C.darkGrey, fontFace: "Calibri",
    });
  });

  // Right: compact flow diagram
  function fb(label, x, y, w, h, fill, border, tc, fsize) {
    s.addShape(pptx.ShapeType.roundRect, {
      x, y, w, h,
      fill: { color: fill }, line: { color: border, width: 1.2 }, rectRadius: 0.04,
    });
    s.addText(label, {
      x, y, w, h, fontSize: fsize || 9, bold: true, color: tc,
      fontFace: "Calibri", align: "center", valign: "middle",
    });
  }
  function fa(x1, y1, x2, y2) {
    s.addShape(pptx.ShapeType.line, {
      x: x1, y: y1, w: x2 - x1, h: y2 - y1,
      line: { color: C.midGrey, width: 1.2, endArrowType: "arrow" },
    });
  }

  fb("Client", 7.0, 1.35, 1.2, 0.36, C.blueBg, "6A9EC4", C.darkBlue, 10);
  fa(7.6, 1.71, 7.6, 1.98);
  fb("Gateway", 7.0, 1.98, 1.2, 0.34, "D0DCE8", "6A9EC4", C.darkBlue, 10);
  fa(7.6, 2.32, 7.6, 2.58);
  fb("Care Coordinator", 6.7, 2.58, 1.8, 0.36, C.darkBlue, C.darkBlue, C.white, 9);
  // fork
  fa(7.6, 2.94, 6.9, 3.22);
  fa(7.6, 2.94, 8.3, 3.22);
  fb("Clinical Agg.", 6.1, 3.22, 1.6, 0.34, C.violetBg, C.violet, C.violet, 9);
  fb("Diagnostic Agg.", 7.6, 3.22, 1.8, 0.34, C.tealBg, C.teal, C.teal, 9);
  // join
  fa(6.9, 3.56, 7.6, 3.82);
  fa(8.5, 3.56, 7.6, 3.82);
  fb("allOf().join()", 7.0, 3.82, 1.2, 0.34, C.darkBlue, C.darkBlue, C.white, 9);
  fa(7.6, 4.16, 7.6, 4.42);
  fb("RiskAnalyzer", 7.0, 4.42, 1.2, 0.34, C.amberBg, C.amber, C.darkBlue, 9);
  fa(7.6, 4.76, 7.6, 5.0);
  fb("FitnessReport", 7.0, 5.0, 1.2, 0.34, C.tealBg, C.teal, C.teal, 9);
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 12 — UC-3 Walkthrough
// ═══════════════════════════════════════════════════════════════════════════
function slide12_uc3() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "UC-3 Walkthrough", "Diagnostic Test Order & Monitoring — asynchronous request-reply");

  const phases = [
    {
      label: "Phase 1 — Order",
      color: C.teal,
      steps: [
        "Client POSTs to /api/diagnostic/patients/{id}/order with panelCode.",
        "Diagnostic Aggregator forwards to Laboratorio: POST /tests/orders → 202 Accepted + orderId.",
        "Aggregator registers a callback URL on Laboratorio (POST /tests/orders/{id}/callback).",
        "Aggregator encodes trackingId = Base64URL(patientId:panelCode:orderId) and returns 202 + trackingId to client.",
      ],
    },
    {
      label: "Phase 2 — Background Processing",
      color: C.violet,
      steps: [
        "Laboratorio processes the sample asynchronously (@Async thread) — simulated with configurable Thread.sleep.",
        "On completion Laboratorio POSTs result to the registered callback URL on Diagnostic Aggregator.",
        "Imaging reports are retrieved (GET /patients/{id}/reports) independently.",
      ],
    },
    {
      label: "Phase 3 — Result Retrieval",
      color: C.darkBlue,
      steps: [
        "Client polls: GET /api/diagnostic/tracking/{trackingId}/status.",
        "Aggregator decodes trackingId → orderId, queries Laboratorio for current status, returns it.",
        "When status = COMPLETED: GET /api/diagnostic/tracking/{trackingId}/result.",
        "Aggregator fetches lab result + imaging reports, assembles and returns DiagnosticBundle.",
      ],
    },
  ];

  let y = 1.3;
  phases.forEach((ph) => {
    s.addShape(pptx.ShapeType.rect, {
      x: 0.5, y, w: 9.0, h: 0.32,
      fill: { color: ph.color }, line: { type: "none" },
    });
    s.addText(ph.label, {
      x: 0.65, y, w: 8.7, h: 0.32,
      fontSize: 12, bold: true, color: C.white, fontFace: "Calibri", valign: "middle",
    });
    y += 0.32;
    s.addShape(pptx.ShapeType.rect, {
      x: 0.5, y, w: 9.0, h: ph.steps.length * 0.38 + 0.1,
      fill: { color: C.white }, line: { color: C.lightGrey, width: 1 },
    });
    ph.steps.forEach((step, si) => {
      s.addText("• " + step, {
        x: 0.7, y: y + 0.05 + si * 0.38, w: 8.6, h: 0.35,
        fontSize: 10.5, color: C.darkGrey, fontFace: "Calibri",
      });
    });
    y += ph.steps.length * 0.38 + 0.2;
  });

  s.addText("Key insight: trackingId is stateless — any Diagnostic Aggregator replica can handle any polling request without shared in-memory state.", {
    x: 0.5, y: y + 0.05, w: 9.0, h: 0.28,
    fontSize: 10, color: C.midGrey, fontFace: "Calibri", italic: true,
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 13 — Technology Stack
// ═══════════════════════════════════════════════════════════════════════════
function slide13_stack() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "Technology Stack");

  s.addShape(pptx.ShapeType.rect, {
    x: 0.5, y: 1.3, w: 4.3, h: 4.2,
    fill: { color: C.white }, line: { color: C.lightGrey, width: 1 },
  });
  s.addText("Frameworks & Libraries", {
    x: 0.7, y: 1.38, w: 3.9, h: 0.34,
    fontSize: 13, bold: true, color: C.darkBlue, fontFace: "Calibri",
  });
  const fw = [
    ["Java 17", "LTS runtime (pinned)"],
    ["Spring Boot 3.2.x", "Service base + embedded Tomcat"],
    ["Spring Cloud 2023.0.x", "Eureka, Gateway, Config, OpenFeign, LoadBalancer"],
    ["Apache CXF 4.0.x", "SOAP contract-first — Anagrafe service + JAX-WS client"],
    ["Resilience4j 2.2.x", "Circuit breaker on Care Coordinator Feign calls"],
    ["springdoc-openapi 2.3.x", "Swagger UI on all REST services"],
    ["Spring Data JPA", "Repository layer over MySQL"],
    ["Apache Maven 3.9.x", "Build — independent per-service POMs (no parent)"],
  ];
  fw.forEach(([name, desc], i) => {
    s.addText(name, {
      x: 0.7, y: 1.82 + i * 0.46, w: 2.0, h: 0.36,
      fontSize: 10.5, bold: true, color: C.teal, fontFace: "Calibri",
    });
    s.addText(desc, {
      x: 2.72, y: 1.82 + i * 0.46, w: 1.9, h: 0.36,
      fontSize: 9.5, color: C.darkGrey, fontFace: "Calibri",
    });
  });

  s.addShape(pptx.ShapeType.rect, {
    x: 5.2, y: 1.3, w: 4.3, h: 4.2,
    fill: { color: C.white }, line: { color: C.lightGrey, width: 1 },
  });
  s.addText("Infrastructure", {
    x: 5.4, y: 1.38, w: 3.9, h: 0.34,
    fontSize: 13, bold: true, color: C.darkBlue, fontFace: "Calibri",
  });
  const infra = [
    ["MySQL 8", "Relational persistence, one schema per provider"],
    ["Docker + Compose", "Single-command full-stack deployment"],
    ["Netflix Eureka", "Service registry, self-registration, health checks"],
    ["Spring Cloud Gateway", "API Gateway, lb:// routing, path rewriting"],
    ["Spring Cloud Config", "Centralised externalised config from git repo"],
    ["Spring Cloud OpenFeign", "REST client with Eureka name resolution"],
    ["@Async / ThreadPool", "Non-blocking processing in Laboratorio & Imaging"],
    ["CompletableFuture", "Fork-join parallel orchestration in Care Coordinator"],
  ];
  infra.forEach(([name, desc], i) => {
    s.addText(name, {
      x: 5.4, y: 1.82 + i * 0.46, w: 1.85, h: 0.36,
      fontSize: 10.5, bold: true, color: C.violet, fontFace: "Calibri",
    });
    s.addText(desc, {
      x: 7.28, y: 1.82 + i * 0.46, w: 2.05, h: 0.36,
      fontSize: 9.5, color: C.darkGrey, fontFace: "Calibri",
    });
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// SLIDE 14 — Constraint Coverage
// ═══════════════════════════════════════════════════════════════════════════
function slide14_constraints() {
  const s = pptx.addSlide();
  s.background = { color: C.lightBg };
  addTitle(s, "Constraint Coverage", "Mapping of all 13 exam-track requirements");

  const rows = [
    ["#", "Requirement", "Covered by"],
    ["1", "REST + SOAP + microservices", "Anagrafe → SOAP (CXF); all others → REST"],
    ["2", "≥3 providers, ≥2 prosumers, ≥1 client, gateway", "4 providers, 3 prosumers, 1 web client, API Gateway :9000"],
    ["3", "Client→prosumer; prosumer→≥2 providers", "UC-1/2/3 → prosumers; each prosumer calls 2 providers"],
    ["4", "Async service + ≥2 prosumers parallel + sync barrier", "Laboratorio @Async; UC-1 Care Coordinator allOf().join()"],
    ["5", "≥3 client→prosumer interactions + direct client→provider", "UC-1/2/3 via prosumers; UC-4 direct to Farmacia"],
    ["6", "Architecture diagram + sequence diagrams per scenario", "System Overview slide + UC-1/UC-3 walkthrough slides"],
    ["7", "Textual documentation", "specifica_applicativa.md + architettura_tecnologica.md"],
    ["8", "Source comments, WSDL, OpenAPI/Swagger", "Javadoc on all classes; WSDL for Anagrafe; Swagger UI on REST"],
    ["9", "Setup README", "README.md with docker-compose up instructions"],
    ["10", "Spring Boot + Docker", "All 10 microservices containerised, single docker-compose.yml"],
    ["11", "Maven build", "Independent Maven project per service (no parent POM)"],
    ["12", "Maven archetype (optional)", "Standard per-service structure replicated consistently"],
    ["13", "Multi-instance + LB + discovery, motivated", "Anagrafe/Lab/Imaging/DiagAgg scale; Eureka + lb:// routing"],
  ];

  const colW = [0.45, 3.65, 5.4];
  const colX = [0.5, 0.95, 4.6];

  rows.forEach((row, ri) => {
    const h = ri === 0 ? 0.34 : 0.38;
    const y = ri === 0 ? 1.28 : 1.62 + (ri - 1) * 0.4;
    const isH = ri === 0;
    row.forEach((cell, ci) => {
      s.addShape(pptx.ShapeType.rect, {
        x: colX[ci], y, w: colW[ci], h,
        fill: { color: isH ? C.darkBlue : ri % 2 === 0 ? C.white : "EFF3F7" },
        line: { color: C.lightGrey, width: 0.75 },
      });
      s.addText(cell, {
        x: colX[ci] + 0.05, y, w: colW[ci] - 0.07, h,
        fontSize: isH ? 11.5 : ci === 0 ? 12 : 9.5,
        bold: isH || ci === 0,
        color: isH ? C.white : ci === 0 ? C.darkBlue : C.darkGrey,
        fontFace: "Calibri", valign: "middle",
      });
    });
  });
}

// ═══════════════════════════════════════════════════════════════════════════
// BUILD DECK
// ═══════════════════════════════════════════════════════════════════════════
slide01_title();

sectionDivider(
  "Block 1 — Domain, Architecture & Infrastructure",
  "Presenter 1",
  "Domain & Motivation · System Overview · Infrastructure · Load Balancing"
);
slide02_domain();
slide03_overview();
slide04_infra();
slide05_lb();

sectionDivider(
  "Block 2 — Providers, Prosumers & Data Model",
  "Presenter 2",
  "Service Providers · Prosumers · Parallel Execution · Data Structures"
);
slide06_providers();
slide07_prosumers();
slide08_parallel();
slide09_data();

sectionDivider(
  "Block 3 — Use Cases, Stack & Coverage",
  "Presenter 3",
  "Use Cases · UC-1 Walkthrough · UC-3 Walkthrough · Tech Stack · Constraints"
);
slide10_usecases();
slide11_uc1();
slide12_uc3();
slide13_stack();
slide14_constraints();

// ── Write file ─────────────────────────────────────────────────────────────
pptx.writeFile({ fileName: "C:/Users/edoar/Documents/GitHub/healthsoa/docs/HealthSOA_Presentation.pptx" })
  .then(() => console.log("Done → docs/HealthSOA_Presentation.pptx"))
  .catch(err => { console.error(err); process.exit(1); });
