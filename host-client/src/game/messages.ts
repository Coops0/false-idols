import type { GameState } from '@/game/state.ts';

export type FalseIdolsErrorType = 'ASSERTION' | 'ILLEGAL_ARGUMENT' | 'ILLEGAL_STATE';

export type FalseIdolsError = {
    error_type: FalseIdolsErrorType;
    player_name: string | null;
    message: string;
}

export type ServerOutboundMessage =
    { type: 'reset_players' } |
    { type: 'start_game' } |
    { type: 'resolve_election', passed: boolean } |
    { type: 'skip' } |
    { type: 'go_back_to_lobby' } |
    { type: 'veto' } |
    { type: 'ping' };


export type ServerInboundMessage =
    { type: 'update_game_state', game_state: GameState } |
    { type: 'error', error: FalseIdolsError } |
    { type: 'policy_peeking' };