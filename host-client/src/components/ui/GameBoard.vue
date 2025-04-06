<template>

</template>

<script setup lang="ts">
import { CardConsequence, type InProgressGameState } from '@/game/state.ts';

const props = defineProps<{ game: InProgressGameState }>();

// NEGATIVE CARD OUTCOMES
// 	- 7-8 players
// - 2 -> investigate
// - 3 -> pick next presidential candidate
// - 4 -> kill
// - 5 -> kill
// - 6 -> win
// - 5-6 players
// - 3 -> policy peek
// - 4 -> kill
// - 5 -> kill
// - 6 -> win
// - 9-10 players
// - 1 -> investigate
// - 2 -> investigate
// - 3 -> pick next president
// - 4 -> kill
// - 5 -> kill
// - 6 -> win

// TODO show chaos level (3 is bad)
// TODO show positive cards
// TODO show negative cards with outcomes

// const chaosLevel = props.game.failed_elections;
// const playerSize = props.game.players.length;
// const negativeCards = props.game.deck.negative_cards_played;
// const positiveCards = props.game.deck.positive_cards_played;

// SHOW CARD DESCRIPTIONS
// const negativeCards = props.game.deck.played_cards.filter(c => c.consequence === CardConsequence.NEGATIVE);
</script>
