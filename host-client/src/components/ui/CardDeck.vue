<template>
  <div>
    <Card v-if="cards.length > 0" :card="cards[cards.length - 1]"/>
    <Card v-if="cards.length > 1" :card="cards[cards.length - 2]"/>
  </div>
</template>

<script lang="ts" setup>
import type { InProgressGameState } from '@/game/state.ts';
import Card from '@/components/ui/Card.vue';
import { computed } from 'vue';

const props = defineProps<{ game: InProgressGameState }>();
const cards = computed(() => props.game.deck.played_cards);
// todo show the last 2 played cards like on a stack
</script>