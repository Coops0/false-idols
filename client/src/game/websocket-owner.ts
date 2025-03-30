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
    private initialMessageBuffer: MessageEvent[] = [];

    constructor(
        private readonly name: Ref<string>,
        private readonly messageCallback: (message: InboundMessage) => void,
        private readonly manualIsConnected: Ref<boolean>
    ) {
        const self = this;

        window.addEventListener('online', () => self.attemptReconnection());
        window.addEventListener('focus', () => self.attemptReconnection());
        window.addEventListener('visibilitychange', () => {
            if (document.visibilityState === 'visible') {
                self.attemptReconnection();
            }
        });

        setInterval(() => self.send({ type: 'ping' }), 1000);
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

        this.wasManuallyDisconnectedLast = false;

        const self = this;
        return new Promise<void>((resolve, reject) => {
            const rejectOnError = (err: Event) => reject(err);
            const resolveOnOpen = (event: MessageEvent) => {
                this.hasEverBeenConnected = true;

                const message = JSON.parse(event.data) as InboundMessage;
                // This should be the first immediate message received
                if (message.type !== 'assign_icon') {
                    console.warn('Got initial message not of type assign icon', message);
                    self.initialMessageBuffer.push(event);
                    return;
                }

                self.manualIsConnected.value = true;

                // Remove our temporary initial event listeners
                self.ws.removeEventListener('message', event => resolveOnOpen(event));
                self.ws.removeEventListener('error', err => rejectOnError(err));
                self.ws.removeEventListener('close', err => rejectOnError(err));

                // Add the real event listeners
                self.ws.addEventListener('message', message => this.handleMessage(message));
                self.ws.addEventListener('close', event => this.handleClosure(event));
                self.ws.addEventListener('error', err => console.warn(err));

                resolve();

                // Finally fall back to real message handling
                self.handleMessage(event);

                // Any other messages in the buffer we also send through
                self.initialMessageBuffer.forEach(event => self.handleMessage(event));
                self.initialMessageBuffer = [];
            };

            self.ws = new WebSocket(`ws://localhost:8080/ws?name=${this.name.value}`);
            self.ws.addEventListener('message', event => resolveOnOpen(event));
            self.ws.addEventListener('error', err => rejectOnError(err));
            self.ws.addEventListener('close', err => rejectOnError(err));
        });
    }

    attemptReconnection(force: boolean = false) {
        if (this.isConnected) return;
        if (!force) {
            if (
                !this.hasEverBeenConnectedSuccessfully ||
                Date.now() - this.lastConnectionAttempt < 100 ||
                this.wasManuallyDisconnectedLast
            ) return;
        }
        this.lastConnectionAttempt = Date.now();

        const delay = (this.hasFailedToConnect && !force) ? 100 : 0;
        console.log(`Trying to reconnect in ${delay}`);

        const self = this;
        setTimeout(() => {
            self.connect()
                .then(() => console.log('Reconnected'))
                .catch(err => {
                    console.error('Failed to reconnect', err);

                    if (self.hasEverBeenConnected && !self.wasManuallyDisconnectedLast) {
                        self.hasFailedToConnect = true;
                        self.attemptReconnection();
                    }
                });
        }, delay);
    }

    send(message: OutboundMessage) {
        if (this.isConnected) {
            if (message.type !== 'ping') {
                console.log('sending', message);
            }
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
        this.manualIsConnected.value = false;
        this.attemptReconnection();
    }
}