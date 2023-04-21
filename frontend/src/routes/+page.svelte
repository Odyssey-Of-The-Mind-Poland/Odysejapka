<!-- src/routes/index.svelte -->
<script>
    import { login } from '../authService.js';
    import Button from '../components/Button.svelte';

    function handleLogin() {
        login();
    }
</script>

<style>
    :global(body) {
        background-color: #3C3E40;
        margin: 0;
        font-family: Ubuntu, sans-serif;
    }

    .container {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        width: 100vw;
    }

    .login-box {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background-color: #292B2D;
        padding: 2rem;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .logo {
        width: 300px;
        height: 300px;
        margin-bottom: 1rem;
        border-radius: 50%;
    }
</style>

<div class="container">
    <div class="login-box">
        <img class="logo" src="/logo.png" alt="Logo" />
        <Button onClick={handleLogin}>Login</Button>
    </div>
</div>