import IconUserCog from "@tabler/icons-svelte/icons/user-cog";
import IconTrophy from "@tabler/icons-svelte/icons/trophy";
import type {Role} from "$lib/userStore";

export type NavItem = {
    title: string;
    url: string;
    icon: typeof IconUserCog;
    requiredRole?: Role;
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
    ] satisfies NavItem[],

    adminRoutes: [
        {url: "/dashboard/editor", requiredRole: 'ADMINISTRATOR' as Role},
        {url: "/dashboard/spontan-editor", requiredRole: 'ADMINISTRATOR' as Role},
    ],
};
