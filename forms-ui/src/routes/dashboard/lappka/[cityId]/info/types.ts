export type InfoCategory = {
    id: number;
    name: string;
    color?: string | null;
};

export type Info = {
    id: number;
    infoName: string;
    infoText: string;
    city: number;
    category: number;
    sortNumber: number;
    categoryName: string;
    icon?: string | null;
    color?: string | null;
};

export type InfoResponse = {
    infos: Info[];
    categories: InfoCategory[];
};
