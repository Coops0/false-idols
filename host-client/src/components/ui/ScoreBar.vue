<template>
  <div class="relative w-full max-w-md mx-auto">
    <div class="bg-white/50 backdrop-blur-sm rounded-xl p-6 border border-gray-100/50">
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center gap-3">
          <div
              class="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center text-white font-bold shadow-sm border border-blue-400/50">
            ⚖️
          </div>
          <span class="text-gray-900 font-medium text-lg">Score</span>
        </div>
      </div>
      <div class="relative h-8">
        <div class="absolute inset-0 bg-gradient-to-r from-red-500/10 via-blue-500/10 to-green-500/10 rounded-full"/>
        <div class="absolute inset-0 flex items-center">
          <div
              :style="{
                 left: `${50 + (score / 2) * 50}%`,
                 transform: 'translateX(-50%)',
                 transition: 'left 0.5s ease-in-out'
               }"
              class="absolute w-2 h-6 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full shadow-sm border border-blue-400/50">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';

const props = defineProps<{ game: InProgressGameState }>();
const score = computed(() => props.game.deck.played_cards.reduce((acc, card) => acc + card.consequence, 0));
</script>