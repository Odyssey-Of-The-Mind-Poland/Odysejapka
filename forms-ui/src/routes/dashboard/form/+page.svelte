<script lang="ts">
    import {z} from 'zod';
    import {writable} from 'svelte/store';

    export const formSchema = z.object({
        name: z.string().min(1, 'Name is required'),
        email: z.string().email('Invalid email'),
    });

    let formData = writable({name: '', email: ''});
    let formErrors = writable<{ [key: string]: string }>({});

    async function handleSubmit() {
        const result = formSchema.safeParse($formData);

        if (!result.success) {
            const errors = result.error.flatten().fieldErrors;
            formErrors.set({
                name: errors.name?.[0] || '',
                email: errors.email?.[0] || '',
            });
        } else {
            formErrors.set({});
            const res = await fetch('/api/submit', {
                method: 'POST',
                body: JSON.stringify(result.data),
                headers: {'Content-Type': 'application/json'},
            });

            if (res.ok) {
                alert('Form submitted!');
            } else {
                alert('Submission failed.');
            }
        }
    }
</script>

<div class="space-y-4 max-w-md">
    <div>
        <label class="block mb-1 font-medium" for="name">Name</label>
        <input
                bind:value={$formData.name}
                class="w-full border p-2 rounded"
                id="name"
        />
        {#if $formErrors.name}
            <p class="text-red-500 text-sm">{$formErrors.name}</p>
        {/if}
    </div>

    <div>
        <label class="block mb-1 font-medium" for="email">Email</label>
        <input
                bind:value={$formData.email}
                class="w-full border p-2 rounded"
                id="email"
                type="email"
        />
        {#if $formErrors.email}
            <p class="text-red-500 text-sm">{$formErrors.email}</p>
        {/if}
    </div>

    <button
            class="bg-black text-white px-4 py-2 rounded hover:bg-gray-800"
            on:click={handleSubmit}
    >
        Submit
    </button>
</div>
