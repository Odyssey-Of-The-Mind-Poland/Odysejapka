import IconUserCog from "@tabler/icons-svelte/icons/user-cog";
import IconForms from "@tabler/icons-svelte/icons/forms";
import IconUsersGroup from "@tabler/icons-svelte/icons/users-group";

export const routes = {
    navMain: [
        {
            title: "Użytkownicy",
            url: "/dashboard/users",
            icon: IconUserCog,
        },
        {
            title: "Edytor formularzy",
            url: "/dashboard/editor",
            icon: IconForms,
        },
        {
            title: "Drużyny",
            url: "/dashboard/teams",
            icon: IconUsersGroup,
        },
    ],
};