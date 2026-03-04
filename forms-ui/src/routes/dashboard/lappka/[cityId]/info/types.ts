export type InfoCategory = {
    id: number;
    name: string;
};

export type Info = {
    id: number;
    infoName: string;
    infoText: string;
    city: number;
    category: number;
    sortNumber: number;
    categoryName: string;
    icon?: string;
    color?: string;
};

export type InfoResponse = {
    infos: Info[];
    categories: InfoCategory[];
};
