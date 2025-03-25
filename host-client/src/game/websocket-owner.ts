import type { ServerInboundMessage, ServerOutboundMessage } from '@/game/messages.ts';

export class WebsocketOwner {
    // @ts-expect-error - Will be initialized immediately after construction
    private ws: WebSocket = null;

    constructor(private readonly onMessageCallback: (message: ServerInboundMessage) => void) {
        setInterval(() => this.send({ type: 'ping' }), 1000);
    }

    get isConnected() {
        return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
    }

    async connect() {
        this.ws = new WebSocket('ws://localhost:8080/server-ws');
        this.ws.addEventListener('message', this.onMessage);
    }

    send(message: ServerOutboundMessage) {
        if (this.isConnected) {
            this.ws.send(JSON.stringify(message));
        }
    }

    private onMessage = (event: MessageEvent) => {
        const message = JSON.parse(event.data) as ServerInboundMessage;
        this.onMessageCallback(message);
    };
}