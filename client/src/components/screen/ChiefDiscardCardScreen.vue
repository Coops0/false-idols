<template>
  <div>
    <p>Choose one card to <span class="bold">discard</span></p>
    <CardPreview v-for="card in gameState.cards" :key="card.id" :card="card" @click="() => discard(card)"/>
  </div>
</template>

<script lang="ts" setup>
import CardPreview from '@/components/ui/CardPreview.vue';
import { computed } from 'vue';
import { type ChiefDiscardCardGameState, Game } from '@/game';
import type { Card } from '@/game/messages.ts';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as ChiefDiscardCardGameState);
const emit = defineEmits<{ discard: [cardId: number] }>();

function discard(card: Card) {
  emit('discard', card.id);
}
</script>