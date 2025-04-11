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
        private readonly messageCallback: (message: InboundMessage) => void,
        private readonly shouldRequestIcon: () => boolean,
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

        setInterval(() => self.ping(), 2000);
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
            self.ws = new WebSocket(`${__WS_HOST__}/ws?name=${self.name.value}`);
            self.ws.onopen = () => {
                self.hasEverBeenConnected = true;
                self.manualIsConnected.value = true;

                // Add the real event listeners
                self.ws.onmessage = self.handleMessage;
                self.ws.onerror = err => console.warn(err);
                self.ws.onclose = self.handleClosure;
                self.ws.onopen = null;

                this.ping(true);

                resolve();
            };

            self.ws.onerror = reject;
            self.ws.onclose = reject;
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
            console.debug('sending', message);
            this.ws.send(JSON.stringify(message));
        } else {
            console.log('refusing to send message, not connected', message);
        }
    }

    ping(forceRequestIcon: boolean = false) {
        this.send({ type: 'ping', request_icon: forceRequestIcon || this.shouldRequestIcon() });
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