<template>
  <div class="h-screen flex flex-col p-2 gap-2 overflow-hidden">
    <div class="flex-none bg-white/90 rounded-lg border border-gray-200 flex items-center justify-center p-3">
      <div class="text-center">
        <h1 class="text-3xl font-bold text-gray-900 mb-1">False Idols</h1>
        <p class="text-lg text-gray-600 mb-1">Join at</p>
        <div class="p-2 bg-gray-50 rounded-lg inline-block border border-gray-200">
          <code class="text-xl font-mono text-gray-900">{{ domain }}</code>
        </div>
      </div>
    </div>

    <div class="flex-grow bg-white/90 rounded-lg border border-gray-200 p-3 overflow-hidden">
      <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3 h-full">
        <div v-for="player in game.players" :key="player.name"
             class="bg-white rounded-lg border border-gray-200 p-2 flex items-center gap-2">
          <div class="w-18 h-18 rounded-lg overflow-hidden border-2 border-gray-200 flex-shrink-0">
            <img :alt="player.name" :src="PlayerIcon.normal(player.icon)" class="w-full h-full object-cover"/>
          </div>
          <div class="flex-1 min-w-0">
            <h3 class="text-sm font-medium text-gray-900 truncate">{{ player.name }}</h3>
          </div>
        </div>
      </div>
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