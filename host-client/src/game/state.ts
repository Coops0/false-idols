export enum Role {
    SATAN = 'SATAN',
    DEMON = 'DEMON',
    ANGEL = 'ANGEL',
}

export type Player = {
    name: string;
    icon: string;
};
export type GamePlayer = Player & {
    role: Role;
    is_chief: boolean;
    is_alive: boolean;

    was_chief_last_round: boolean;
    was_advisor_last_round: boolean;
};

export type Card = {
    id: number;
    description: string;
    consequence: number;
    consequence_qualifier: 'POSITIVE' | 'NEGATIVE' | 'NEUTRAL';
}

export type CardDeck = {
    card_stack: Card[];
    played_cards: Card[];
}

export enum ActionChoice {
    INVESTIGATE = 'INVESTIGATE',
    ELECT = 'ELECT',
    KILL = 'KILL',
}

export type InnerGameState = {
    type: 'idle';
} | {
    type: 'awaiting_player_action_choice';
    permitted_actions: ActionChoice[];
} | {
    type: 'awaiting_chief_card_discard';
    cards: Card[];
} | {
    type: 'awaiting_advisor_card_choice';
    cards: Card[];
} | {
    type: 'awaiting_election_resolution';
    nominee: string;
} | {
    type: 'awaiting_investigation_analysis';
    target: string;
}

export type GameState = {
    type: 'lobby';
} | {
    type: 'game_in_progress';
    players: GamePlayer[];
    inner_game_state: InnerGameState;
    deck: CardDeck;
    failed_elections: number;
} | {
    type: 'game_over';
    players: Player[];
    winner: string;
    satan: string;
    demons: string[];
    reason: 'SATAN_KILLED' | 'SATAN_ELECTED_ADVISOR_LATE_GAME' | 'POSITIVE_THRESHOLD_REACHED' | 'NEGATIVE_THRESHOLD_REACHED' | 'ALL_ANGELS_DEAD';
}