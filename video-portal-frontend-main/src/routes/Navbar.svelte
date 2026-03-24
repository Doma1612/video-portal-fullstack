<script>
	import logo from '$lib/Logo.svg';
	import { user } from '$lib/store';
	import { logout } from '$lib/auth';
</script>

<div>
	<nav class="bg-white-500">
		<div class="max-w-5xl mx-auto flex justify-between items-center">
			<a href="/"> <img src={logo} alt="Logo" class="w-40 h-22" /></a>
			<div class="flex items-center space-x-20">
				<ul class="list-none flex space-x-20">
					<li><a href="/" class="text-black font-bold">Home</a></li>
					<li><a href="/topics" class="text-black font-bold">Video Search</a></li>
					<li><a href="/video-upload" class="text-black font-bold">Upload Video</a></li>
					<li><a href="/categories" class="text-black font-bold">Categories</a></li>
				</ul>
			</div>
			{#if $user}
				<div class="flex items-center space-x-3 ml-6">
					<span class="text-black font-semibold">{$user.username}</span>
					<button
						class="bg-gray-100 hover:bg-blue-100 text-gray-800 font-semibold py-2 px-4 border border-gray-400 rounded shadow"
						on:click={() => logout()}
					>
						Logout
					</button>
				</div>
			{:else}
				<div class="ml-6">
					<!--Quelle: https://v1.tailwindcss.com/components/buttons-->
					<button
						><a
							href="/register"
							class="bg-gray-100 hover:bg-blue-100 text-gray-800 font-semibold py-2 px-4 border border-gray-400 rounded shadow"
							>Register</a
						></button
					>
				</div>
				<div>
					<!--Quelle: https://v1.tailwindcss.com/components/buttons-->
					<button>
						<a
							href="/login"
							class="bg-gray-100 hover:bg-blue-100 text-gray-800 font-semibold py-2 px-4 border border-gray-400 rounded shadow"
							>Login</a>
					</button>
				</div>
			{/if}
		</div>
	</nav>
</div>
