<template>
  <div class="h-screen flex flex-col p-4 gap-2">
    <div class="h-[15vh] bg-white/90 rounded-lg border border-gray-200 flex items-center justify-center">
      <div class="text-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-2">False Idols</h1>
        <p class="text-xl text-gray-600 mb-2">Join at</p>
        <div class="p-3 bg-gray-50 rounded-lg inline-block border border-gray-200">
          <code class="text-2xl font-mono text-gray-900">{{ domain }}</code>
        </div>
      </div>
    </div>

    <div class="h-[35vh] bg-white/90 rounded-lg border border-gray-200 p-4">
      <h2 class="text-2xl font-semibold text-gray-900 mb-6 text-center">Waiting for Players</h2>
      <div class="grid grid-cols-4 gap-4 h-full">
        <div v-for="player in game.players" :key="player.name"
             class="bg-white rounded-lg border border-gray-200 p-3 flex items-center gap-3">
          <div class="w-16 h-16 rounded-lg overflow-hidden border-2 border-gray-200">
            <img :alt="player.name" :src="PlayerIcon.normal(player.icon)" class="w-full h-full object-cover"/>
          </div>
          <div class="flex-1 min-w-0">
            <h3 class="text-lg font-medium text-gray-900 truncate">{{ player.name }}</h3>
            <p class="text-sm text-gray-500">Waiting to join...</p>
          </div>
        </div>
      </div>
    </div>

    <div class="h-[35vh] bg-white/90 rounded-lg border border-gray-200 p-4">
      <div class="h-full flex flex-col items-center justify-center text-center">
        <h2 class="text-2xl font-semibold text-gray-900 mb-4">Game Rules</h2>
        <p class="text-lg text-gray-600 max-w-2xl">
          Players will be randomly assigned roles as either Angels or Demons.
          The Demons must work together to achieve their goals while remaining hidden.
          The Angels must identify and eliminate the Demons.
        </p>
      </div>
    </div>

    <div class="h-[15vh] bg-white/90 rounded-lg border border-gray-200 flex items-center justify-center">
      <p class="text-xl text-gray-600">Waiting for players to join...</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { LobbyGameState } from '@/game/state.ts';
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';

defineProps<{ game: LobbyGameState }>();
const domain = computed(() => window?.location?.hostname ?? 'LOADING');
</script>

<style scoped>
* {
  transition: none !important;
  animation: none !important;
}
</style>