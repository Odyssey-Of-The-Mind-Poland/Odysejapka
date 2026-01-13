import UsersIcon from "@tabler/icons-svelte/icons/users";
import UsersGroupIcon from "@tabler/icons-svelte/icons/users-group";

export const routes = {
    user: {
        name: "shadcn",
        email: "m@example.com",
        avatar: "/avatars/shadcn.jpg",
    },
    navMain: [
        {
            title: "User management",
            url: "/dashboard/users",
            icon: UsersIcon,
        },
        {
            title: "Edytor",
            url: "/dashboard/editor",
            icon: UsersIcon,
        },
        {
            title: "Teams",
            url: "/dashboard/teams",
            icon: UsersGroupIcon,
        },
    ],
};