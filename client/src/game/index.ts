import {
    ActionChoice,
    type ActionSupplementedPlayer,
    type Card,
    type InboundMessage,
    type Player,
    Role,
    type SimpleRole
} from './messages.ts';

export type IdleGameState = {
    type: 'idle';
}

export type ViewRoleGameState = {
    type: 'view_role';
    /// Show confirmation/warning before showing the role.
    hasConfirmed: boolean;
    demonCount: number;
    demonExtras: {
        teammates: Player[];
        satan: Player;
    } | null
}

export type CommitActionGameState = {
    type: 'commit_action';
    permittedActions: ActionChoice[];
    supplementedPlayers: ActionSupplementedPlayer[];
}

export type ChiefDiscardCardGameState = {
    type: 'chief_discard_card';
    cards: Card[];
}

export type AdvisorChooseCardGameState = {
    type: 'advisor_choose_card';
    cards: Card[];
}

export type ViewInvestigationResultsGameState = {
    type: 'view_investigation_results';
    player: Player;
    role: SimpleRole;
    hasConfirmed: boolean;
}

export type GameState =
    IdleGameState
    | ViewRoleGameState
    | CommitActionGameState
    | ChiefDiscardCardGameState
    | AdvisorChooseCardGameState
    | ViewInvestigationResultsGameState;

export class Game {
    readonly role: Role;
    isChief: boolean;
    state: GameState;

    constructor(message: InboundMessage) {
        if (message.type !== 'assign_role') {
            throw new Error('Invalid message type');
        }

        this.state = {
            type: 'view_role',
            hasConfirmed: false,
            demonCount: message.demon_count,
            demonExtras: message.role === Role.DEMON ? {
                teammates: message.teammates,
                satan: message.satan
            } : null
        };

        this.role = message.role;
        this.isChief = false;
    }
}