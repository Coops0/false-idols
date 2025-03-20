import { type InboundMessage, type OutboundMessage } from './messages.ts';
import type { Ref } from 'vue';
import { isNameValid } from '@/util';

export class WebsocketOwner {
    // @ts-expect-error - Will be initialized immediately after construction
    private ws: WebSocket = null;

    private hasFailedToConnect: boolean = false;
    private lastConnectionAttempt: number = 0;
    private hasEverBeenConnected: boolean = false;
    private wasManuallyDisconnectedLast: boolean = false;

    constructor(
        private readonly name: Ref<string>,
        private readonly messageCallback: (message: InboundMessage) => void
    ) {
        window.addEventListener('online', () => this.attemptReconnection());
        window.addEventListener('focus', () => this.attemptReconnection());
        window.addEventListener('visibilitychange', () => {
            if (document.visibilityState === 'visible') {
                this.attemptReconnection();
            }
        });

        setInterval(() => this.send({ type: 'ping' }), 1000);
    }

    get isConnected() {
        return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
    }

    get hasEverBeenConnectedSuccessfully() {
        return this.hasEverBeenConnected;
    }

    connect() {
        if (!isNameValid(this.name.value)) {
            throw new Error('Name is invalid');
        }

        return new Promise<void>((resolve, reject) => {
            const rejectOnError = (err: Event) => reject(err);

            this.ws = new WebSocket(`ws://localhost:8080/ws?name=${this.name.value}`);

            this.ws.addEventListener('open', () => {
                this.hasFailedToConnect = false;
                this.hasEverBeenConnected = true;
                this.wasManuallyDisconnectedLast = false;

                this.ws.removeEventListener('error', rejectOnError);

                resolve();
            });

            this.ws.addEventListener('error', err => rejectOnError(err));

            this.ws.addEventListener('message', message => this.handleMessage(message));
            this.ws.addEventListener('close', event => this.handleClosure(event));
            this.ws.addEventListener('error', err => console.warn(err));
        });
    }

    attemptReconnection(force: boolean = false) {
        if (this.isConnected || (!force && Date.now() - this.lastConnectionAttempt < 100)) return;
        this.lastConnectionAttempt = Date.now();

        const delay = (this.hasFailedToConnect && !force) ? 100 : 0;
        console.log(`Trying to reconnect in ${delay}`);

        setTimeout(() => {
            this.connect()
                .then(() => console.log('Reconnected'))
                .catch(err => {
                    console.error('Failed to reconnect', err);

                    if (this.hasEverBeenConnected && !this.wasManuallyDisconnectedLast) {
                        this.hasFailedToConnect = true;
                        this.attemptReconnection();
                    }
                });
        }, delay);
    }

    send(message: OutboundMessage) {
        if (this.isConnected) {
            this.ws.send(JSON.stringify(message));
        }
    }

    disconnect() {
        this.wasManuallyDisconnectedLast = true;

        try {
            this.ws.close();
        } catch {
            /* ignored */
        }
    }

    private handleMessage(event: MessageEvent) {
        const message = JSON.parse(event.data) as InboundMessage;
        this.messageCallback(message);
    }

    private handleClosure(event: CloseEvent) {
        console.log('Connection closed', event);
        this.attemptReconnection();
    }
}