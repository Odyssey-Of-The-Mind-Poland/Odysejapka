import IconUserCog from "@tabler/icons-svelte/icons/user-cog";
import IconTrophy from "@tabler/icons-svelte/icons/trophy";
import IconPaw from "@tabler/icons-svelte/icons/paw";
import IconTrees from "@tabler/icons-svelte/icons/trees";
import IconBuilding from "@tabler/icons-svelte/icons/building";
import IconCalendarEvent from "@tabler/icons-svelte/icons/calendar-event";
import type {Role} from "$lib/userStore";

export type NavChild = {
    id: string;
    label: string;
};

export type NavItem = {
    title: string;
    url: string;
    icon: typeof IconUserCog;
    requiredRole?: Role;
    requiredRoles?: Role[];
    needsCity?: boolean;
    children?: NavChild[];
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
            title: "Miasta",
            url: "/dashboard/cities",
            icon: IconBuilding,
            requiredRole: 'ADMINISTRATOR',
        },
        {
            title: "Konkursy",
            url: "/dashboard/competitions",
            icon: IconTrophy,
            needsCity: true,
            children: [
                {id: 'dt', label: 'Problemy DT'},
                {id: 'spontany', label: 'Spontany'},
            ],
        },
        {
            title: "Łappka Omera",
            url: "/dashboard/lappka",
            icon: IconPaw,
            requiredRoles: ['ADMINISTRATOR', 'LAPPKA'],
            needsCity: true,
            children: [
                {id: 'info', label: 'Informacje'},
                {id: 'stages', label: 'Sceny'},
                {id: 'sponsors', label: 'Sponsorzy'},
                {id: 'breaking-change', label: 'Breaking Change'},
            ],
        },
        {
            title: "Harmonogram",
            url: "/dashboard/harmonogram",
            icon: IconCalendarEvent,
            requiredRole: 'ADMINISTRATOR',
            needsCity: true,
            children: [
                {id: 'stages', label: 'Sceny'},
                {id: 'spontan', label: 'Spontany'},
            ],
        },
        {
            title: "Zwierzyniec",
            url: "/dashboard/zwierzyniec",
            icon: IconTrees,
            requiredRole: 'ADMINISTRATOR',
            needsCity: true,
            children: [
                {id: 'rak', label: 'RAK'},
                {id: 'gad', label: 'GAD'},
                {id: 'sak', label: 'SAK'},
                {id: 'fixer', label: 'Fixer'},
            ],
        },
    ] satisfies NavItem[],

    adminRoutes: [
        {url: "/dashboard/editor", requiredRole: 'ADMINISTRATOR' as Role},
        {url: "/dashboard/spontan-editor", requiredRole: 'ADMINISTRATOR' as Role},
        {url: "/dashboard/zwierzyniec", requiredRole: 'ADMINISTRATOR' as Role},
    ],
};
