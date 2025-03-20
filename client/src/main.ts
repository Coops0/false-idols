import van from 'vanjs-core';
import './style.css';
import { WebsocketOwner } from './websocket-owner.ts';
import { Game } from './game/game.ts';
import { PlayerIcon } from './game/player-icon.ts';
import { App, InitialState } from './components/App.ts';

// @ts-expect-error - Will be init before any other classes try to access
export let ws: WebsocketOwner = null;

let mounted = false;

let name: string = localStorage.getItem('name') ?? '';

if (name.length) {
    tryToConnect();
} else {
    van.add(App(InitialState.NEED_LOGIN));
}

export async function tryToConnect() {
    if (ws === null) {
        ws = new WebsocketOwner(name);
    }

    try {
        await ws.connect();
        if (!mounted) {
            van.add(App(InitialState.CONNECTED));
        }

        setTimeout(() => PlayerIcon.preload());
    } catch (err) {
        console.error(err);
        if (!mounted) {
            van.add(App(InitialState.CONNECTION_FAILED));
        }

        // todo err.message
    }

    mounted = true;
}

//     name = (target.querySelector('#username') as HTMLInputElement).value;
//         if (!name.length || name.length < 3 || name.length > 15 || !/^[a-zA-Z0-9]+$/.test(name)) {
//             return;
//         }

