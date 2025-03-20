export enum ActionChoice {
    INVESTIGATE = 'INVESTIGATE',
    KILL = 'KILL',
    NOMINATE = 'NOMINATE'
}

export type Player = { name: string, icon: string };

export enum Role {
    SATAN = 'SATAN',
    DEMON = 'DEMON',
    ANGEL = 'ANGEL',
}

export type SimpleRole = Role.DEMON | Role.ANGEL;

export type ActionSupplementedPlayer = Player & {
    investigatable: boolean,
    electable: boolean
}

export enum ConsequenceQualifier {
    POSITIVE = 'POSITIVE',
    NEGATIVE = 'NEGATIVE',
    NEUTRAL = 'NEUTRAL'
}

export type Card = {
    id: number;
    description: string;
    consequence: number;
    consequence_qualifier: ConsequenceQualifier;
}

export type OutboundMessage =
/// Our response to play an action on a player.
    { type: 'choose_action', action: ActionChoice, target: string } |
    /// As chief, which card we pick to **discard**.
    { type: 'discard_one_card', card_id: number } |
    /// As advisor, which card we pick to play.
    { type: 'choose_card', card_id: number } |
    /// Client side just send every ~15s, ignored by the server.
    { type: 'ping' };


//@formatter:off
export type InboundMessage =
/// Game start, server has assigned a role to player.
/// If role == Satan or Angel, send no additional data.
    { type: 'assign_role', role: Role.SATAN | Role.ANGEL, demon_count: number } |
    /// Game start cont.
    /// If role == Demon, send fellow teammates and who satan is
    { type: 'assign_role', role: Role.DEMON, demon_count: number, teammates: Player[], satan: Player } |
    /// The server is requesting us to play an action, most likely when we are chief.
    /// It will supply the players we can run actions on, and the actions we can run.
    /// For an action to be valid, it has to be both a permitted action, and be valid on the ActionSupplementedPlayer.
    { type: 'request_action', permitted_actions: ActionChoice[], players: ActionSupplementedPlayer[] } |
    /// We are chief, the server wants us to choose one card to remove before letting
    /// the advisor pick from the leftover two.
    /// ASSERT: cards.length === 3
    { type: 'request_chief_card_discard', cards: Card[] } |
    /// We are advisor, the chief has discard one card, we need to pick one of the two cards.
    /// ASSERT: cards.length === 2
    { type: 'request_advisor_card_choice', cards: Card[] } |
    /// We chose to investigate another player, this is their 'simple role'.
    /// (same thing as normal role, but if Satan then remap to Demon)
    { type: 'investigation_result', target: Player, simple_role: SimpleRole } |
    /// This is rare, it's (right now) only used when the server chooses to reset all players from the lobby.
    { type: 'disconnect', reason: string } |
    /// This should be sent as soon as we join, it just tells us what our icon is.
    { type: 'assign_icon', icon: string };
//@formatter:on