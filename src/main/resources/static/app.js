const consoleNode = document.getElementById("console");
const tokenStateNode = document.getElementById("tokenState");
const serviceStatusNode = document.getElementById("serviceStatus");
const serviceTimestampNode = document.getElementById("serviceTimestamp");

const loginForm = document.getElementById("loginForm");
const userForm = document.getElementById("userForm");
const subscriptionForm = document.getElementById("subscriptionForm");
const eventForm = document.getElementById("eventForm");
const lookupForm = document.getElementById("lookupForm");
const preferencesForm = document.getElementById("preferencesForm");
const deliveryForm = document.getElementById("deliveryForm");

const pingButton = document.getElementById("pingButton");
const loadDemoButton = document.getElementById("loadDemoButton");
const clearTokenButton = document.getElementById("clearTokenButton");
const loadSubscriptionsButton = document.getElementById("loadSubscriptionsButton");
const loadNotificationsButton = document.getElementById("loadNotificationsButton");
const loadPreferencesButton = document.getElementById("loadPreferencesButton");
const loadDeliveryButton = document.getElementById("loadDeliveryButton");
const loadAttemptsButton = document.getElementById("loadAttemptsButton");
const retryDeliveryButton = document.getElementById("retryDeliveryButton");
const loadDeadLetterButton = document.getElementById("loadDeadLetterButton");

const TOKEN_KEY = "notification-platform-token";

function getToken() {
    return localStorage.getItem(TOKEN_KEY) || "";
}

function setToken(token) {
    if (token) {
        localStorage.setItem(TOKEN_KEY, token);
    } else {
        localStorage.removeItem(TOKEN_KEY);
    }
    tokenStateNode.textContent = token ? "token: loaded" : "token: missing";
}

function writeConsole(title, payload) {
    const formatted = typeof payload === "string"
        ? payload
        : JSON.stringify(payload, null, 2);
    consoleNode.textContent = `${title}\n\n${formatted}`;
}

async function apiFetch(path, options = {}) {
    const headers = new Headers(options.headers || {});
    headers.set("Content-Type", "application/json");

    const token = getToken();
    if (token) {
        headers.set("Authorization", `Bearer ${token}`);
    }

    const response = await fetch(path, {
        ...options,
        headers
    });

    const text = await response.text();
    const body = text ? safeJsonParse(text) : null;

    if (!response.ok) {
        const errorPayload = body || { status: response.status, message: text };
        writeConsole(`HTTP ${response.status} ${path}`, errorPayload);
        throw new Error(errorPayload.message || `Request failed: ${response.status}`);
    }

    writeConsole(`HTTP ${response.status} ${path}`, body || { ok: true });
    return body;
}

function safeJsonParse(text) {
    try {
        return JSON.parse(text);
    } catch (error) {
        return text;
    }
}

function syncUserId(userId) {
    document.getElementById("subscriptionUserId").value = userId;
    document.getElementById("lookupUserId").value = userId;
    document.getElementById("preferencesUserId").value = userId;
}

pingButton.addEventListener("click", async () => {
    try {
        const response = await fetch("/api/v1/ping");
        const body = await response.json();
        serviceStatusNode.textContent = body.status;
        serviceTimestampNode.textContent = body.timestamp;
        writeConsole("Ping", body);
    } catch (error) {
        serviceStatusNode.textContent = "offline";
        serviceTimestampNode.textContent = error.message;
        writeConsole("Ping error", error.message);
    }
});

loadDemoButton.addEventListener("click", () => {
    document.getElementById("username").value = "operator";
    document.getElementById("password").value = "operator";
    document.getElementById("subscriptionDestination").value = "demo@example.com";
    document.getElementById("subscriptionConditionJson").value = JSON.stringify({
        all: [
            { field: "severity", op: "eq", value: "CRITICAL" },
            { field: "service", op: "in", value: ["billing", "auth"] }
        ]
    }, null, 2);
    document.getElementById("eventExternalEventId").value = `demo-event-${Date.now()}`;
    document.getElementById("eventPayload").value = JSON.stringify({
        severity: "CRITICAL",
        service: "billing",
        message: "Demo event from frontend"
    }, null, 2);
    writeConsole("Demo", "Demo values loaded.");
});

