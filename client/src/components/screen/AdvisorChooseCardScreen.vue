<template>
  <div>
    <p>Choose one card to <span class="bold">play</span></p>
    <CardPreview v-for="card in gameState.cards" :key="card.id" :card="card" @click="() => choose(card)"/>
  </div>
</template>

<script lang="ts" setup>
import type { AdvisorChooseCardGameState, Game } from '@/game';
import { computed } from 'vue';
import type { Card } from '@/game/messages.ts';
import CardPreview from '@/components/ui/CardPreview.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as AdvisorChooseCardGameState);
const emit = defineEmits<{ choose: [cardId: number] }>();

function choose(card: Card) {
  emit('choose', card.id);
}
</script>