<template>
  <div class="p-8 max-w-md">
    <div class="text-center flex flex-col items-center">
      <h2 class="text-xl text-gray-900 mb-3">The advisor is choosing a card to play</h2>
      <div class="relative mb-3">
        <PlayerCard :player="advisor" size="lg"/>
      </div>
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