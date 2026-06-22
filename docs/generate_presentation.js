const PptxGenJS = require("pptxgenjs");

const pptx = new PptxGenJS();
pptx.layout = "LAYOUT_16x9";

// Colors
const DARK_BLUE = "1F4E79";
const DARK_GREY = "595959";
const WHITE = "FFFFFF";
const LIGHT_BG = "F5F8FA";
const VIOLET = "6B4F9E";
const TEAL = "1A7A6E";
const LIGHT_GREY = "E8EEF2";

function titleSlide() {
  const slide = pptx.addSlide();
  slide.background = { color: DARK_BLUE };

  slide.addShape(pptx.ShapeType.rect, {
    x: 0, y: 2.8, w: 10, h: 0.08,
    fill: { color: TEAL }
  });

  slide.addText("HealthSOA", {
    x: 0.8, y: 1.0, w: 8.4, h: 1.2,
    fontSize: 54, bold: true, color: WHITE,
    fontFace: "Calibri", align: "center"
  });

  slide.addText("Hospital Platform for Pre-Procedure Clinical Assessment", {
    x: 0.8, y: 2.3, w: 8.4, h: 0.6,
    fontSize: 22, color: "CADCFC",
    fontFace: "Calibri", align: "center", italic: true
  });

  slide.addText("Service-Oriented Software Engineering  ·  A.A. 2025/2026\nUniversità degli Studi dell'Aquila  ·  DISIM", {
    x: 0.8, y: 3.5, w: 8.4, h: 0.9,
    fontSize: 14, color: "A0B8CC",
    fontFace: "Calibri", align: "center"
  });
}