clearTokenButton.addEventListener("click", () => {
    setToken("");
    writeConsole("Auth", "JWT удалён из localStorage.");
});

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const payload = {
        username: document.getElementById("username").value.trim(),
        password: document.getElementById("password").value
    };

    try {
        const body = await apiFetch("/auth/login", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        setToken(body.token || "");
    } catch (error) {
        setToken("");
    }
});

userForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const payload = {
        email: document.getElementById("userEmail").value.trim(),
        name: document.getElementById("userName").value.trim()
    };

    const body = await apiFetch("/api/users", {
        method: "POST",
        body: JSON.stringify(payload)
    });

    if (body && body.id) {
        syncUserId(body.id);
        document.getElementById("subscriptionDestination").value ||= body.email;
        document.getElementById("eventPayload").value = JSON.stringify({
            userId: body.id,
            email: body.email,
            name: body.name
        }, null, 2);
    }
});

subscriptionForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const payload = {
        userId: document.getElementById("subscriptionUserId").value.trim(),
        eventType: document.getElementById("subscriptionEventType").value,
        channel: document.getElementById("subscriptionChannel").value,
        destination: document.getElementById("subscriptionDestination").value.trim(),
        conditionJson: document.getElementById("subscriptionConditionJson").value.trim() || null
    };

    await apiFetch("/api/subscriptions", {
        method: "POST",
        body: JSON.stringify(payload)
    });
});

eventForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const rawPayload = document.getElementById("eventPayload").value.trim();
    const normalizedPayload = safeJsonParse(rawPayload);
    const payload = {
        type: document.getElementById("eventType").value,
        payload: typeof normalizedPayload === "string" ? normalizedPayload : JSON.stringify(normalizedPayload),
        source: document.getElementById("eventSource").value.trim() || "frontend-demo",
        producer: document.getElementById("eventProducer").value.trim() || "frontend-demo",
        externalEventId: document.getElementById("eventExternalEventId").value.trim() || `ui-${Date.now()}`
    };

    await apiFetch("/api/events", {
        method: "POST",
        body: JSON.stringify(payload)
    });
});

loadSubscriptionsButton.addEventListener("click", async () => {
    const userId = document.getElementById("lookupUserId").value.trim();
    await apiFetch(`/api/users/${userId}/subscriptions`, { method: "GET" });
});

loadNotificationsButton.addEventListener("click", async () => {
    const userId = document.getElementById("lookupUserId").value.trim();
    await apiFetch(`/api/users/${userId}/notifications`, { method: "GET" });
});

loadPreferencesButton.addEventListener("click", async () => {
    const userId = document.getElementById("lookupUserId").value.trim();
    await apiFetch(`/api/v1/users/${userId}/preferences`, { method: "GET" });
});

preferencesForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const userId = document.getElementById("preferencesUserId").value.trim();
    const channels = document.getElementById("allowedChannels").value
        .split(",")
        .map(value => value.trim())
        .filter(Boolean);
    await apiFetch(`/api/v1/users/${userId}/preferences`, {
        method: "PUT",
        body: JSON.stringify({
            allowedChannels: channels,
            preferredChannel: document.getElementById("preferredChannel").value,
            timezone: "UTC",
            digestMode: document.getElementById("digestMode").value
        })
    });
});

loadDeliveryButton.addEventListener("click", async () => {
    const id = document.getElementById("deliveryId").value.trim();
    await apiFetch(`/api/v1/deliveries/${id}`, { method: "GET" });
});

loadAttemptsButton.addEventListener("click", async () => {
    const id = document.getElementById("deliveryId").value.trim();
    await apiFetch(`/api/v1/deliveries/${id}/attempts`, { method: "GET" });
});

retryDeliveryButton.addEventListener("click", async () => {
    const id = document.getElementById("deliveryId").value.trim();
    await apiFetch(`/api/v1/deliveries/${id}/retry`, { method: "POST" });
});

loadDeadLetterButton.addEventListener("click", async () => {
    await apiFetch("/api/v1/admin/dead-letter", { method: "GET" });
});

lookupForm.addEventListener("submit", (event) => {
    event.preventDefault();
    loadNotificationsButton.click();
});

setToken(getToken());
document.getElementById("eventPayload").value = JSON.stringify({
    severity: "CRITICAL",
    service: "billing",
    message: "Hello from the built-in dashboard"
}, null, 2);
