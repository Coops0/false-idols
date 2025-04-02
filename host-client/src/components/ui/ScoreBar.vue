<template>
  <div class="size-full flex items-center justify-center">
    <div class="p-2 size-full flex flex-col">
      <div class="flex items-center gap-1 mb-1">
        <div
            class="w-4 h-4 rounded-md bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center text-white text-xs font-bold">
          ⚖️
        </div>
        <span class="text-gray-900 font-medium text-xs">Score</span>
      </div>
      <div class="flex-grow flex items-center justify-center px-2">
        <div class="relative w-full h-4">
          <div class="absolute inset-0 bg-gradient-to-r from-red-500/50 via-blue-500/10 to-green-500/50 rounded-full"/>
          <div class="absolute inset-0 flex items-center">
            <div
                :style="{
                  left: `${leftPercent}%`,
                  transform: 'translateX(-50%)',
                  transition: 'left 0.5s ease-in-out'
                }"
                class="absolute w-3 h-6 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full shadow-sm border border-blue-400/50">
            </div>
          </div>
        </div>
      </div>
      <div class="flex justify-between text-xs text-gray-500 px-1">
        <div>Demons</div>
        <div>Angels</div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';

const props = defineProps<{ game: InProgressGameState }>();
const score = computed(() => props.game.deck.played_cards.reduce((acc, card) => acc + card.consequence, 0));

const min = -5;
const max = 11;
const leftPercent = computed(() => {

  if (score.value < 0) {
    return ((score.value - min) / (0 - min)) * 50;
  } else {
    return 50 + (score.value / max) * 50;
  }
});
</script>
