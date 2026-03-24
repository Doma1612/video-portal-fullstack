export const API_BASE = '/api/v1';

function getCookieValue(name) {
	if (typeof document === 'undefined') return null;
	const cookies = document.cookie ? document.cookie.split(';') : [];
	for (const raw of cookies) {
		const [k, ...rest] = raw.trim().split('=');
		if (k === name) {
			return decodeURIComponent(rest.join('='));
		}
	}
	return null;
}

function shouldSendCsrf(method) {
	const m = (method ?? 'GET').toUpperCase();
	return !['GET', 'HEAD', 'OPTIONS', 'TRACE'].includes(m);
}

let csrfInitPromise = null;

async function ensureCsrfCookie() {
	// Only relevant in the browser.
	if (typeof document === 'undefined') return;
	if (getCookieValue('XSRF-TOKEN')) return;
	if (csrfInitPromise) return csrfInitPromise;

	csrfInitPromise = fetch(apiUrl('/auth/csrf'), {
		credentials: 'include',
		headers: { Accept: 'application/json' }
	})
		.catch(() => {
			// ignore; request will fail with 403 if CSRF truly can't be obtained
		})
		.finally(() => {
			csrfInitPromise = null;
		});

	return csrfInitPromise;
}

export function apiUrl(path) {
	if (!path) return API_BASE;
	const normalized = path.startsWith('/') ? path : `/${path}`;
	return `${API_BASE}${normalized}`;
}

export async function apiFetch(path, options = {}) {
	const method = options.method ?? 'GET';
	const headers = {
		...(options.headers ?? {})
	};

	if (shouldSendCsrf(method)) {
		await ensureCsrfCookie();
		const token = getCookieValue('XSRF-TOKEN');
		if (token && !headers['X-XSRF-TOKEN']) {
			headers['X-XSRF-TOKEN'] = token;
		}
	}

	return fetch(apiUrl(path), {
		...options,
		credentials: options.credentials ?? 'include',
		headers: {
			...headers
		}
	});
}

export async function apiJson(path, options = {}) {
	const res = await apiFetch(path, {
		...options,
		headers: {
			Accept: 'application/json',
			...(options.headers ?? {})
		}
	});

	if (!res.ok) {
		const text = await res.text().catch(() => '');
		throw new Error(text || `Request failed (${res.status})`);
	}

	return res.json();
}
