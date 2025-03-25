<template>
  <div>
    <p>Score: {{ score }}</p>
  </div>
</template>

<script lang="ts" setup>
// todo show score with threshold of negative and positive
import type { InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';

const props = defineProps<{ game: InProgressGameState }>();
const score = computed(() => props.game.deck.played_cards.reduce((acc, card) => acc + card.consequence, 0));
</script>