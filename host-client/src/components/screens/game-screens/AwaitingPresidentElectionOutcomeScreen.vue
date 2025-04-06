<template>
  <div class="p-10 max-w-md">
    <div class="text-center flex flex-col items-center">
      <h2 class="text-xl text-gray-900 mb-3">Election</h2>
      <div class="flex items-center gap-6">
        <div class="relative">
          <PlayerCard :player="nominee" size="lg"/>
        </div>
        <p class="text-base text-gray-700">Elect as president?</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingPresidentElectionOutcomeInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerCard from '@/components/ui/PlayerCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingPresidentElectionOutcomeInnerGameState);

const nominee = computed(() => props.game.players.find(p => p.name === gameState.value.nominee)!);
</script>