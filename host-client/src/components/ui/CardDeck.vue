<template>
  <div class="relative w-full max-w-md mx-auto">
    <div class="bg-white/50 backdrop-blur-sm rounded-xl p-6 border border-gray-100/50">
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center gap-3">
          <div
              class="w-10 h-10 rounded-xl bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center text-white font-bold shadow-sm border border-purple-400/50">
            🎴
          </div>
          <span class="text-gray-900 font-medium text-lg">Played Cards</span>
        </div>
      </div>
      <div class="relative h-32">
        <div v-for="(card, index) in cards.slice(-3).reverse()"
             :key="index"
             :style="{
               transform: `translateY(${index * 4}px)`,
               zIndex: cards.length - index
             }"
             class="absolute w-full">
          <GameCard :card/>
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