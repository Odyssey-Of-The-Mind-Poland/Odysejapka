import UsersIcon from "@tabler/icons-svelte/icons/users";

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
            title: "Form",
            url: "/dashboard/form",
            icon: UsersIcon,
        },
        {
            title: "Edytor",
            url: "/dashboard/editor",
            icon: UsersIcon,
        },
    ],
};