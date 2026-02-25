import IconUserCog from "@tabler/icons-svelte/icons/user-cog";
import IconForms from "@tabler/icons-svelte/icons/forms";
import IconUsersGroup from "@tabler/icons-svelte/icons/users-group";
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
            title: "Edytor formularzy",
            url: "/dashboard/editor",
            icon: IconForms,
            requiredRole: 'ADMINISTRATOR',
        },
        {
            title: "Drużyny",
            url: "/dashboard/teams",
            icon: IconUsersGroup,
        },
    ] satisfies NavItem[],
};