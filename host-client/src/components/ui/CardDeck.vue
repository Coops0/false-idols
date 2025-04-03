<template>
  <div class="size-full flex items-center justify-center">
    <div class="p-2 size-full flex flex-col">
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
        <div v-else class="flex justify-center gap-3">
          <div v-for="(card, index) in cards.slice(-3).reverse()" :key="index"
               class="w-20 h-24">
            <GameCard :card :class="index === 0 && '!border-yellow-400/60 border-3'" class="size-full"/>
          </div>
          <div v-if="cards.length > 3" class="mt-auto text-gray-400 text-xs">...</div>
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