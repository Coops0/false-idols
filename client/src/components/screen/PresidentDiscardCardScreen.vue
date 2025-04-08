<template>
  <div class="size-full flex flex-col items-center justify-center">
    <p class="text-xl text-gray-600 text-center mt-2">
      Choose one card to <span class="font-bold text-red-600">discard</span>
    </p>

    <div class="mt-6 flex items-center flex-row flex-wrap justify-evenly gap-2 relative">
      <PlayedGameCard
          v-for="card in gameState.cards"
          :key="card.id"
          :card
          class="w-30"
          @click="() => discard(card)"
      />
    </div>

    <p class="mt-14 text-xs text-gray-800 font-bold">You cannot show anyone this screen</p>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { Game, type PresidentDiscardCardGameState } from '@/game';
import type { Card } from '@/game/messages.ts';
import PlayedGameCard from '@/components/ui/PlayedGameCard.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as PresidentDiscardCardGameState);
const emit = defineEmits<{ discard: [cardId: number] }>();

function discard(card: Card) {
  emit('discard', card.id);
}
</script>