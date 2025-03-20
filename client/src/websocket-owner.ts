import { setGame } from './main.ts';
import { Game } from './game/game.ts';
import { InboundMessage, OutboundMessages, Role } from './game/messages.ts';

export class WebsocketOwner {
    // @ts-expect-error - Will be initialized immediately after construction
    private ws: WebSocket = null;

    private hasFailedToConnect: boolean = false;
    private lastConnectionAttempt: number = 0;
    private hasEverBeenConnected: boolean = false;

    get isConnected() {
        return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
    }

    connect() {
        return new Promise<void>((resolve, reject) => {
            const rejectOnError = (err: Event) => reject(err);

            this.ws = new WebSocket(`ws://localhost:8080/ws?name=${this.name}`);

            this.ws.addEventListener('open', () => {
                this.hasFailedToConnect = false;
                this.hasEverBeenConnected = true;
                this.ws.removeEventListener('error', rejectOnError);

                resolve();
            });

            this.ws.addEventListener('error', err => rejectOnError(err));

            this.ws.addEventListener('message', message => this.handleMessage(message));
            this.ws.addEventListener('close', event => this.handleClosure(event));
            this.ws.addEventListener('error', err => console.warn(err));
        });
    }

    constructor(private readonly name: string) {
        window.addEventListener('online', () => this.attemptReconnection());
        window.addEventListener('focus', () => this.attemptReconnection());
        window.addEventListener('visibilitychange', () => {
            if (document.visibilityState === 'visible') {
                this.attemptReconnection();
            }
        });

        setInterval(() => {
            this.send({ type: 'ping' });
        }, 1000);
    }

    private handleMessage(event: MessageEvent) {
        const message = JSON.parse(event.data) as InboundMessage;
        switch (message.type) {
            case 'request_chief_card_discard':
                console.log('Discard one card', message.cards);

                break;
            case 'request_action':
                console.log('Request action', message.permittedActions, message.players);
                break;
            case 'request_advisor_card_choice':
                console.log('Choose card', message.cards);
                break;
            case 'investigation_result':
                console.log('Investigation result', message.target, message.simpleRole);
                break;
            case 'disconnect': {
                console.log('Disconnecting', message.reason);
                this.ws.close();
                break;
            }
            case 'assign_role': {
                console.log('Role assigned', message.role, message.isChief, message.demonCount);
                if (message.role === Role.Demon) {
                    console.log('Teammates', message.teammates);
                    console.log('Satan', message.satan);
                }

                setGame(new Game(message));
                break;
            }
            default: {
                console.warn('Unhandled message', message);
            }
        }
    }

    private attemptReconnection() {
        if (this.isConnected || Date.now() - this.lastConnectionAttempt < 100) return;
        this.lastConnectionAttempt = Date.now();

        const delay = this.hasFailedToConnect ? 100 : 0;
        console.log(`Trying to reconnect in ${delay}`);

        setTimeout(() => {
            this.connect()
                .then(() => console.log('Reconnected'))
                .catch(err => {
                    console.error('Failed to reconnect', err);

                    if (this.hasEverBeenConnected) {
                        this.hasFailedToConnect = true;
                        this.attemptReconnection();
                    }
                });
        }, delay);
    }

    private handleClosure(event: CloseEvent) {
        console.log('Connection closed', event);
        this.attemptReconnection();
    }

    send(message: OutboundMessages) {
        if (this.isConnected) {
            this.ws.send(JSON.stringify(message));
        }
    }
}

//@formatter:on