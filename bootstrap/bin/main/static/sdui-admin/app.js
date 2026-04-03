const screenRoot = document.getElementById("screen-root");
const titleElement = document.getElementById("screen-title");
const subtitleElement = document.getElementById("screen-subtitle");
const screenIdElement = document.getElementById("screen-id");
const generatedAtElement = document.getElementById("generated-at");
const refreshStateElement = document.getElementById("refresh-state");
const refreshToggleButton = document.getElementById("refresh-toggle");
const blockTemplate = document.getElementById("block-template");

let refreshTimerId = null;
let autoRefreshEnabled = true;
let refreshIntervalSeconds = 10;

refreshToggleButton.addEventListener("click", () => {
    autoRefreshEnabled = !autoRefreshEnabled;
    updateRefreshState();

    if (autoRefreshEnabled) {
        scheduleAutoRefresh();
        showToast("자동 새로고침을 다시 시작했습니다.");
        return;
    }

    clearRefreshTimer();
    showToast("자동 새로고침을 일시정지했습니다.");
});

async function bootstrapScreen(options = {}) {
    try {
        const response = await fetch("/api/v1/admin/screens/core-banking-overview", { cache: "no-store" });
        const screen = await response.json();
        renderScreen(screen);
        if (!options.silent) {
            updateRefreshState();
        }
    } catch (error) {
        showToast("화면 정의를 불러오지 못했습니다.");
        console.error(error);
    }
}

function renderScreen(screen) {
    titleElement.textContent = screen.title;
    subtitleElement.textContent = screen.subtitle;
    screenIdElement.textContent = screen.screenId;
    generatedAtElement.textContent = formatDateTime(screen.generatedAt);
    refreshIntervalSeconds = screen.refresh?.intervalSeconds ?? 10;
    scheduleAutoRefresh();
    updateRefreshState();

    screenRoot.replaceChildren();

    screen.blocks.forEach((block) => {
        const blockNode = blockTemplate.content.firstElementChild.cloneNode(true);
        blockNode.dataset.component = block.component;
        blockNode.style.gridColumn = `span ${block.span ?? 12}`;
        blockNode.querySelector(".panel-eyebrow").textContent = block.component;
        blockNode.querySelector(".panel-title").textContent = block.title;
        blockNode.querySelector(".panel-description").textContent = block.description;
        const body = blockNode.querySelector(".panel-body");
        renderBlockBody(block, body);
        screenRoot.appendChild(blockNode);
    });
}

function renderBlockBody(block, container) {
    switch (block.component) {
        case "metric-grid":
            renderMetricGrid(block.items, container);
            break;
        case "status-grid":
            renderStatusGrid(block.items, container);
            break;
        case "table":
            renderTable(block.columns, block.rows, container);
            break;
        case "timeline":
            renderTimeline(block.items, container);
            break;
        case "fact-list":
            renderFactList(block.items, container);
            break;
        case "action-list":
            renderActionList(block.items, container);
            break;
        case "log-list":
            renderLogList(block.items, container);
            break;
        default:
            container.textContent = "지원하지 않는 컴포넌트입니다.";
    }
}

function renderMetricGrid(items, container) {
    const grid = document.createElement("div");
    grid.className = "metric-grid";

    items.forEach((item) => {
        const card = document.createElement("article");
        card.className = "metric-card";
        card.dataset.emphasis = item.emphasis;
        card.innerHTML = `
            <div>
                <p class="metric-key">${item.key}</p>
                <h3>${item.label}</h3>
            </div>
            <div class="metric-value">${item.value}</div>
            <span class="metric-description">${item.description}</span>
        `;
        grid.appendChild(card);
    });

    container.appendChild(grid);
}

function renderStatusGrid(items, container) {
    const grid = document.createElement("div");
    grid.className = "status-grid";

    items.forEach((item) => {
        const card = document.createElement("article");
        card.className = "status-card";
        card.dataset.state = item.state;
        card.innerHTML = `
            <div class="status-row">
                <strong>${item.label}</strong>
                <span class="state-chip">${item.state}</span>
            </div>
            <p class="status-summary">${item.summary}</p>
            <p class="status-detail">${item.detail}</p>
        `;
        grid.appendChild(card);
    });

    container.appendChild(grid);
}

