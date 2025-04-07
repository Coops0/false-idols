<template>
  <div class="size-full flex items-center justify-center">
    <BaseCard class="w-full max-w-4xl mx-4">
      <template #header>
        <h1 class="text-xl font-bold text-gray-800 text-center">Discard a Card</h1>
        <p class="text-sm text-gray-600 text-center mt-2">
          Choose one card to <span class="font-bold text-red-600">discard</span>
        </p>
      </template>

      <div class="grid grid-cols-2 gap-2">
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
        <p class="text-xs text-gray-800 font-bold">You cannot show anyone this screen</p>
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