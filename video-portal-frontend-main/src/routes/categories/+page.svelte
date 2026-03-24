<script>
	import Footer from '../Footer.svelte';
    import Navbar from '../Navbar.svelte';
    import { userRole } from '$lib/store';
    import LoginComponent from '../LoginComponent.svelte';
	import { apiUrl } from '$lib/api';
	import { onMount } from 'svelte';
	let name = '';
	let updateName = '';
	let unterkategorienName = '';
	let kategorieID = 0;
	let id = 0;
	let idU = 0;
	let kategorie = '';
	let kategorien = [];

	const newCategory = {
		name: '',
		unterkategorien: []
	};

	const newUnterkategorie = {
		name,
		thema: {
			id,
			name,
			unterkategorien: []
		}
	};

	const updateCategory = {
		id,
		name,
		unterkategorien: []
	};

	function onSubcategoryCategoryChange() {
		const selectedCategoryId = Number(idU);
		const selectedKategorie = kategorien.find((k) => Number(k.id) === selectedCategoryId);

		if (selectedKategorie) {
			newUnterkategorie.thema.id = selectedKategorie.id;
			newUnterkategorie.thema.name = selectedKategorie.name;
		}
	}
	async function getAllKategorien() {
		const response = await fetch(apiUrl('/themes'), { credentials: 'include' });
		const responseData = await response.json();
		kategorien = responseData;
		console.log(kategorien);
	}

	onMount(() => {
		getAllKategorien();
	});

	async function kategorieHinzufuegen() {
		newCategory.name = name;
		console.log(newCategory);

		if (!name) {
		window.alert('Please enter a category name.');
      return;
		}
        


		try {
			const response = await fetch(
				apiUrl('/themes'),
				{
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					credentials: 'include',
					body: JSON.stringify({ name: newCategory.name })
				}
			);

			console.log(response);

			if (response.ok) {
				const created = await response.json();
				console.log('Category created successfully');
				window.alert('Category created successfully.');
				name = '';
				kategorien = [...kategorien, created];
			} else {
				if (response.status === 409) {
					window.alert('This category name already exists.');
					return;
				}
				console.log('Error creating category');
				window.alert('Error creating category.');
			}
		} catch (error) {
			console.error('kategorieHinzufuegen() - error occurred: ', error);
			window.alert('An error occurred. Please try again.');
		}
	}

	async function kategorieAendern() {
		updateCategory.name = updateName;
		updateCategory.id = id;
		console.log("Category was renamed to " + updateName);
		if (!updateName || !id ) {
      window.alert('Please select a category and enter a new name.');
      return;
		}
		try {
			const response = await fetch(
				apiUrl(`/themes/${id}`),
				{
					method: 'PUT',
					headers: {
						'Content-Type': 'application/json'
					},
					credentials: 'include',
					body: JSON.stringify({ name: updateCategory.name })
				}
			);

			console.log(response);

			if (response.ok) {
				console.log('Category updated successfully');
				window.alert('Category updated successfully.');
				updateName = '';
				await getAllKategorien();
			} else {
				if (response.status === 409) {
					window.alert('This category name already exists.');
					return;
				}
				console.log('Error updating category');
				window.alert('Error updating category.');
			}
		} catch (error) {
			console.error('kategorieAendern() - error occurred: ', error);
			window.alert('Error updating category.');
		}
	}

	async function kategorieLoeschen() {
		console.log(kategorie);

		if(!kategorieID) {
			window.alert("Please select a category first.")
		}
		try {
			const response = await fetch(
				apiUrl(`/themes/${kategorieID}`),
				{
					method: 'DELETE',
					headers: {
						'Content-Type': 'application/json'
					},
					credentials: 'include'
				}
			);

			console.log(response);

			if (response.ok) {
				console.log('Category deleted successfully');
				window.alert("Category deleted successfully.");
				await getAllKategorien();
			} else {
				if(response.status === 409) {
					window.alert("This category already has subcategories and cannot be deleted.")
				}
				console.log('Error deleting category');
				window.alert("Error deleting category.");
			}

			
		} catch (error) {
			console.error('kategorieLoeschen() - error occurred: ', error);
			window.alert("Error deleting category.");
		}
	}

	async function unterkategorieHinzufuegen() {
        
		
		newUnterkategorie.name = unterkategorienName;
		newUnterkategorie.thema.id = Number(idU);
		
		

		if (!idU) {
			window.alert('Please select a category first.');
			return;
		}

		if (!unterkategorienName) {
			window.alert('Please enter a subcategory name.');
			return;
		}
		try {
			const response = await fetch(
				apiUrl('/subcategories'),
				{
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					credentials: 'include',
					body: JSON.stringify({ name: newUnterkategorie.name, themeId: Number(newUnterkategorie.thema.id) })
				}
			);

			console.log(response);

			if (response.ok) {
				console.log('Subcategory created successfully');
				window.alert("Subcategory created successfully.");
				unterkategorienName = '';
				await getAllKategorien();
			} else {
				console.log('Error creating subcategory');
				window.alert("Error creating subcategory.");
			}
		} catch (error) {
			console.error('unterkategorieHinzufuegen() - error occurred: ', error);
			window.alert("Error creating subcategory.");
		}
	}