function renderTable(columns, rows, container) {
    const shell = document.createElement("div");
    shell.className = "table-shell";

    const table = document.createElement("table");
    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");
    columns.forEach((column) => {
        const th = document.createElement("th");
        th.textContent = column;
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);

    const tbody = document.createElement("tbody");
    rows.forEach((row) => {
        const tr = document.createElement("tr");
        row.forEach((cell) => {
            const td = document.createElement("td");
            td.textContent = cell;
            tr.appendChild(td);
        });
        tbody.appendChild(tr);
    });

    table.append(thead, tbody);
    shell.appendChild(table);
    container.appendChild(shell);
}

function renderTimeline(items, container) {
    const list = document.createElement("div");
    list.className = "timeline-list";

    items.forEach((item) => {
        const node = document.createElement("article");
        node.className = "timeline-item";
        node.innerHTML = `
            <div class="timeline-head">
                <p class="timeline-eyebrow">${item.eyebrow}</p>
                <span class="state-chip">${item.status}</span>
            </div>
            <div class="timeline-title">${item.title}</div>
            <span class="timeline-meta">${item.meta}</span>
            <span class="timeline-time">${formatDateTime(item.timestamp)}</span>
        `;
        list.appendChild(node);
    });

    container.appendChild(list);
}

function renderFactList(items, container) {
    const list = document.createElement("div");
    list.className = "fact-list";

    items.forEach((item) => {
        const node = document.createElement("article");
        node.className = "fact-item";
        node.innerHTML = `
            <span class="fact-label">${item.label}</span>
            <strong class="fact-value">${item.value}</strong>
        `;
        list.appendChild(node);
    });

    container.appendChild(list);
}

function renderActionList(items, container) {
    const list = document.createElement("div");
    list.className = "action-list";

    items.forEach((item) => {
        const node = document.createElement("article");
        node.className = "action-card";

        const tone = document.createElement("p");
        tone.className = "action-tone";
        tone.textContent = item.actionType;

        const title = document.createElement("h3");
        title.textContent = item.label;

        const description = document.createElement("p");
        description.className = "action-description";
        description.textContent = item.description;

        const button = document.createElement("button");
        button.className = "primary-button";
        button.dataset.tone = item.tone;
        button.textContent = item.label;
        button.addEventListener("click", () => performAction(item));

        node.append(tone, title, description, button);
        list.appendChild(node);
    });

    container.appendChild(list);
}

function renderLogList(items, container) {
    const list = document.createElement("div");
    list.className = "log-list";

    items.forEach((item) => {
        const node = document.createElement("article");
        node.className = "log-item";
        node.dataset.level = item.level;
        node.innerHTML = `
            <div class="log-head">
                <span class="log-level">${item.level}</span>
                <span class="log-category">${item.category}</span>
            </div>
            <p class="log-message">${item.message}</p>
            <div class="log-meta">
                <span>${formatDateTime(item.occurredAt)}</span>
                <span>${item.traceId}</span>
            </div>
        `;
        list.appendChild(node);
    });

    container.appendChild(list);
}

async function performAction(action) {
    if (action.actionType === "refresh-screen") {
        await bootstrapScreen({ silent: true });
        showToast("화면 정의를 다시 불러왔습니다.");
        return;
    }

    if (action.actionType === "toggle-auto-refresh") {
        refreshToggleButton.click();
        return;
    }

    if (action.actionType === "open-link") {
        window.open(action.target, "_blank", "noopener,noreferrer");
        return;
    }

    showToast(`지원하지 않는 액션입니다: ${action.actionType}`);
}

function scheduleAutoRefresh() {
    clearRefreshTimer();
    if (!autoRefreshEnabled) {
        return;
    }

    refreshTimerId = window.setInterval(() => {
        bootstrapScreen({ silent: true });
    }, refreshIntervalSeconds * 1000);
}

function clearRefreshTimer() {
    if (refreshTimerId) {
        clearInterval(refreshTimerId);
        refreshTimerId = null;
    }
}

function updateRefreshState() {
    refreshStateElement.textContent = autoRefreshEnabled
        ? `${refreshIntervalSeconds}초 주기 자동 갱신`
        : "일시정지";
    refreshToggleButton.textContent = autoRefreshEnabled
        ? "자동 새로고침 일시정지"
        : "자동 새로고침 다시 시작";
}

function formatDateTime(value) {
    try {
        return new Intl.DateTimeFormat("ko-KR", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit"
        }).format(new Date(value));
    } catch (error) {
        return value;
    }
}

function showToast(message) {
    const existing = document.querySelector(".toast");
    if (existing) {
        existing.remove();
    }

    const toast = document.createElement("div");
    toast.className = "toast";
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => toast.remove(), 2200);
}

bootstrapScreen();
