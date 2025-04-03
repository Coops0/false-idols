<template>
  <div class="p-10 max-w-lg">
    <div class="flex flex-col items-center">
      <h2 class="text-xl text-gray-900 mb-3">Investigation</h2>
      <div class="flex items-center gap-6">
        <div class="flex flex-col items-center">
          <div class="relative">
            <PlayerCard :player="chief" size="lg"/>
          </div>
        </div>

        <div class="flex items-center">
          <svg class="w-6 h-6 text-gray-500 animate-pulse" fill="none" stroke="currentColor" viewBox="0 0 24 24"
               xmlns="http://www.w3.org/2000/svg">
            <path d="M14 5l7 7m0 0l-7 7m7-7H3" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"></path>
          </svg>
        </div>

        <div class="flex flex-col items-center">
          <div class="relative">
            <PlayerCard :player="target" size="lg"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingInvestigationAnalysisInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerCard from '@/components/ui/PlayerCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingInvestigationAnalysisInnerGameState);

const chief = computed(() => props.game.players.find(p => p.is_chief)!);
const target = computed(() => props.game.players.find(p => p.name === gameState.value.target)!);
</script>