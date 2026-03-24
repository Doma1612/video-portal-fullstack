
import { writable } from 'svelte/store';

export const userRole = writable(9);

// null when not logged in; otherwise { id, username, role }
export const user = writable(null);
