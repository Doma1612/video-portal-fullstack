<script>
    import Footer from "../Footer.svelte";
import Navbar from "../Navbar.svelte";
import { goto } from '$app/navigation';
  import { apiFetch } from '$lib/api';
    let successmessage='';
    let error = '';

  let userdaten = {
    username: '',
    password: ''
  };

    
    async function registrieren() {

      if (!userdaten.username || !userdaten.password) {
      error = 'Username and password are required.';

      setTimeout(() => {
        error = '';
      }, 3000);
      return;
    } 

    try {
      const response = await apiFetch('/auth/register', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(userdaten)
	});
      if (response.ok) {
        /* wenn Registrierung erfolgreich war */
        successmessage = 'Registration successful. Redirecting to login...';

        setTimeout(() => {
        successmessage = '';
        goto('/login');
        }, 3000);
      } else {
		error = 'Registration failed. Please try again.';
		setTimeout(() => {
			error = '';
		}, 3000);
      }
    } catch (error) {
      console.error("An error occurred: ", error);
    }
  }

    
</script>
<Navbar />

<body>
    <div class="flex justify-center items-center h-screen bg-blue-100">
        <div class="w-96 p-6 shadow-lg bg-white rounded-md">
          <h1 class="text-3xl block text-center font-semibold">Register</h1>
            <hr class="mt-3">
            <div class="mt-3">
            <label for="username" class="block text-base mb-2">Username</label>
            <input type="text" bind:value={userdaten.username} id="username" class="border w-full text-base px-2 py-1 focus:outline-none focus:ring-0 focus:border-gray-600" placeholder="Choose a username...">
            </div>
            <div class="mt-3">
            <label for="passwort" class="block text-base mb-2">Password</label>
            <input type="password" autocomplete="new-password" id="passwort" bind:value={userdaten.password} class="border w-full text-base px-2 py-1 focus:outline-none focus:ring-0 focus:border-gray-600" placeholder="Choose a password...">
            </div>
            <div class="mt-5">
                <button
                type="submit"
                class="border-2 border-indigo-700 bg-blue-500 text-white py-1 w-full rounded-mg hover:bg-transparent hover:text-blue-500 font-semibold"
                on:click={registrieren}
            >Register</button>
            
            </div>
        </div>
        {#if successmessage}
      <div class="fixed top-0 left-0 right-0 bottom-0 flex items-center justify-center z-50">
        <div class="bg-indigo-200 p-4 rounded-md border border-gray-300">
          <p>{successmessage}</p>
        </div>
      </div>
    {/if}
    {#if error}
      <div class="fixed top-0 left-0 right-0 bottom-0 flex items-center justify-center z-50">
        <div class="bg-red-500 p-4 rounded-md border border-gray-300">
          <p>{error}</p>
        </div>
      </div>
    {/if}
    </div>
</body>
<Footer />
