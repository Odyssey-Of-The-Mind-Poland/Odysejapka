
const API_URL = `http://localhost:8080`

const dataProvider = {
    getList: async (resource, params) => {
        const { page, perPage } = params.pagination
        const { field, order } = params.sort
        const request = new Request(`${API_URL}/${resource}?page=${page - 1}&size=${perPage}&sort=${field},${order}`, {
            method: 'GET'
        })
        const response = await fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    throw new Error(response.statusText)
                }
                return response.json()
            })
        var data = {
            data: response,
            total: response.length,
        }
        return data
    },
    getOne: async (resource, params) => {
        const request = new Request(`${API_URL}/${resource}/${params.id}`, {
            method: 'GET',
        })
        const response = await fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    throw new Error(response.statusText)
                }
                return response.json()
            })
        return {
            data: response
        }
    },
    // NO VISIBLE USE CASE
    getMany: (resource, params) => Promise,
    // NO VISIBLE USE CASE
    getManyReference: (resource, params) => Promise,
    create: async (resource, params) => {
        const request = new Request(`${API_URL}/${resource}`, {
            method: 'POST',
            body: JSON.stringify(params.data)
        })
        const response = await fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    return Promise.reject(response)
                }
                return response.json()
            })
        return {
            data: response
        }
    },
    update: async (resource, params) => {
        const request = new Request(`${API_URL}/${resource}/${params.id}`, {
            method: 'PUT',
            body: JSON.stringify(params.data)
        })
        const response = await fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    return Promise.reject(response)
                }
                return { data: { id: params.id }}
            })
        return {
            data: response
        }
    },
    // NO VISIBLE USE CASE
    updateMany: (resource, params) => Promise,
    delete: async (resource, params) => {
        const request = new Request(`${API_URL}/${resource}/${params.id}`, {
            method: 'DELETE',
        })
        const response = await fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    return Promise.reject(response)
                }
                return { data: { id: params.id }}
            })
        return {
            data: response
        }
    },
    // NO VISIBLE USE CASE
    deleteMany: (resource, params) => Promise,
}

export default dataProvider