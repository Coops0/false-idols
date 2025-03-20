import van from 'vanjs-core';
import { game } from '../main.ts';
import { Game } from '../game/game.ts';
const { p } = van.tags;

export enum InitialState {
    NEED_LOGIN,
    CONNECTION_FAILED,
    CONNECTED
}

export const App = (initialState: InitialState) => {
    const r = () => !!game ? p('game') : p('login');
    return p('hi');
}

const InGame = (game: Game) => {
    return p('1');
}