<template>
  <div class="flex flex-col gap-4">
    <PolicyTrack
        variant="angel"
        :cards-played="props.game.deck.positive_cards_played"
        :player-count="props.game.players.length"
        :descriptions="positiveDescriptions"
    />

    <PolicyTrack
        variant="demon"
        :cards-played="props.game.deck.negative_cards_played"
        :player-count="props.game.players.length"
        :descriptions="negativeDescriptions"
    />

    <div class="flex items-center justify-center gap-2 mt-2">
      <div v-for="i in 3" :key="i"
           :class="{
             'bg-red-500': i <= props.game.failed_elections,
             'bg-gray-200': i > props.game.failed_elections
           }"
           class="w-3 h-3 rounded-full transition-colors duration-300">
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { CardConsequence, type InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PolicyTrack from './PolicyTrack.vue';

const props = defineProps<{ game: InProgressGameState }>();

const positiveDescriptions = computed(() =>
    props.game.deck.played_cards
        .filter(c => c.consequence === CardConsequence.POSITIVE)
        .map(c => c.description));

const negativeDescriptions = computed(() =>
    props.game.deck.played_cards
        .filter(c => c.consequence === CardConsequence.NEGATIVE)
        .map(c => c.description));
</script>
