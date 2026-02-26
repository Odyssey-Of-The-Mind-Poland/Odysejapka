import type { Cookies } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';
import crypto from 'node:crypto';

const COOKIE_NAME = 'backend_token';
const ALGORITHM = 'aes-256-gcm';

function getEncryptionKey(): Buffer {
	const secret = env.AUTH_SECRET;
	if (!secret) throw new Error('AUTH_SECRET is not set');
	return crypto.createHash('sha256').update(secret).digest();
}

export function encrypt(text: string): string {
	const key = getEncryptionKey();
	const iv = crypto.randomBytes(12);
	const cipher = crypto.createCipheriv(ALGORITHM, key, iv);
	let encrypted = cipher.update(text, 'utf8', 'hex');
	encrypted += cipher.final('hex');
	const authTag = cipher.getAuthTag().toString('hex');
	return `${iv.toString('hex')}:${authTag}:${encrypted}`;
}

export function decrypt(encryptedText: string): string {
	const key = getEncryptionKey();
	const [ivHex, authTagHex, encrypted] = encryptedText.split(':');
	const iv = Buffer.from(ivHex, 'hex');
	const authTag = Buffer.from(authTagHex, 'hex');
	const decipher = crypto.createDecipheriv(ALGORITHM, key, iv);
	decipher.setAuthTag(authTag);
	let decrypted = decipher.update(encrypted, 'hex', 'utf8');
	decrypted += decipher.final('utf8');
	return decrypted;
}

export function setBackendToken(cookies: Cookies, token: string): void {
	cookies.set(COOKIE_NAME, encrypt(token), {
		httpOnly: true,
		secure: env.NODE_ENV === 'production',
		sameSite: 'lax',
		path: '/',
		maxAge: 60 * 60 * 24 * 7 // 7 days
	});
}

export function getBackendToken(cookies: Cookies): string | null {
	const encrypted = cookies.get(COOKIE_NAME);
	if (!encrypted) return null;
	try {
		return decrypt(encrypted);
	} catch {
		return null;
	}
}

export function clearBackendToken(cookies: Cookies): void {
	cookies.delete(COOKIE_NAME, { path: '/' });
}
