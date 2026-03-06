export type Team = {
    id: number;
    teamName: string;
    problem: number;
    age: number;
    league: string;
    stageTimeSlotId?: number | null;
    spontanRoomSlotId?: number | null;
};

export type Stage = {
    id: number;
    number: number;
    name: string;
    city: number;
};

export type TimeSlot = {
    id: number;
    stageId: number;
    startTime: string;
    endTime: string;
    teamId?: number | null;
    teamName?: string | null;
};

export type SpontanRoom = {
    id: number;
    name: string;
    city: number;
};

export type SpontanSlot = {
    id: number;
    roomId: number;
    startTime: string;
    endTime: string;
    teamId?: number | null;
    teamName?: string | null;
};

export type HarmonogramData = {
    teams: Team[];
    stages: Stage[];
    timeSlots: TimeSlot[];
    spontanRooms: SpontanRoom[];
    spontanSlots: SpontanSlot[];
};
