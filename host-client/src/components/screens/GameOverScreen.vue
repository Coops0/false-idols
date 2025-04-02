<template>
  <div class="h-screen flex flex-col p-2 gap-2 overflow-hidden">
    <div class="flex-none bg-white/90 rounded-lg border border-gray-200 flex items-center justify-center p-3">
      <div class="text-center">
        <h1 :class="game.winner === 'ANGELS' ? 'text-blue-600' : 'text-red-600'"
            class="text-2xl md:text-3xl font-bold mb-1">
          {{ game.winner === 'ANGELS' ? 'Angels Win' : 'Demonic Win' }}
        </h1>
        <p class="text-sm md:text-base text-gray-600">{{ reasonText }}</p>
      </div>
    </div>

    <div class="flex-grow bg-white/90 rounded-lg border border-gray-200 p-3 overflow-hidden">
      <div class="flex flex-col items-center justify-center gap-3 h-full">
        <div class="relative">
          <div class="w-28 h-28 rounded-lg overflow-hidden border-4 border-red-600">
            <img :alt="satan.name" :src="PlayerIcon.satan(satan.icon)" class="w-full h-full object-cover player-icon"/>
          </div>
          <div class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-red-600 text-white px-2 py-0.5 rounded-lg text-xs font-medium">
            Satan
          </div>
        </div>
        <div class="text-center">
          <h2 class="text-lg md:text-xl font-bold text-red-600">{{ satan.name }}</h2>
        </div>
      </div>
    </div>

    <div v-if="demons.length" class="flex-grow bg-white/90 rounded-lg border border-gray-200 p-3 overflow-hidden">
      <h3 class="text-lg font-semibold text-red-600 mb-2 text-center">Demons</h3>
      <div class="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 lg:grid-cols-6 xl:grid-cols-9 gap-2">
        <div v-for="player in demons" :key="player.name" class="relative">
          <div class="w-24 h-24 rounded-lg overflow-hidden border-3 border-red-500 mx-auto">
            <img :alt="player.name" :src="PlayerIcon.demon(player.icon)" class="w-full h-full object-cover player-icon"/>
          </div>
          <div class="absolute -bottom-1 left-1/2 transform -translate-x-1/2 bg-red-500 text-white px-1.5 py-0.5 rounded-lg text-xs font-medium">
            {{ player.name }}
          </div>
        </div>
      </div>
    </div>

    <div class="flex-none bg-white/90 rounded-lg border border-gray-200 flex items-center justify-center p-2">
      <p class="text-base text-gray-600">Game Over</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { type GameOverGameState } from '@/game/state.ts';
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';

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