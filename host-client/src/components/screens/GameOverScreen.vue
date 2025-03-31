<template>
  <div class="h-screen flex flex-col p-4 gap-2">
    <div class="h-[15vh] bg-white/90 rounded-lg border border-gray-200 flex items-center justify-center">
      <div class="text-center">
        <h1 :class="game.winner === 'ANGELS' ? 'text-blue-600' : 'text-red-600'" class="text-5xl font-bold mb-2">
          {{ game.winner === 'ANGELS' ? 'Angels Win' : 'Demonic Win' }}
        </h1>
        <p class="text-xl text-gray-600">{{ reasonText }}</p>
      </div>
    </div>

    <div class="h-[35vh] bg-white/90 rounded-lg border border-gray-200 p-4">
      <div class="flex items-center justify-center gap-8 h-full">
        <div class="relative">
          <div class="w-40 h-40 rounded-lg overflow-hidden border-4 border-red-600">
            <img :alt="satan.name" :src="PlayerIcon.satan(satan.icon)" class="w-full h-full object-cover"/>
          </div>
          <div
              class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-red-600 text-white px-3 py-1 rounded-lg text-sm font-medium">
            Satan
          </div>
        </div>
        <div class="text-left">
          <h2 class="text-3xl font-bold text-red-600 mb-2">{{ satan.name }}</h2>
        </div>
      </div>
    </div>

    <div v-if="demons.length" class="h-[35vh] bg-white/90 rounded-lg border border-gray-200 p-4">
      <h3 class="text-2xl font-semibold text-red-600 mb-6 text-center">Demons</h3>
      <div class="grid grid-cols-4 gap-6 h-full">
        <div v-for="player in demons" :key="player.name" class="relative">
          <div class="w-32 h-32 rounded-lg overflow-hidden border-4 border-red-500">
            <img :alt="player.name" :src="PlayerIcon.demon(player.icon)" class="w-full h-full object-cover"/>
          </div>
          <div
              class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-red-500 text-white px-3 py-1 rounded-lg text-sm font-medium">
            {{ player.name }}
          </div>
        </div>
      </div>
    </div>

    <div class="h-[15vh] bg-white/90 rounded-lg border border-gray-200 flex items-center justify-center">
      <p class="text-xl text-gray-600">Game Over</p>
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

<style scoped>
* {
  transition: none !important;
  animation: none !important;
}
</style>