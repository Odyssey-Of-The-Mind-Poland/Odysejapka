import jwt_decode from 'jwt-decode';

export default class Token {
    private readonly token: string | undefined;

    constructor(token: string | undefined) {
        this.token = token;
    }

    isExpired(): boolean {
        if (!this.token || this.token === '') {
            return true;
        }

        try {
            const decoded: any = jwt_decode(this.token);
            const currentTime = Date.now() / 1000;

            if (decoded.exp < currentTime) {
                return true;
            }
        } catch (error) {
            console.error('Error decoding token:', error);
        }

        return false;
    }

    getUserRoles(): any[] {
        if (this.token != null) {
            const decoded: any = jwt_decode(this.token);
            return decoded['https://app.odyseja.org/roles'] || [];
        }
        return [];
    }
}