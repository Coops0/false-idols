export class WebsocketOwner {
    private readonly name: string;
    // @ts-expect-error - Will be initialized immediately after construction
    private ws: WebSocket;

    private hasFailedToConnect: boolean = false;

    connect() {
        return new Promise<void>((resolve, reject) => {
            const rejectOnError = (err: Event) => reject(err);

            this.ws = new WebSocket(`ws://localhost:8080/ws?name=${this.name}`);

            this.ws.addEventListener('open', () => {
                this.hasFailedToConnect = false;
                this.ws.removeEventListener('error', rejectOnError);

                resolve();
            });

            this.ws.addEventListener('error', err => rejectOnError(err));
            this.ws.addEventListener('message', message => this.handleMessage(message));
            this.ws.addEventListener('close', event => this.handleClosure(event));
            this.ws.addEventListener('error', err => console.warn(err));
        });
    }

    constructor(name: string) {
        this.name = name;
    }

    private handleMessage(event: MessageEvent) {
        console.log(event.data);
    }

    private attemptReconnection() {
        const delay = this.hasFailedToConnect ? 100 : 0;

        console.log(`Trying to reconnect in ${delay}`);

        setTimeout(() => {
            this.connect()
                .then(() => console.log('Reconnected'))
                .catch(err => {
                    console.error('Failed to reconnect', err);
                    this.attemptReconnection();
                });
        }, delay);
    }

    private handleClosure(event: CloseEvent) {
        console.log('Connection closed', event);

        this.hasFailedToConnect = true;
        this.attemptReconnection();
    }
}