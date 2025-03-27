<template>
  <div class="relative w-full max-w-md mx-auto">
    <div class="absolute inset-0 bg-gradient-to-br from-blue-100 to-blue-200 rounded-lg shadow-lg"></div>
    <div class="relative p-4 border-2 border-blue-300 rounded-lg bg-gradient-to-br from-blue-50 to-blue-100">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <div
              class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-bold shadow-md border border-blue-300">
            ⚖️
          </div>
          <span class="text-blue-900 font-semibold">Divine Balance</span>
        </div>
        <div class="text-sm text-blue-800">
          {{ score > 0 ? 'Divine Favor' : score < 0 ? 'Demonic Influence' : 'Perfect Balance' }}
        </div>
      </div>
      <div class="mt-4 relative h-8">
        <div
            class="absolute inset-0 bg-gradient-to-r from-red-500 via-blue-500 to-green-500 rounded-full opacity-20"></div>
        <div class="absolute inset-0 flex items-center">
          <div
              :style="{
                 left: `${50 + (score / 2) * 50}%`,
                 transform: 'translateX(-50%)',
                 transition: 'left 0.5s ease-in-out'
               }"
              class="absolute w-2 h-6 bg-gradient-to-br from-blue-600 to-blue-800 rounded-full shadow-md border border-blue-400">
          </div>
        </div>
      </div>
      <div class="mt-2 flex justify-between text-xs text-blue-800">
        <span>Demonic</span>
        <span>Divine</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
// todo show score with threshold of negative and positive
// make it like a bar line, don't show exact score, but show the 2 bounds and make it look cool
import type { InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';

const props = defineProps<{ game: InProgressGameState }>();
const score = computed(() => props.game.deck.played_cards.reduce((acc, card) => acc + card.consequence, 0));
</script>