<script>
	//import { enhance } from '$app/forms'
	import { page } from '$app/stores';
	import { onMount } from 'svelte';
	import '../styles/app.css';
	import { refreshSession } from '$lib/auth';

	onMount(() => {
		// Populate stores from existing backend session (HttpOnly cookie)
		refreshSession();
	});
</script>

<!-- Quelle: Login Projekt Svelte Prof. Dr Klaus Dallmöller -->
<svelte:head>
	<title>DLJP Video Portal</title>
</svelte:head>

<nav>
	{#if $page.data.user}
		<a href="/kunden">Test</a>
		<a href="/konten">Test</a>
	{/if}
</nav>

<main>
	<slot />
</main>
