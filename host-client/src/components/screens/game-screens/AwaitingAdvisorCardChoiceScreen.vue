<template>
  <div class="bg-white/90 rounded-lg border border-gray-200 shadow-sm p-6 max-w-md">
    <div class="text-center flex flex-col items-center">
      <div class="relative mb-3">
        <div class="size-32 rounded-lg overflow-hidden border-2 border-gray-100 shadow-sm bg-white">
          <img :alt="advisor.name" :src="PlayerIcon.normal(advisor.icon)" class="w-full h-full object-cover"/>
        </div>
        <div class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-gradient-to-br from-purple-500 to-purple-600 text-white px-2 py-0.5 rounded-lg text-sm font-medium shadow-sm border border-purple-400/50">
          {{ advisor.name }}
        </div>
      </div>
      <p class="text-base text-gray-700">The advisor choosing which card to choose...</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingAdvisorCardChoiceInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingAdvisorCardChoiceInnerGameState);

const advisor = computed(() => props.game.players.find(p => p.name === gameState.value.advisor_name)!);
</script>