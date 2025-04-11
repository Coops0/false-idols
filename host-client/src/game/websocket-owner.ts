import type { ServerInboundMessage, ServerOutboundMessage } from '@/game/messages.ts';

export class WebsocketOwner {
    // @ts-expect-error - Will be initialized immediately after construction
    private ws: WebSocket = null;

    constructor(
        private readonly onMessageCallback: (message: ServerInboundMessage) => void,
        private readonly shouldRequestState: () => boolean
    ) {
        setInterval(() => this.send({ type: 'ping', request_state: shouldRequestState() }), 2000);
    }

    get isConnected() {
        return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
    }

    async connect() {
        this.ws = new WebSocket(`${__WS_HOST__}/server-ws`);
        this.ws.onmessage = event => this.onMessage(event);
    }

    send(message: ServerOutboundMessage) {
        if (this.isConnected) {
            if (message.type !== 'ping') {
                console.debug('sending', message);
            }
            this.ws.send(JSON.stringify(message));
        }
    }

    close() {
        if (this.ws) {
            this.ws.close();
        }
    }

    private onMessage(event: MessageEvent) {
        const message = JSON.parse(event.data) as ServerInboundMessage;
        this.onMessageCallback(message);
    }
}