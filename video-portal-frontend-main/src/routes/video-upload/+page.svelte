<script>
	import Footer from '../Footer.svelte';
import Navbar from '../Navbar.svelte';
import { userRole } from '$lib/store';
import LoginComponent from '../LoginComponent.svelte';
import { goto } from '$app/navigation';
	import { apiFetch, apiJson } from '$lib/api';
	import { onMount } from 'svelte';
	let titel = '';
	let beschreibung = '';
	let selectedCategoryId = null;
	let stichwoerter = '';
	let kategorie = '';
	let unterkategorie = '';
	let file;
	//let unterkategorien = 'Test';
	let unterkategorien = [];
	let dateiEndung = '';
	/** @type {FileList}*/
	let files;
	let kategorien = [];
	let videoSrc = '';

	async function getAllKategorien() {
		try {
			kategorien = await apiJson('/themes');
			console.log(kategorien);
		} catch (error) {
			console.error('Failed to load categories', error);
			kategorien = [];
		}
	}

	async function dateiEndungHolen(datei) {
		
		return datei.split('.').pop().toLowerCase();
	}

	async function getAllUntrkategorien() {
		try {
			unterkategorien = await apiJson('/subcategories');
			console.log(unterkategorien);
		} catch (error) {
			console.error('Failed to load subcategories', error);
			unterkategorien = [];
		}
	}

	onMount(() => {
		getAllKategorien();
		getAllUntrkategorien();
	});
	async function videoUpload() {
		console.log(titel + ' ' + beschreibung + ' ' + kategorie + ' ' + stichwoerter + ' '+ dateiEndung);
        if (!titel || !beschreibung || !kategorie || !stichwoerter) {
      window.alert('Please fill in all required fields.');
      return;
		}
		if (!file) {
			window.alert('Please select a file.');
			return;
		}

		window.alert('Uploading video...');
		const form = new FormData();
		form.append('file', file);
		form.append('title', titel);
		form.append('description', beschreibung);
		form.append('themeId', String(kategorie));
		form.append('keywords', stichwoerter);
		if (unterkategorie) {
			form.append('subcategoryIds', String(unterkategorie));
		}

		try {
			const response = await apiFetch('/videos', {
				method: 'POST',
				body: form
			});

			if (response.ok) {
				console.log('Video uploaded');
				setTimeout(() => {
					goto('/');
				}, 2000);
				window.alert('Video uploaded successfully.');
			} else {
				const msg = await response.text().catch(() => '');
				console.error('Video upload error', msg);
				window.alert('Upload failed. Please try again.');
			}
		} catch (error) {
			console.error('Upload error', error);
			window.alert('Upload failed. Please try again.');
		}
	}

	function updateSelectedCategoryId() {
		const selectedKategorie = kategorien.find((kat) => String(kat.id) === String(kategorie));

		selectedCategoryId = selectedKategorie && selectedKategorie.id;
		console.log(selectedCategoryId);
	}

	async function handleFileChange(event) {
		file = event.target.files[0];

		if (file) {
			const reader = new FileReader();

			reader.onload = () => {
				videoSrc = reader.result;
			};
			reader.readAsDataURL(file);

			dateiEndung = await dateiEndungHolen(file.name);
            console.log('Dateiendung :', dateiEndung);
			//convertedFile = new Uint8Array(event.target.files[0]);
			//console.log(convertedFile);
			//reader.readAsBinaryString(file);
			console.log(videoSrc);
		}
	}
</script>
{#if $userRole == 1}
<div>
	<Navbar />
</div>

<div class="w-full sm:w-5/6 md:w-3/4 lg:w-2/3 xl:w-1/2 mx-auto my-4 border border-dotted border-gray-40 rounded-md bg-gray-100">
	<h1 class="text-3xl font-bold text-gray-600 text-center">Upload Video</h1>
	

	<form id="videoForm" enctype="multipart/form-data" class="my-4">
		<div class="my-4">
			<input
				type="file"
				bind:files
				on:change={handleFileChange}
				id="file"
				accept="video/*"
				class="form-input p-2 border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500"
				required
			/>
			<button type="button" class="p-2 bg-blue-500 text-white hover:bg-blue-700 rounded-lg mt-2 w-full sm:w-auto"
				>Upload video</button
			>
		</div>

		<h3 class="text-xl font-bold text-gray-600 my-2">Title</h3>
		<input
			type="text"
			placeholder="Title"
			bind:value={titel}
			class="form-input w-full p-2 border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500"
		/>
		<h3 class="text-xl font-bold text-gray-600 my-2">Description</h3>
		<input
			type="text"
			placeholder="Description"
			bind:value={beschreibung}
			class="form-input w-full p-2 border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500"
		/>

		<h3 class="text-xl font-bold text-gray-600 my-2">Select a category:</h3>

		<select
			name="kategorie"
			bind:value={kategorie}
			on:change={() => updateSelectedCategoryId()}
			id="kategorie"
			class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
		>
			<option value="" disabled selected>Select a category</option>
			{#each kategorien as Kategorie}
				<option value={Kategorie.id}>{Kategorie.name}</option>
			{/each}
		</select>

		<h3 class="text-xl font-bold text-gray-600 my-2">Select a subcategory:</h3>

		<select
			name="unterkategorie"
			bind:value={unterkategorie}
			id="kategorie"
			class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
		>
			<option value="" disabled selected>Select a subcategory</option>
			{#each unterkategorien as uk}
				{#if uk.thema.id == selectedCategoryId}
					<option value={uk.id}>{uk.name}</option>
				{/if}
			{/each}
		</select>
		<h3 class="text-xl font-bold text-gray-600 my-2">Keywords</h3>
		<input
			type="text"
			bind:value={stichwoerter}
			placeholder="Keywords (comma-separated)"
			class="form-input w-full p-2 border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500"
		/>

		<button type="button"
			on:click={videoUpload}
			class="w-full p-2 bg-blue-500 text-white hover:bg-blue-700 rounded-lg mt-2 sm:w-auto"
			>Upload</button
		>
	</form>
</div>
<Footer />
{:else}
<LoginComponent benötigteRolle = "Admin account required" />
{/if}
