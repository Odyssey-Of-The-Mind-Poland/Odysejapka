import IconUserCog from "@tabler/icons-svelte/icons/user-cog";
import IconTrophy from "@tabler/icons-svelte/icons/trophy";
import IconPaw from "@tabler/icons-svelte/icons/paw";
import type {Role} from "$lib/userStore";

export type NavItem = {
    title: string;
    url: string;
    icon: typeof IconUserCog;
    requiredRole?: Role;
    requiredRoles?: Role[];
};

export const routes = {
    navMain: [
        {
            title: "Użytkownicy",
            url: "/dashboard/users",
            icon: IconUserCog,
            requiredRole: 'ADMINISTRATOR',
        },
        {
            title: "Konkursy",
            url: "/dashboard/competitions",
            icon: IconTrophy,
        },
        {
            title: "Łappka Omera",
            url: "/dashboard/lappka",
            icon: IconPaw,
            requiredRoles: ['ADMINISTRATOR', 'LAPPKA'],
        },
    ] satisfies NavItem[],

    adminRoutes: [
        {url: "/dashboard/editor", requiredRole: 'ADMINISTRATOR' as Role},
        {url: "/dashboard/spontan-editor", requiredRole: 'ADMINISTRATOR' as Role},
    ],
};
