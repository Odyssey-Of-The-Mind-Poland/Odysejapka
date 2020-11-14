const API_URL = `http://localhost:8080`

const authProvider = {
    login: ({username, password}) => {
        console.log("jestem tu")
        const request = new Request(`${API_URL}/login`, {
            method: 'POST',
            credentials: 'include',
            body: JSON.stringify({ username, password }),
            headers: new Headers({ 'Content-Type': 'application/json' }),
        })
        return fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    throw new Error(response.statusText)
                }
                return response.headers
            })
    },
    logout: () => {
        const request = new Request(`${API_URL}/logout`, {
            method: 'GET',
            credentials: 'include',
        })
        return fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    throw new Error(response.statusText)
                }
                return Promise.resolve()
            })
    },
    checkAuth: () => {
        const request = new Request(`${API_URL}/user`, {
            method: 'GET',
            credentials: 'include',
        })
        return fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    throw new Error(response.statusText)
                }
                return response.json()
            })
            .then(data => {
                console.log(data)
                if(data.authenticated === true) return Promise.resolve()
                else return Promise.reject()
                })
    },
    checkError: error => Promise.resolve(),
    getPermissions: params => Promise.resolve(),
    getIdentity: () => Promise.resolve(),
}

export default authProvider;
