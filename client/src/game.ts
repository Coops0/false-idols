import { InboundMessage, Role } from './websocket-owner.ts';

export class Game {
    readonly role: Role;
    isChief: boolean;

    constructor(message: InboundMessage) {
        if (message.type !== 'assign_role') {
            throw new Error('Invalid message type');
        }

        this.role = message.role;
        this.isChief = message.isChief;
    }
}