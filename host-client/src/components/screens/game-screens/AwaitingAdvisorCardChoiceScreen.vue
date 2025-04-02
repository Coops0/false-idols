<template>
  <div class="p-8 max-w-md">
    <div class="text-center flex flex-col items-center">
      <div class="relative mb-3">
        <PlayerCard :player="advisor" size="lg"/>
      </div>
      <p class="text-base text-gray-700">The advisor choosing which card to choose...</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingAdvisorCardChoiceInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerCard from '@/components/ui/PlayerCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingAdvisorCardChoiceInnerGameState);

const advisor = computed(() => props.game.players.find(p => p.name === gameState.value.advisor_name)!);
</script>