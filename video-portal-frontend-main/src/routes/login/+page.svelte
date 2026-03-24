
<script>
  let errormessage = '';

    import Navbar from "../Navbar.svelte";
    import Footer from "../Footer.svelte";
    import { goto } from '$app/navigation';

    // Store-Variable Login für Upload etc.
    import { user, userRole } from '$lib/store';
    import { apiFetch } from '$lib/api';

  let userdaten = {
    username: '',
    password: ''
  };

  // @ts-ignore
  async function login() {
    try {
      const response = await apiFetch('/auth/login', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(userdaten)
	});

      if (response.ok) {
        /* wenn Login erfolgreich war */
        const data = await response.json(); 

        // Session ist serverseitig (HttpOnly Cookie). Im Frontend halten wir nur den UI-State.
        user.set(data);
        userRole.set(data.role);
        goto('/');

      } else {
        errormessage = 'Login failed. Please try again.';
        setTimeout(() => {
        errormessage = '';
        }, 3000);

		userdaten = { username: '', password: '' };
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
      <h1 class="text-3xl block text-center font-semibold">Login</h1>
      <hr class="mt-3" />
      <div class="mt-3">
        <label for="username" class="block text-base mb-2">Username</label>
        <input
          type="text"
          bind:value={userdaten.username}
          id="username"
          class="border w-full text-base px-2 py-1 focus:outline-none focus:ring-0 focus:border-gray-600"
          placeholder="Enter username..."
        />
      </div>
      <div class="mt-3">
        <label for="passwort" class="block text-base mb-2">Password</label>
        <input
          type="password"
          autocomplete="current-password"
          bind:value={userdaten.password}
          id="passwort"
          class="border w-full text-base px-2 py-1 focus:outline-none focus:ring-0 focus:border-gray-600"
          placeholder="Enter password..."
        />
      </div>
      <div class="mt-5">
        <button
          type="submit"
          class="border-2 border-indigo-700 bg-blue-500 text-white py-1 w-full rounded-mg hover:bg-transparent hover:text-blue-500 font-semibold"
          on:click={login}>Login</button
        >
      </div>
    </div>
    {#if errormessage}
      <div class="fixed top-0 left-0 right-0 bottom-0 flex items-center justify-center z-50">
        <div class="bg-indigo-200 p-4 rounded-md border border-gray-300">
          <p>{errormessage}</p>
        </div>
      </div>
    {/if}
  </div>
</body>
<Footer />

