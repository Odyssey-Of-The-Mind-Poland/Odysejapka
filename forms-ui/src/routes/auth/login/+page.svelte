<script lang="ts">
    import {SignIn} from "@auth/sveltekit/components";
    import {Button} from "$lib/components/ui/button";
    import {Input} from "$lib/components/ui/input";
    import {Label} from "$lib/components/ui/label";
    import * as Card from "$lib/components/ui/card";
    import {enhance} from "$app/forms";

    let {form} = $props();
</script>

<main class="flex min-h-screen items-center justify-center">
    <Card.Root class="w-full max-w-md">
        <Card.Header>
            <Card.Title class="text-2xl">Zaloguj się do Odyseja</Card.Title>
            <Card.Description>Wybierz metodę logowania</Card.Description>
        </Card.Header>
        <Card.Content class="space-y-6 w-full">
            <!-- Auth0 -->
            <SignIn provider="auth0" className="w-full">
                <input type="hidden" name="callbackUrl" value="/dashboard" />
                <Button slot="submitButton" variant="secondary" class="w-full">
                    Zaloguj przez Auth0
                </Button>
            </SignIn>

            <div class="relative">
                <div class="absolute inset-0 flex items-center">
                    <span class="w-full border-t"></span>
                </div>
                <div class="relative flex justify-center text-xs uppercase">
                    <span class="bg-background px-2 text-muted-foreground">Lub</span>
                </div>
            </div>

            <!-- Email/password -->
            <form method="POST" use:enhance class="space-y-4">
                {#if form?.error}
                    <div class="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-600">
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
</main>
