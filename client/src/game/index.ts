import { type InboundMessage, type Player, Role } from './messages.ts';

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

export type GameState = IdleGameState | ViewRoleGameState;

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