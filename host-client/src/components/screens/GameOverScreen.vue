<template>
  <div class="h-screen flex flex-col p-2 gap-2 overflow-hidden">
    <div class="flex-none bg-white/90 rounded-lg border border-gray-200 flex items-center justify-center p-3">
      <div class="text-center">
        <h1 :class="game.winner === 'ANGELS' ? 'text-blue-600' : 'text-red-600'"
            class="text-2xl md:text-3xl font-black mb-2">
          {{ game.winner === 'ANGELS' ? 'Angels Win' : 'Demonic Win' }}
        </h1>
        <p class="text-sm text-gray-600">{{ reasonText }}</p>
      </div>
    </div>

    <div class="flex-grow mt-4 bg-white/90 rounded-lg border border-gray-200 p-3 overflow-hidden">
      <div class="flex flex-col items-center justify-center gap-3 h-full">
        <p class="text-3xl font-bold text-red-500">Satan</p>
        <div class="relative">
          <PlayerCard :player="satan" icon-variant="satan" size="xl" ignore-modifiers/>
        </div>
      </div>
    </div>

    <div v-if="demons.length" class="flex-grow bg-white/90 rounded-lg border border-gray-200 p-3 overflow-hidden">
      <h3 class="text-2xl font-semibold text-red-600 mb-2 text-center">Demons</h3>
      <div class="grid grid-cols-3 gap-2">
        <div v-for="player in demons" :key="player.name" class="relative">
          <PlayerCard :player icon-variant="demon" size="xl" ignore-modifiers/>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { type GameOverGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerCard from '@/components/ui/PlayerCard.vue';

const props = defineProps<{ game: GameOverGameState }>();

const reasonText = computed(() => {
  switch (props.game.reason) {
    case 'NEGATIVE_THRESHOLD_REACHED':
      return 'The demons played enough negative cards';
    case 'POSITIVE_THRESHOLD_REACHED':
      return 'The angels played enough positive cards';
    case 'ALL_ANGELS_DEAD':
      return 'All angels were killed';
    case 'SATAN_ELECTED_ADVISOR_LATE_GAME':
      return 'Satan was elected as advisor';
    case 'SATAN_KILLED':
      return 'Satan was killed';
  }
});

const demons = computed(() => props.game.demons.map(d => props.game.players.find(p => p.name === d)!));
const satan = computed(() => props.game.players.find(p => p.name === props.game.satan)!);
</script>