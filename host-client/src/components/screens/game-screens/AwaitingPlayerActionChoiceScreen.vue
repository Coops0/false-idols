<template>
  <div class="bg-white/90 rounded-lg border border-gray-200 shadow-sm p-6">
    <div class="text-center">
      <div class="flex justify-center mb-4">
        <div class="relative">
          <div class="size-32 rounded-lg overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
            <img :alt="chief.name" :src="PlayerIcon.normal(chief.icon)" class="w-full h-full object-cover"/>
          </div>
          <div
              class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-gradient-to-br from-blue-500 to-blue-600 text-white px-2 py-0.5 rounded-lg text-sm font-medium shadow-sm border border-blue-400/50">
            {{ chief.name }}
          </div>
          <div
              class="absolute -top-2 -right-2 w-6 h-6 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center text-white text-xs font-bold shadow-sm border border-blue-400/50">
            👑
          </div>
        </div>
      </div>

      <div class="bg-gray-50 rounded-lg p-3 mx-auto max-w-xs">
        <p class="text-base text-gray-700">The chief is deciding...</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingChiefActionChoiceInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingChiefActionChoiceInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
</script>