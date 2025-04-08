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
    isSmallGame: boolean;
    demons: Player[] | null;
    satan: Player | null;
}

export type CommitActionGameState = {
    type: 'commit_action';
    action: ActionChoice;
    supplementedPlayers: ActionSupplementedPlayer[];
}

export type PresidentDiscardCardGameState = {
    type: 'president_discard_card';
    cards: Card[];
}

export type AdvisorChooseCardGameState = {
    type: 'advisor_choose_card';
    cards: Card[];
    vetoable: boolean;
}

export type ViewInvestigationResultsGameState = {
    type: 'view_investigation_results';
    player: Player;
    role: SimpleRole;
    hasConfirmed: boolean;
}

export type PolicyPeekGameState = {
    type: 'policy_peek';
    cards: Card[];
}

export type GameState =
    IdleGameState
    | ViewRoleGameState
    | CommitActionGameState
    | PresidentDiscardCardGameState
    | AdvisorChooseCardGameState
    | ViewInvestigationResultsGameState
    | PolicyPeekGameState;

export class Game {
    readonly role: Role;
    state: GameState;

    constructor(message: InboundMessage) {
        if (message.type !== 'assign_role') {
            throw new Error('Invalid message type');
        }

        this.state = {
            type: 'view_role',
            hasConfirmed: false,
            demonCount: message.demon_count,
            demons: message.demons || null,
            satan: message.satan || null,
            isSmallGame: message.is_small_game,
        };

        this.role = message.role;
    }
}

// const t: Game = {
//     role: "SATAN",
//     state: {
//         type: "view_role",
//         hasConfirmed: true,
//         demonCount: 1,
//         isSmallGame: true,
//         demons: [{name: 'Ok', icon: 'gerbil'}],
//         // satan: Player | null,
//     }
// }