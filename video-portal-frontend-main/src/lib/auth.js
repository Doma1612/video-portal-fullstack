import { apiFetch, apiJson } from './api';
import { user, userRole } from './store';

const GUEST_ROLE = 9;

export async function refreshSession() {
	try {
		// Ensure CSRF cookie exists early (needed for POST/PUT/DELETE)
		await apiJson('/auth/csrf', {
			credentials: 'include'
		});

		const sessionUser = await apiJson('/auth/me', {
			credentials: 'include'
		});
		user.set(sessionUser);
		userRole.set(sessionUser.role);
		return sessionUser;
	} catch {
		user.set(null);
		userRole.set(GUEST_ROLE);
		return null;
	}
}

export async function logout() {
	try {
		await apiFetch('/auth/logout', {
			method: 'POST',
			credentials: 'include'
		});
	} catch {
		// ignore network errors; we still clear local state
	} finally {
		user.set(null);
		userRole.set(GUEST_ROLE);
	}
}
