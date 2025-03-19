import './style.css';
import { WebsocketOwner } from './websocket-owner.ts';

const ws = new WebsocketOwner('cooper');
console.log(ws.connect());