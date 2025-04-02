<template>
  <div class="bg-white/90 rounded-lg border border-gray-200 shadow-sm p-6 max-w-md">
    <div class="text-center flex flex-col items-center">
      <h2 class="text-lg font-bold text-gray-900 mb-3">Election</h2>
      <div class="flex items-center gap-6">
        <div class="relative">
          <div class="size-32 rounded-lg overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
            <img :alt="nominee.name" :src="PlayerIcon.normal(nominee.icon)" class="w-full h-full object-cover player-icon"/>
          </div>
          <div class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-gradient-to-br from-blue-500 to-blue-600 text-white px-2 py-0.5 rounded-lg text-sm font-medium shadow-sm border border-blue-400/50">
            {{ nominee.name }}
          </div>
        </div>
        <p class="text-base text-gray-700">Elect as advisor?</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingElectionOutcomeInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingElectionOutcomeInnerGameState);

const nominee = computed(() => props.game.players.find(p => p.name === gameState.value.nominee)!);
</script>