</script>



<!-- Kategorie anlegen -->


<!-- Kategorie anlegen -->
{#if $userRole == 1}
<Navbar />
<div class="w-full sm:w-full md:w-3/4 lg:w-1/2 xl:w-3/4 mx-auto my-4">
  <div class="bg-gray-100 p-4 rounded-md border border-dotted border-gray-400">
	<h3 class="text-xl font-bold text-gray-600 mb-4">Add Category</h3>
    <div class="flex items-center">
      <input
        class="flex-grow form-input p-2 border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500 mr-2"
        bind:value={name}
		placeholder="Enter a new category"
      />
      <button
        class="p-2 bg-blue-500 text-white hover:bg-blue-600 rounded-lg "
		on:click={kategorieHinzufuegen}>Add
      </button>
    </div>
    <br /><br />

    <!-- Kategorien ändern-->
	<h3 class="text-xl font-bold text-gray-600 mb-4">Rename Category</h3>
    <div class="flex items-center">
      <select
        name="kategorie"
        bind:value={id}
        on:click={() => getAllKategorien()}
        id="kategorie"
        class="flex-grow bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 p-2.5 mr-2"
      >
		<option value="" disabled>Select a category</option>
        {#each kategorien as Kategorie}
          <option value={Kategorie.id}>{Kategorie.name}</option>
        {/each}
      </select>
      <input
        class="flex-grow form-input p-2 border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500 mr-2"
        bind:value={updateName}
		placeholder="Enter a new category name"
      />
      <button
        class="p-2 bg-blue-500 text-white hover:bg-blue-600 rounded-lg"
		on:click={kategorieAendern}>Rename
      </button>
    </div>

    <!-- Kategorie löschen -->
    <br /><br />
	<h3 class="text-xl font-bold text-gray-600 mb-4">Delete Category</h3>
    <div class="flex items-center">
      <select
        name="kategorie"
        bind:value={kategorieID}
        on:click={() => getAllKategorien()}
        on:change
        id="kategorie"
        class="flex-grow bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 p-2.5 mr-2"
      >
		<option value="" disabled>Select a category</option>
        {#each kategorien as Kategorie}
          <option value={Kategorie.id}>{Kategorie.name}</option>
        {/each}
      </select>
      <button
        class="p-2 bg-red-500 text-white hover:bg-red-700 rounded-lg"
		on:click={kategorieLoeschen}>Delete
      </button>
    </div>

    <br /><br />
	<h3 class="text-xl font-bold text-gray-600 mb-4">Add Subcategory</h3>
    <div class="flex items-center">
      <select
        name="kategorie"
        bind:value={idU}
        on:click={() => getAllKategorien()}
				on:change={onSubcategoryCategoryChange}
        id="kategorie"
        class="flex-grow bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 p-2.5 mr-2"
      >
		<option value="" disabled>Select a category</option>
        {#each kategorien as KategorieU}
          <option value={KategorieU.id}>{KategorieU.name}</option>
        {/each}
      </select>
      <input
        class="w-1/2 form-input p-2 border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500 mr-2"
        bind:value={unterkategorienName}
		placeholder="Enter a subcategory"
      />
      <button
        class="p-2 bg-green-500 text-white hover:bg-green-700 rounded-lg"
		on:click={unterkategorieHinzufuegen}>Create
      </button>
    </div>
  </div>
</div>
<Footer />
{:else}
<LoginComponent benötigteRolle = "Admin account required" />
{/if}

