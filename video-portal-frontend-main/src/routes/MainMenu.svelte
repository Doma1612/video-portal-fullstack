<script>
	import Panel from './Panel.svelte';

	//let anzahlAufrufe = 0;

	//async function aufrufCounter() {
	//	anzahlAufrufe++;

	//Endpunkt/{videoID}

	//const form = document.querySelector('form');

	// const formData = new FormData();
	//const fileField = document.querySelector('input[type="file"]');

	//formData.append("video", fileField.files[0]);
	//}
</script>
<body class="flex justify-center items-center min-h-screen bg-blue-100">

    <div class="p-4 md:p-8 lg:p-12 xl:p-20 mr-24">

        <div class="max-w-2xl mx-auto flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4 lg:space-x-12">
            <Panel title="Video Search" linkTo="/topics" beschreibung="Enter a search term and find videos by topic and description."></Panel>
            <Panel title="Video Upload" linkTo="/video-upload" beschreibung="Upload videos in common formats. An admin account is required."></Panel>
            
        </div>
        <br><br>

        <div class="max-w-2xl mx-auto flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4 lg:space-x-12">
            <Panel title="Manage Categories" linkTo="/categories" beschreibung="Create, edit, and delete categories and add subcategories."></Panel>
            <Panel title="Contact" linkTo="/contact" beschreibung="Questions or feedback? Send us a message via the contact form."></Panel>
        </div>
    </div>

    <div class="p-4 md:p-8 lg:p-12 xl:p-20">
        
    </div>
	</body>


