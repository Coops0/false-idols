<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 p-4">
    <BaseCard class="w-full max-w-4xl">
      <template #header>
        <h1 class="text-2xl font-bold text-gray-800 text-center">Discard a Card</h1>
        <p class="text-gray-600 text-center mt-2">
          Choose one card to <span class="font-bold text-red-600">discard</span>
        </p>
        <p class="text-gray-300 text-center mt-1">(Nobody will see the card you discard)</p>
      </template>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
            v-for="card in gameState.cards"
            :key="card.id"
            @click="() => discard(card)"
        >
          <CardPreview :card="card"/>
        </div>
      </div>
    </BaseCard>
  </div>
</template>

<script lang="ts" setup>
import CardPreview from '@/components/ui/CardPreview.vue';
import { computed } from 'vue';
import { type ChiefDiscardCardGameState, Game } from '@/game';
import type { Card } from '@/game/messages.ts';
import BaseCard from '@/components/ui/BaseCard.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as ChiefDiscardCardGameState);
const emit = defineEmits<{ discard: [cardId: number] }>();

function discard(card: Card) {
  emit('discard', card.id);
}
</script>