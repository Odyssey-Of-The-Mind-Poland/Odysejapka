<script lang="ts">
	import { SignIn } from '@auth/sveltekit/components';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import * as Card from '$lib/components/ui/card';
	import { enhance } from '$app/forms';

	let { form } = $props();
</script>

<main class="flex min-h-screen flex-col items-center justify-center bg-muted/30">
	<div class="flex w-full max-w-md flex-col items-center gap-8 px-4">
		<div class="flex flex-col items-center gap-4">
			<img src="/logo.jpeg" alt="Odyseja" class="h-24 w-24 rounded-xl object-cover shadow-lg" />
			<div class="text-center">
				<h1 class="text-2xl font-bold tracking-tight">Odyseja Umysłu</h1>
				<p class="text-muted-foreground">Witaj w panelu administracyjnym</p>
			</div>
		</div>

		<Card.Root class="w-full">
			<Card.Header>
				<Card.Title>Zaloguj się</Card.Title>
				<Card.Description>Wybierz metodę logowania</Card.Description>
			</Card.Header>
			<Card.Content class="space-y-6">
				<!-- Auth0 -->
				<form method="POST" action="/signin" class="signInButton w-full">
					<input type="hidden" name="callbackUrl" value="/dashboard" />
					<Button type="submit" slot="submitButton" variant="secondary" class="w-full">
						Zaloguj przez Auth0
					</Button>
				</form>
				<div class="relative">
					<div class="absolute inset-0 flex items-center">
						<span class="w-full border-t"></span>
					</div>
					<div class="relative flex justify-center text-xs uppercase">
						<span class="bg-card px-2 text-muted-foreground">Lub</span>
					</div>
				</div>

				<!-- Email/password -->
				<form method="POST" use:enhance class="space-y-4">
					{#if form?.error}
						<div class="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-600 dark:border-red-900 dark:bg-red-950/50 dark:text-red-400">
							{form.error}
						</div>
					{/if}

					<div class="space-y-2">
						<Label for="email">Email</Label>
						<Input
							id="email"
							name="email"
							type="email"
							placeholder="jan@example.com"
							value={form?.email ?? ''}
							required
						/>
					</div>

					<div class="space-y-2">
						<Label for="password">Hasło</Label>
						<Input
							id="password"
							name="password"
							type="password"
							placeholder="Twoje hasło"
							required
						/>
					</div>

					<Button type="submit" class="w-full">Zaloguj się</Button>
				</form>
			</Card.Content>
		</Card.Root>
	</div>
</main>
