import type { IconType } from '@/game/player-icon.ts';

export enum ActionChoice {
    INVESTIGATE = 'INVESTIGATE',
    KILL = 'KILL',
    NOMINATE = 'NOMINATE',
    NOMINATE_PRESIDENT = 'NOMINATE_PRESIDENT',
}

export type Player = { name: string, icon: IconType };

export enum Role {
    SATAN = 'SATAN',
    DEMON = 'DEMON',
    ANGEL = 'ANGEL',
}

export type SimpleRole = Role.DEMON | Role.ANGEL;

export const roleName = (role: Role | SimpleRole) => {
    switch (role) {
        case Role.ANGEL:
            return 'Angel';
        case Role.DEMON:
            return 'Demon';
        case Role.SATAN:
            return 'Satan';
    }
};

export type ActionSupplementedPlayer = Player & {
    investigatable: boolean,
    electable: boolean
}

export enum CardConsequence {
    POSITIVE = 'POSITIVE',
    NEGATIVE = 'NEGATIVE',
    NEUTRAL = 'NEUTRAL'
}

export type Card = {
    id: number;
    description: string;
    consequence: CardConsequence;
}

export type OutboundMessage =
/// Our response to play an action on a player.
    { type: 'choose_action', target: string } |
    /// As president, which card we pick to **discard**.
    { type: 'discard_one_card', card_id: number } |
    /// As advisor, which card we pick to play.
    { type: 'choose_card', card_id: number } |
    { type: 'ping', request_icon?: boolean };


//@formatter:off
export type InboundMessage =
    /// Game start, server has assigned a role to player.
    { type: 'assign_role', role: Role.ANGEL, demon_count: number, demons?: Player[] | null, satan?: Player | null, is_small_game: boolean } |
    //@formatter:on
    /// The server is requesting us to play an action, most likely when we are president.
    /// It will supply the players we can run actions on, and the actions we can run.
    /// For an action to be valid, it has to be both a permitted action, and be valid on the ActionSupplementedPlayer.
    { type: 'request_action', action: ActionChoice, players: ActionSupplementedPlayer[] } |
    /// We are president, the server wants us to choose one card to remove before letting
    /// the advisor pick from the leftover two.
    /// ASSERT: cards.length === 3
    { type: 'request_president_card_discard', cards: Card[] } |
    /// We are advisor, the president has discard one card, we need to pick one of the two cards.
    /// ASSERT: cards.length === 2
    { type: 'request_advisor_card_choice', cards: Card[], vetoable: boolean } |
    /// We chose to investigate another player, this is their 'simple role'.
    /// (same thing as normal role, but if Satan then remap to Demon)
    { type: 'investigation_result', target: Player, simple_role: SimpleRole } |
    /// This should be sent as soon as we join, it just tells us what our icon is.
    { type: 'assign_icon', icon: IconType } |
    /// We get to see the top three cards of the deck.
    { type: 'policy_peek', cards: Card[] } |
    /// This is rare, it's (right now) only used when the server chooses to reset all players from the lobby.
    { type: 'disconnect', reason: string };