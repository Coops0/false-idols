<template>
  <div class="relative w-full max-w-md mx-auto">
    <div class="absolute inset-0 bg-gradient-to-br from-amber-100 to-amber-200 rounded-lg shadow-lg"></div>
    <div class="relative p-4 border-2 border-amber-300 rounded-lg bg-gradient-to-br from-amber-50 to-amber-100">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <div
              class="w-8 h-8 rounded-full bg-gradient-to-br from-amber-400 to-amber-600 flex items-center justify-center text-white font-bold shadow-md border border-amber-300">
            🎴
          </div>
          <span class="text-amber-900 font-semibold">Played Cards</span>
        </div>
        <div class="text-sm text-amber-800">
          {{ cards.length }} cards played
        </div>
      </div>
      <div class="mt-4 relative h-32">
        <div v-for="(card, index) in cards.slice(-3).reverse()"
             :key="index"
             :style="{
               transform: `translateY(${index * 4}px)`,
               zIndex: cards.length - index
             }"
             class="absolute w-full transition-all duration-300">
          <GameCard :card="card"/>
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