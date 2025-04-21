import { ICONS } from '@/game/player-icon.ts';

export enum Role {
    SATAN = 'SATAN',
    DEMON = 'DEMON',
    ANGEL = 'ANGEL',
}

export const roleName = (role: Role) => {
    switch (role) {
        case Role.ANGEL:
            return 'Angel';
        case Role.DEMON:
            return 'Demon';
        case Role.SATAN:
            return 'Satan';
    }
};

export type Player = {
    name: string;
    icon: typeof ICONS[number];
};

export type GamePlayer = Player & {
    role: Role;
    is_president: boolean;
    is_alive: boolean;

    was_president_last_round: boolean;
    was_advisor_last_round: boolean;
};

export enum CardConsequence {
    POSITIVE = 'POSITIVE',
    NEGATIVE = 'NEGATIVE',
    NEUTRAL = 'NEUTRAL',
}

export type Card = {
    id: number;
    description: string;
    consequence: CardConsequence;
}

export type CardDeck = {
    card_stack: Card[];
    played_cards: Card[];
}

export enum ActionChoice {
    INVESTIGATE = 'INVESTIGATE',
    KILL = 'KILL',
    NOMINATE = 'NOMINATE',
    NOMINATE_PRESIDENT = 'NOMINATE_PRESIDENT',
}

export type IdleInnerGameState = { type: 'idle' };
export type AwaitingPresidentActionChoiceInnerGameState = {
    type: 'awaiting_president_action_choice',
    action: ActionChoice,
};
export type AwaitingPresidentCardDiscardInnerGameState = {
    type: 'awaiting_president_card_discard',
    cards: Card[],
    advisor_name: string
};
export type AwaitingAdvisorCardChoiceInnerGameState = {
    type: 'awaiting_advisor_card_choice',
    cards: Card[],
    advisor_name: string
};
export type AwaitingAdvisorElectionOutcomeInnerGameState = {
    type: 'awaiting_advisor_election_outcome',
    nominee: string
};
export type AwaitingPresidentElectionOutcomeInnerGameState = {
    type: 'awaiting_president_election_outcome',
    nominee: string
};
export type AwaitingRoleConfirmationsInnerGameState = {
    type: 'awaiting_role_confirmations';
    confirmed: string[]
}

export type AwaitingInvestigationAnalysisInnerGameState = { type: 'awaiting_investigation_analysis', target: string };

export type InnerGameState =
    IdleInnerGameState
    | AwaitingPresidentActionChoiceInnerGameState
    | AwaitingPresidentCardDiscardInnerGameState
    | AwaitingAdvisorCardChoiceInnerGameState
    | AwaitingAdvisorElectionOutcomeInnerGameState
    | AwaitingPresidentElectionOutcomeInnerGameState
    | AwaitingInvestigationAnalysisInnerGameState
    | AwaitingRoleConfirmationsInnerGameState;

export type LobbyGameState = {
    type: 'lobby';
    players: Player[];
};

export type InProgressGameState = {
    type: 'game_in_progress';
    players: GamePlayer[];
    inner_game_state: InnerGameState;
    deck: CardDeck;
    failed_elections: number;
}

export type GameOverGameState = {
    type: 'game_over';
    players: Player[];
    winner: 'ANGEL' | 'DEMON';
    satan: string;
    demons: string[];
    reason: 'SATAN_KILLED' | 'SATAN_ELECTED_ADVISOR_LATE_GAME' | 'POSITIVE_THRESHOLD_REACHED' | 'NEGATIVE_THRESHOLD_REACHED' | 'ALL_ANGELS_DEAD';
}

export type GameState = LobbyGameState | InProgressGameState | GameOverGameState;