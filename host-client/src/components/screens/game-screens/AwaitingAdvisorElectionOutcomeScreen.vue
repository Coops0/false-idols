<template>
  <div class="p-10">
    <div class="text-center">
      <div class="flex justify-center items-center flex-col">
        <div class="relative">
          <PlayerCard :player="nominee" size="xl"/>
        </div>
        <p class="text-base text-gray-700">Elect as advisor?</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingAdvisorElectionOutcomeInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerCard from '@/components/ui/PlayerCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingAdvisorElectionOutcomeInnerGameState);

const nominee = computed(() => props.game.players.find(p => p.name === gameState.value.nominee)!);
</script>