function slide2_domain() {
  const slide = pptx.addSlide();
  slide.background = { color: LIGHT_BG };

  slide.addText("Domain & Motivation", {
    x: 0.5, y: 0.3, w: 9, h: 0.7,
    fontSize: 36, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  const bullets = [
    { icon: "🏥", title: "Legacy SOAP Integration", body: "Anagrafe patient registry exposes a SOAP/JAX-WS endpoint; modern services must interoperate with it via contract-first CXF client." },
    { icon: "⏱", title: "Asynchronous Lab & Imaging", body: "Diagnostic exams take minutes to hours — polling + webhook callback prevent blocking the caller (Laboratorio & Imaging services)." },
    { icon: "⚡", title: "Parallel Branch Orchestration", body: "Care Coordinator concurrently calls Clinical and Diagnostic aggregators (CompletableFuture.allOf) to minimise latency." },
    { icon: "📈", title: "Scale Under Peak Load", body: "Laboratorio and Imaging can run multiple replicas; Eureka + lb:// routing distributes traffic with no code changes." }
  ];

  bullets.forEach((b, i) => {
    const y = 1.2 + i * 1.1;
    slide.addShape(pptx.ShapeType.rect, {
      x: 0.5, y: y, w: 9, h: 0.95,
      fill: { color: WHITE }, line: { color: LIGHT_GREY, width: 1 }
    });
    slide.addShape(pptx.ShapeType.rect, {
      x: 0.5, y: y, w: 0.08, h: 0.95,
      fill: { color: i % 2 === 0 ? TEAL : VIOLET }
    });
    slide.addText(b.title, {
      x: 0.75, y: y + 0.05, w: 8.5, h: 0.3,
      fontSize: 14, bold: true, color: DARK_BLUE, fontFace: "Calibri"
    });
    slide.addText(b.body, {
      x: 0.75, y: y + 0.38, w: 8.5, h: 0.5,
      fontSize: 11, color: DARK_GREY, fontFace: "Calibri"
    });
  });
}

function slide3_overview() {
  const slide = pptx.addSlide();
  slide.background = { color: LIGHT_BG };

  slide.addText("System Overview", {
    x: 0.5, y: 0.3, w: 9, h: 0.7,
    fontSize: 36, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  // Layer definitions: [label, color, y, w, x, textColor]
  const layers = [
    { label: "Client  (Browser / curl)", color: "D9E8F5", x: 3.2, y: 1.1, w: 3.6 },
    { label: "API Gateway  :9000", color: "B8CFE4", x: 3.2, y: 2.0, w: 3.6 },
    { label: "Prosumers", color: "D6C8EE", x: 0.5, y: 2.9, w: 4.2, violet: true },
    { label: "Providers", color: "C3E0DC", x: 5.3, y: 2.9, w: 4.2, teal: true },
    { label: "MySQL  :3306", color: "E0E0E0", x: 3.2, y: 3.8, w: 3.6 },
  ];

  layers.forEach(l => {
    slide.addShape(pptx.ShapeType.roundRect, {
      x: l.x, y: l.y, w: l.w, h: 0.6,
      fill: { color: l.color },
      line: { color: l.violet ? VIOLET : l.teal ? TEAL : "A0A0A0", width: 1.5 },
      rectRadius: 0.05
    });
    slide.addText(l.label, {
      x: l.x, y: l.y, w: l.w, h: 0.6,
      fontSize: 12, bold: true,
      color: l.violet ? VIOLET : l.teal ? TEAL : DARK_BLUE,
      fontFace: "Calibri", align: "center", valign: "middle"
    });
  });

  // Prosumer detail boxes
  const prosumers = ["Care\nCoordinator\n:9203", "Clinical\nAggregator\n:9202", "Diagnostic\nAggregator\n:9201"];
  prosumers.forEach((p, i) => {
    slide.addShape(pptx.ShapeType.rect, {
      x: 0.55 + i * 1.4, y: 3.65, w: 1.3, h: 0.7,
      fill: { color: "EDE5F7" }, line: { color: VIOLET, width: 1 }
    });
    slide.addText(p, {
      x: 0.55 + i * 1.4, y: 3.65, w: 1.3, h: 0.7,
      fontSize: 8, color: VIOLET, align: "center", valign: "middle", fontFace: "Calibri"
    });
  });

  // Provider detail boxes
  const providers = ["Anagrafe\nSOAP :9101", "Farmacia\n:9103", "Laboratorio\n:9102", "Imaging\n:9104"];
  providers.forEach((p, i) => {
    slide.addShape(pptx.ShapeType.rect, {
      x: 5.35 + i * 1.03, y: 3.65, w: 0.98, h: 0.7,
      fill: { color: "D5EDEA" }, line: { color: TEAL, width: 1 }
    });
    slide.addText(p, {
      x: 5.35 + i * 1.03, y: 3.65, w: 0.98, h: 0.7,
      fontSize: 8, color: TEAL, align: "center", valign: "middle", fontFace: "Calibri"
    });
  });

  // Arrows (vertical lines + arrowheads via text)
  const arrows = [
    { x: 5.0, y1: 1.7, y2: 2.0 },
    { x: 5.0, y1: 2.6, y2: 2.9 },
    { x: 5.0, y1: 4.35, y2: 4.5 },
  ];
  arrows.forEach(a => {
    slide.addShape(pptx.ShapeType.line, {
      x: a.x, y: a.y1, w: 0, h: a.y2 - a.y1,
      line: { color: "808080", width: 1.5, endArrowType: "arrow" }
    });
  });

  // Legend
  const legend = [
    { color: "D6C8EE", border: VIOLET, label: "Prosumer" },
    { color: "C3E0DC", border: TEAL, label: "Provider" },
    { color: "E0E0E0", border: "A0A0A0", label: "Infrastructure / DB" },
  ];
  legend.forEach((l, i) => {
    slide.addShape(pptx.ShapeType.rect, {
      x: 0.5 + i * 2.5, y: 5.0, w: 0.25, h: 0.22,
      fill: { color: l.color }, line: { color: l.border, width: 1 }
    });
    slide.addText(l.label, {
      x: 0.82 + i * 2.5, y: 5.0, w: 1.8, h: 0.22,
      fontSize: 10, color: DARK_GREY, fontFace: "Calibri"
    });
  });
}

function slide4_infra() {
  const slide = pptx.addSlide();
  slide.background = { color: LIGHT_BG };

  slide.addText("Infrastructure Services", {
    x: 0.5, y: 0.3, w: 9, h: 0.7,
    fontSize: 36, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  const items = [
    {
      title: "Config Server  :8888",
      bullets: [
        "Centralises all service properties in a git-backed repository",
        "Each service reads its config on startup via spring.config.import",
        "Runtime refresh supported via POST /actuator/refresh + @RefreshScope"
      ]
    },
    {
      title: "Discovery Server — Eureka  :8761",
      bullets: [
        "All services self-register on startup (@EnableDiscoveryClient)",
        "Enables service-name resolution for Feign clients and Gateway lb:// URIs",
        "Health dashboard at http://localhost:8761"
      ]
    },
    {
      title: "API Gateway  :9000",
      bullets: [
        "Single entry point — routes requests to the correct service by path prefix",
        "Uses lb://service-name URIs; load balances across all healthy replicas",
        "Path filters (StripPrefix) rewrite paths before forwarding"
      ]
    }
  ];

  items.forEach((item, i) => {
    const y = 1.2 + i * 1.4;
    slide.addShape(pptx.ShapeType.rect, {
      x: 0.5, y: y, w: 9, h: 1.25,
      fill: { color: WHITE }, line: { color: LIGHT_GREY, width: 1 }
    });
    slide.addText(item.title, {
      x: 0.7, y: y + 0.08, w: 8.6, h: 0.32,
      fontSize: 15, bold: true, color: DARK_BLUE, fontFace: "Calibri"
    });
    item.bullets.forEach((b, j) => {
      slide.addText("• " + b, {
        x: 0.9, y: y + 0.42 + j * 0.26, w: 8.2, h: 0.24,
        fontSize: 11, color: DARK_GREY, fontFace: "Calibri"
      });
    });
  });
}

function slide5_providers() {
  const slide = pptx.addSlide();
  slide.background = { color: LIGHT_BG };

  slide.addText("Service Providers", {
    x: 0.5, y: 0.3, w: 9, h: 0.7,
    fontSize: 36, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  const rows = [
    ["Service", "Protocol", "Port", "Responsibility"],
    ["Anagrafe", "SOAP (CXF)", "9101", "Patient registry — demographics, conditions, allergies"],
    ["Laboratorio", "REST async", "9102", "Lab test orders, async processing, result retrieval"],
    ["Farmacia", "REST", "9103", "Pharmaceutical prescriptions — CRUD by patient"],
    ["Imaging", "REST async", "9104", "Radiology/imaging orders, async processing, reports"],
  ];

  const colW = [2.0, 1.6, 0.9, 4.7];
  const colX = [0.5, 2.5, 4.1, 5.0];

  rows.forEach((row, ri) => {
    const y = 1.2 + ri * 0.78;
    row.forEach((cell, ci) => {
      const isHeader = ri === 0;
      slide.addShape(pptx.ShapeType.rect, {
        x: colX[ci], y: y, w: colW[ci], h: 0.72,
        fill: { color: isHeader ? TEAL : ci === 0 ? "D5EDEA" : WHITE },
        line: { color: isHeader ? TEAL : LIGHT_GREY, width: 1 }
      });
      slide.addText(cell, {
        x: colX[ci] + 0.08, y: y, w: colW[ci] - 0.1, h: 0.72,
        fontSize: isHeader ? 13 : 12,
        bold: isHeader,
        color: isHeader ? WHITE : ci === 0 ? TEAL : DARK_GREY,
        fontFace: "Calibri", valign: "middle"
      });
    });
  });

  slide.addText("All providers register with Eureka — no hardcoded addresses.", {
    x: 0.5, y: 5.1, w: 9, h: 0.3,
    fontSize: 11, color: DARK_GREY, fontFace: "Calibri", italic: true
  });
}

function slide6_prosumers() {
  const slide = pptx.addSlide();
  slide.background = { color: LIGHT_BG };

  slide.addText("Service Prosumers", {
    x: 0.5, y: 0.3, w: 9, h: 0.7,
    fontSize: 36, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  const rows = [
    ["Service", "Port", "Providers Consumed", "Use Case"],
    ["Clinical Aggregator", "9202", "Anagrafe (SOAP) + Farmacia (REST)", "UC-2: Build clinical profile"],
    ["Diagnostic Aggregator", "9201", "Laboratorio + Imaging (REST)", "UC-3: Collect diagnostic bundle"],
    ["Care Coordinator", "9203", "Clinical Aggregator + Diagnostic Aggregator", "UC-1: Pre-procedure fitness report"],
  ];

  const colW = [2.1, 0.7, 3.6, 2.8];
  const colX = [0.5, 2.6, 3.3, 6.9];

  rows.forEach((row, ri) => {
    const y = 1.2 + ri * 1.0;
    row.forEach((cell, ci) => {
      const isHeader = ri === 0;
      slide.addShape(pptx.ShapeType.rect, {
        x: colX[ci], y: y, w: colW[ci], h: 0.9,
        fill: { color: isHeader ? VIOLET : ci === 0 ? "EDE5F7" : WHITE },
        line: { color: isHeader ? VIOLET : LIGHT_GREY, width: 1 }
      });
      slide.addText(cell, {
        x: colX[ci] + 0.08, y: y, w: colW[ci] - 0.1, h: 0.9,
        fontSize: isHeader ? 13 : 11,
        bold: isHeader,
        color: isHeader ? WHITE : ci === 0 ? VIOLET : DARK_GREY,
        fontFace: "Calibri", valign: "middle"
      });
    });
  });

  slide.addText("Prosumers call providers via OpenFeign (@FeignClient(name=...)) — no hardcoded URLs.", {
    x: 0.5, y: 5.0, w: 9, h: 0.3,
    fontSize: 11, color: DARK_GREY, fontFace: "Calibri", italic: true
  });
}

function slide7_parallel() {
  const slide = pptx.addSlide();
  slide.background = { color: LIGHT_BG };

  slide.addText("Parallel Execution & Synchronisation", {
    x: 0.5, y: 0.3, w: 9, h: 0.7,
    fontSize: 32, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  // Fork box
  slide.addShape(pptx.ShapeType.rect, {
    x: 4.1, y: 1.2, w: 1.8, h: 0.55,
    fill: { color: DARK_BLUE }, line: { color: DARK_BLUE, width: 1 }
  });
  slide.addText("Care Coordinator", {
    x: 4.1, y: 1.2, w: 1.8, h: 0.55,
    fontSize: 10, bold: true, color: WHITE, fontFace: "Calibri", align: "center", valign: "middle"
  });

  // Two branch lines going down
  slide.addShape(pptx.ShapeType.line, {
    x: 4.5, y: 1.75, w: -1.8, h: 0.5,
    line: { color: VIOLET, width: 2, endArrowType: "arrow" }
  });
  slide.addShape(pptx.ShapeType.line, {
    x: 5.5, y: 1.75, w: 1.8, h: 0.5,
    line: { color: TEAL, width: 2, endArrowType: "arrow" }
  });

  // supplyAsync label
  slide.addText("supplyAsync()", {
    x: 1.5, y: 1.78, w: 1.6, h: 0.3,
    fontSize: 9, color: VIOLET, fontFace: "Consolas", italic: true
  });
  slide.addText("supplyAsync()", {
    x: 6.9, y: 1.78, w: 1.6, h: 0.3,
    fontSize: 9, color: TEAL, fontFace: "Consolas", italic: true
  });

  // Branch boxes
  slide.addShape(pptx.ShapeType.rect, {
    x: 0.6, y: 2.35, w: 2.8, h: 0.7,
    fill: { color: "EDE5F7" }, line: { color: VIOLET, width: 1.5 }
  });
  slide.addText("getClinicalProfile()\nClinical Aggregator :9202", {
    x: 0.6, y: 2.35, w: 2.8, h: 0.7,
    fontSize: 11, color: VIOLET, fontFace: "Calibri", align: "center", valign: "middle"
  });

  slide.addShape(pptx.ShapeType.rect, {
    x: 6.6, y: 2.35, w: 2.8, h: 0.7,
    fill: { color: "D5EDEA" }, line: { color: TEAL, width: 1.5 }
  });
  slide.addText("getDiagnosticBundle()\nDiagnostic Aggregator :9201", {
    x: 6.6, y: 2.35, w: 2.8, h: 0.7,
    fontSize: 11, color: TEAL, fontFace: "Calibri", align: "center", valign: "middle"
  });

  // Lines converging to join
  slide.addShape(pptx.ShapeType.line, {
    x: 2.0, y: 3.05, w: 2.0, h: 0.5,
    line: { color: VIOLET, width: 2, endArrowType: "arrow" }
  });
  slide.addShape(pptx.ShapeType.line, {
    x: 8.0, y: 3.05, w: -2.0, h: 0.5,
    line: { color: TEAL, width: 2, endArrowType: "arrow" }
  });

  // allOf join barrier
  slide.addShape(pptx.ShapeType.rect, {
    x: 3.5, y: 3.6, w: 3.0, h: 0.6,
    fill: { color: DARK_BLUE }, line: { color: DARK_BLUE, width: 1 }
  });
  slide.addText("CompletableFuture.allOf().join()", {
    x: 3.5, y: 3.6, w: 3.0, h: 0.6,
    fontSize: 10, bold: true, color: WHITE, fontFace: "Consolas", align: "center", valign: "middle"
  });

  // Down to risk analysis
  slide.addShape(pptx.ShapeType.line, {
    x: 5.0, y: 4.2, w: 0, h: 0.4,
    line: { color: "808080", width: 2, endArrowType: "arrow" }
  });

  slide.addShape(pptx.ShapeType.rect, {
    x: 3.4, y: 4.65, w: 3.2, h: 0.55,
    fill: { color: "FFF3CD" }, line: { color: "B8860B", width: 1 }
  });
  slide.addText("Risk Analysis → FitnessReport", {
    x: 3.4, y: 4.65, w: 3.2, h: 0.55,
    fontSize: 12, color: DARK_BLUE, fontFace: "Calibri", align: "center", valign: "middle"
  });

  // Bottom note
  slide.addText("ThreadPoolTaskExecutor (core=5, max=20) · Circuit breaker on each Feign call (Resilience4j)", {
    x: 0.5, y: 5.35, w: 9, h: 0.28,
    fontSize: 10, color: DARK_GREY, fontFace: "Calibri", italic: true, align: "center"
  });
}

function slide8_loadbalancing() {
  const slide = pptx.addSlide();
  slide.background = { color: LIGHT_BG };

  slide.addText("Load Balancing & Scalability", {
    x: 0.5, y: 0.3, w: 9, h: 0.7,
    fontSize: 36, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  // Left column
  slide.addShape(pptx.ShapeType.rect, {
    x: 0.5, y: 1.15, w: 4.3, h: 3.6,
    fill: { color: WHITE }, line: { color: LIGHT_GREY, width: 1 }
  });
  slide.addText("Scalable Services", {
    x: 0.7, y: 1.25, w: 3.9, h: 0.4,
    fontSize: 15, bold: true, color: TEAL, fontFace: "Calibri"
  });

  const scalable = [
    "Laboratorio  :9102 — stateless, MySQL-backed",
    "Imaging  :9104 — stateless, MySQL-backed",
    "Anagrafe  :9101 — stateless, SOAP",
    "Farmacia  :9103 — stateless, MySQL-backed",
  ];
  scalable.forEach((s, i) => {
    slide.addText("• " + s, {
      x: 0.7, y: 1.75 + i * 0.45, w: 3.9, h: 0.38,
      fontSize: 11, color: DARK_GREY, fontFace: "Calibri"
    });
  });

  slide.addText("Single-instance (stateful / coordinator):", {
    x: 0.7, y: 3.65, w: 3.9, h: 0.3,
    fontSize: 12, bold: true, color: VIOLET, fontFace: "Calibri"
  });
  ["Diagnostic Aggregator — ConcurrentHashMap state", "Clinical Aggregator — SOAP port singleton", "Care Coordinator — orchestrator"].forEach((s, i) => {
    slide.addText("• " + s, {
      x: 0.7, y: 4.0 + i * 0.32, w: 3.9, h: 0.28,
      fontSize: 11, color: DARK_GREY, fontFace: "Calibri"
    });
  });

  // Right column
  slide.addShape(pptx.ShapeType.rect, {
    x: 5.2, y: 1.15, w: 4.3, h: 3.6,
    fill: { color: WHITE }, line: { color: LIGHT_GREY, width: 1 }
  });
  slide.addText("How It Works", {
    x: 5.4, y: 1.25, w: 3.9, h: 0.4,
    fontSize: 15, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  const how = [
    ["Eureka registry", "Every replica registers; Gateway + Feign query the registry at call time."],
    ["lb:// routing", "Gateway routes with load balancing — no IP addresses in config."],
    ["@FeignClient(name=...)", "Feign resolves the name via Eureka, picks a replica (round-robin)."],
    ["docker-compose --scale", "docker-compose up --scale laboratorio-service=3 to add replicas."],
  ];
  how.forEach((h, i) => {
    slide.addText(h[0], {
      x: 5.4, y: 1.75 + i * 0.8, w: 3.9, h: 0.28,
      fontSize: 12, bold: true, color: DARK_BLUE, fontFace: "Calibri"
    });
    slide.addText(h[1], {
      x: 5.4, y: 2.05 + i * 0.8, w: 3.9, h: 0.4,
      fontSize: 11, color: DARK_GREY, fontFace: "Calibri"
    });
  });
}

function slide9_usecases() {
  const slide = pptx.addSlide();
  slide.background = { color: LIGHT_BG };

  slide.addText("Use Cases Summary", {
    x: 0.5, y: 0.3, w: 9, h: 0.7,
    fontSize: 36, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  const rows = [
    ["UC", "Name", "Components", "Interaction"],
    ["UC-1", "Pre-Procedure Fitness Assessment", "Care Coordinator → Clinical Agg + Diagnostic Agg", "Parallel async orchestration"],
    ["UC-2", "Build Clinical Profile", "Clinical Aggregator → Anagrafe (SOAP) + Farmacia (REST)", "Synchronous composite"],
    ["UC-3", "Collect Diagnostic Bundle", "Diagnostic Aggregator → Laboratorio + Imaging", "Async polling + callback"],
    ["UC-4", "Manage Prescriptions", "Client → Gateway → Farmacia", "Direct REST CRUD"],
  ];

  const colW = [0.7, 2.5, 3.8, 2.5];
  const colX = [0.5, 1.2, 3.7, 7.5];

  rows.forEach((row, ri) => {
    const y = 1.2 + ri * 0.85;
    row.forEach((cell, ci) => {
      const isHeader = ri === 0;
      slide.addShape(pptx.ShapeType.rect, {
        x: colX[ci], y: y, w: colW[ci], h: 0.78,
        fill: { color: isHeader ? DARK_BLUE : ri % 2 === 0 ? "F0F4F8" : WHITE },
        line: { color: LIGHT_GREY, width: 1 }
      });
      slide.addText(cell, {
        x: colX[ci] + 0.06, y: y, w: colW[ci] - 0.08, h: 0.78,
        fontSize: isHeader ? 12 : 11,
        bold: isHeader || ci === 0,
        color: isHeader ? WHITE : ci === 0 ? DARK_BLUE : DARK_GREY,
        fontFace: "Calibri", valign: "middle"
      });
    });
  });
}

function slide10_stack() {
  const slide = pptx.addSlide();
  slide.background = { color: LIGHT_BG };

  slide.addText("Technology Stack", {
    x: 0.5, y: 0.3, w: 9, h: 0.7,
    fontSize: 36, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  // Left column — Frameworks
  slide.addShape(pptx.ShapeType.rect, {
    x: 0.5, y: 1.15, w: 4.3, h: 4.1,
    fill: { color: WHITE }, line: { color: LIGHT_GREY, width: 1 }
  });
  slide.addText("Frameworks & Libraries", {
    x: 0.7, y: 1.25, w: 3.9, h: 0.35,
    fontSize: 14, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  const frameworks = [
    ["Java 17", "LTS runtime"],
    ["Spring Boot 3.2.x", "Service foundation + embedded Tomcat"],
    ["Spring Cloud 2023.0.x", "Eureka, Gateway, Config, OpenFeign"],
    ["Apache CXF 4.0.x", "SOAP contract-first (Anagrafe + client)"],
    ["Resilience4j 2.2.x", "Circuit breaker on coordinator calls"],
    ["springdoc-openapi 2.3.x", "Swagger UI on all REST services"],
    ["Spring Data JPA", "Repository layer over MySQL"],
  ];
  frameworks.forEach((f, i) => {
    slide.addText(f[0], {
      x: 0.7, y: 1.72 + i * 0.48, w: 2.2, h: 0.38,
      fontSize: 11, bold: true, color: TEAL, fontFace: "Calibri"
    });
    slide.addText(f[1], {
      x: 2.9, y: 1.72 + i * 0.48, w: 1.8, h: 0.38,
      fontSize: 10, color: DARK_GREY, fontFace: "Calibri"
    });
  });

  // Right column — Infrastructure
  slide.addShape(pptx.ShapeType.rect, {
    x: 5.2, y: 1.15, w: 4.3, h: 4.1,
    fill: { color: WHITE }, line: { color: LIGHT_GREY, width: 1 }
  });
  slide.addText("Infrastructure", {
    x: 5.4, y: 1.25, w: 3.9, h: 0.35,
    fontSize: 14, bold: true, color: DARK_BLUE, fontFace: "Calibri"
  });

  const infra = [
    ["MySQL 8", "Relational persistence (per-service DB)"],
    ["Docker + Compose", "Single-command deployment"],
    ["Apache Maven 3.9.x", "Build — independent per-service POMs"],
    ["Spring @Async", "Non-blocking lab/imaging processing"],
    ["CompletableFuture", "Fork-join in Care Coordinator"],
    ["Git config repo", "Externalised properties (healthsoa-properties-repository)"],
  ];
  infra.forEach((f, i) => {
    slide.addText(f[0], {
      x: 5.4, y: 1.72 + i * 0.53, w: 1.9, h: 0.42,
      fontSize: 11, bold: true, color: VIOLET, fontFace: "Calibri"
    });
    slide.addText(f[1], {
      x: 7.3, y: 1.72 + i * 0.53, w: 1.9, h: 0.42,
      fontSize: 10, color: DARK_GREY, fontFace: "Calibri"
    });
  });
}

// Build all slides
titleSlide();
slide2_domain();
slide3_overview();
slide4_infra();
slide5_providers();
slide6_prosumers();
slide7_parallel();
slide8_loadbalancing();
slide9_usecases();
slide10_stack();

pptx.writeFile({ fileName: "C:/Users/edoar/Documents/GitHub/healthsoa/docs/HealthSOA_Presentation.pptx" })
  .then(() => console.log("Done: docs/HealthSOA_Presentation.pptx"))
  .catch(err => { console.error(err); process.exit(1); });
