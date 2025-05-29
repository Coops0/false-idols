<template>
  <div class="flex flex-col items-center">
    <div class="flex justify-center items-center flex-col">
      <div class="relative">
        <PlayerPreview :player="nominee" size="2xl"/>
      </div>
      <p class="text-gray-700 font-amazonia font-light text-center text-3xl">Elect as president?</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingPresidentElectionOutcomeInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingPresidentElectionOutcomeInnerGameState);

const nominee = computed(() => props.game.players.find(p => p.name === gameState.value.nominee)!);
</script>