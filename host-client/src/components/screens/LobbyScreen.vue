<template>
  <div class="h-full flex flex-col p-2 gap-2 overflow-hidden">
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
        <PlayerCard v-for="player in game.players" :key="player.name" :player size="lg"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { LobbyGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerCard from '@/components/ui/PlayerCard.vue';

defineProps<{ game: LobbyGameState }>();
const domain = computed(() => window?.location?.hostname ?? 'LOADING');
</script>