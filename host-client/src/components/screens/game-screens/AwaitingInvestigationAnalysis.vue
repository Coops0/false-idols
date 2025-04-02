<template>
  <div class="bg-white/90 rounded-lg border border-gray-200 shadow-sm p-6 max-w-md">
    <div class="flex flex-col items-center">
      <h2 class="text-lg font-bold text-gray-900 mb-3">Investigation</h2>
      <div class="flex items-center gap-6">
        <div class="flex flex-col items-center">
          <div class="relative">
            <div class="size-32 rounded-lg overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
              <img :alt="chief.name" :src="PlayerIcon.normal(chief.icon)" class="w-full h-full object-cover player-icon"/>
            </div>
            <div class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-gradient-to-br from-blue-500 to-blue-600 text-white px-2 py-0.5 rounded-lg text-sm font-medium shadow-sm border border-blue-400/50">
              {{ chief.name }}
            </div>
            <div class="absolute -top-2 -right-2 w-6 h-6 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center text-white text-xs font-bold shadow-sm border border-blue-400/50">
              👑
            </div>
          </div>
        </div>

        <div class="flex items-center">
          <svg class="w-6 h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path>
          </svg>
        </div>

        <div class="flex flex-col items-center">
          <div class="relative">
            <div class="w-28 h-28 rounded-lg overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
              <img :alt="target.name" :src="PlayerIcon.normal(target.icon)" class="w-full h-full object-cover player-icon"/>
            </div>
            <div class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-gradient-to-br from-purple-500 to-purple-600 text-white px-2 py-0.5 rounded-lg text-sm font-medium shadow-sm border border-purple-400/50">
              {{ target.name }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingInvestigationAnalysisInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingInvestigationAnalysisInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
const target = computed(() => props.game.players.find(p => p.name === gameState.value.target)!);
</script>