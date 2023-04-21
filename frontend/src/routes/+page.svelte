<script>
    import { onMount } from "svelte";
    import { login, logout, handleAuthentication } from '../authService.js';
    import tokenStore from '../tokenStore.js';

    onMount(async () => {
        handleAuthentication(token => tokenStore.set(token));
    });

    async function fetchData() {
        let token;
        tokenStore.subscribe(value => token = value);
        const response = await fetch('http://localhost:8081/age', {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });

        if (response.ok) {
            const data = await response.json();
            console.log(data);
        } else {
            console.error('Error fetching data:', response.status, response.statusText);
        }
    }
</script>

<button on:click={login}>Log in</button>
<button on:click={logout}>Log out</button>
<button on:click={fetchData}>Fetch</button>
