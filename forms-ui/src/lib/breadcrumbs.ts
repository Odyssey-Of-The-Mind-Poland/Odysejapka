import {writable} from 'svelte/store';

export type Crumb = { name: string; href?: string; current: boolean };

const _breadcrumbs = writable<Crumb[]>([]);
export const breadcrumbs = {subscribe: _breadcrumbs.subscribe};

export function setBreadcrumbs(items: Array<{ name: string; href?: string }>) {
    const cleaned: Crumb[] = items
        .filter((c) => c && c.name)
        .map((c) => ({name: c.name, href: normalizeHref(c.href), current: false}));

    if (cleaned.length) cleaned[cleaned.length - 1].current = true;

    if (cleaned.length && cleaned[cleaned.length - 1].current) {
        cleaned[cleaned.length - 1].href = cleaned[cleaned.length - 1].href ?? undefined;
    }

    _breadcrumbs.set(cleaned);
}

export function clearBreadcrumbs() {
    _breadcrumbs.set([]);
}

function normalizeHref(href?: string) {
    if (!href) return undefined;
    if (/^https?:\/\//i.test(href)) return href;
    href = href.startsWith('/') ? href : `/${href}`;
    href = href.replace(/\/+$/g, '');
    return href || '/';
}
