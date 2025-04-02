<template>
  <div class="w-full h-full flex items-center justify-center">
    <div class="bg-white/50 backdrop-blur-sm rounded-lg p-2 border border-gray-100/50 w-full h-full flex flex-col">
      <div class="flex items-center gap-1 mb-1">
        <div
            class="w-4 h-4 rounded-md bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center text-white text-xs font-bold">
          🎴
        </div>
        <span class="text-gray-900 font-medium text-xs">Played Cards</span>
      </div>
      <div class="relative flex-grow flex items-center justify-center">
        <div v-if="cards.length === 0" class="text-gray-400 text-xs text-center">
          No cards played
        </div>
        <div v-else class="flex justify-center gap-2">
          <div v-for="(card, index) in cards.slice(-2).reverse()" :key="index"
               class="w-20 h-24">
            <GameCard :card class="w-full h-full" :class="index === 0 && 'ring-yellow-400/60 ring-offset-2 ring-1'"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { InProgressGameState } from '@/game/state.ts';
import GameCard from '@/components/ui/GameCard.vue';
import { computed } from 'vue';

const props = defineProps<{ game: InProgressGameState }>();
const cards = computed(() => props.game.deck.played_cards);
</script>