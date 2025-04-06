<template>
  <div class="min-h-screen flex items-center justify-center p-4">
    <BaseCard class="w-full max-w-4xl mx-4">
      <template #header>
        <h1 class="text-xl md:text-2xl font-bold text-gray-800 text-center">Discard a Card</h1>
        <p class="text-sm md:text-base text-gray-600 text-center mt-2">
          Choose one card to <span class="font-bold text-red-600">discard</span>
        </p>
        <p class="text-xs md:text-sm text-gray-500 text-center mt-1">(Nobody will see the card you discard)</p>
      </template>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
        <div
            v-for="card in gameState.cards"
            :key="card.id"
            class="cursor-pointer active:scale-95 transition-transform"
            @click="() => discard(card)"
        >
          <CardPreview :card/>
        </div>
      </div>

      <div class="mt-6">
        <p class="text-xs md:text-sm text-gray-800 font-bold">You cannot show anyone this screen</p>
      </div>
    </BaseCard>
  </div>
</template>

<script lang="ts" setup>
import CardPreview from '@/components/ui/CardPreview.vue';
import { computed } from 'vue';
import { Game, type PresidentDiscardCardGameState } from '@/game';
import type { Card } from '@/game/messages.ts';
import BaseCard from '@/components/ui/BaseCard.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as PresidentDiscardCardGameState);
const emit = defineEmits<{ discard: [cardId: number] }>();

function discard(card: Card) {
  emit('discard', card.id);
}
</script>