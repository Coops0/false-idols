import './style.css';
import { WebsocketOwner } from './websocket-owner.ts';
import { HtmlTemplate } from './html-template.ts';
import { Game } from './game.ts';

const root: HTMLElement = document.getElementById('root')!;
let isLoginMounted = false;

root.addEventListener('submit', handleSubmit);
root.addEventListener('click', handleClick);

export let ws: WebsocketOwner | null = null;
export let game: Game | null = null;
export function setGame(g: Game) { game = g }

let name: string = localStorage.getItem('name') ?? '';

if (name.length) {
    tryToConnect();
} else {
    mountLogin();
}

async function tryToConnect() {
    if (ws === null) {
        ws = new WebsocketOwner(name);
    }

    try {
        await ws.connect();
        unmountLogin();
    } catch (err) {
        console.error(err);
        if (!isLoginMounted) {
            mountLogin();
        }

        // @ts-ignore
        showError(err.message ?? 'Failed to connect');
    }
}

function handleSubmit(event: Event) {
    event.preventDefault();
    const target = event.target as HTMLElement;

    if (target.id === 'login-form') {
        name = (target.querySelector('#username') as HTMLInputElement).value;
        if (!name.length || name.length < 3 || name.length > 15 || !/^[a-zA-Z0-9]+$/.test(name)) {
            return;
        }

        tryToConnect();
    }
}

function handleClick(event: Event) {
    const target = event.target as HTMLElement;
    if (target.matches('.delete-button')) {

    }
}

function mountLogin() {
    isLoginMounted = true;
    root.insertAdjacentHTML('beforeend', HtmlTemplate.login());
}

function unmountLogin() {
    isLoginMounted = false;
    root.querySelector('#login-form')?.remove();
}

function showError(message: string) {
    const form = document.querySelector('#login-form')!;

    let error = form.querySelector('#error');
    if (!error) {
        error = document.createElement('div');
        error.id = 'error';
        error.classList.add('text-red-500');
        form.appendChild(error);
    }

    error.textContent = message;
}