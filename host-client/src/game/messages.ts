import type { GameState } from '@/game/state.ts';

export type ServerOutboundMessage =
    { type: 'reset_players' } |
    { type: 'start_game' } |
    { type: 'resolve_election', passed: boolean } |
    { type: 'skip' } |
    { type: 'go_back_to_lobby' } |
    { type: 'ping' };

export type ServerInboundMessage = { type: 'update_game_state', game_state: GameState };