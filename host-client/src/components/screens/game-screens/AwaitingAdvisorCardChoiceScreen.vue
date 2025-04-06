<template>
  <div class="flex flex-col items-center">
    <div class="flex justify-center items-center flex-col">
      <div class="relative">
        <PlayerPreview :player="advisor" size="2xl"/>
      </div>
      <p class="text-gray-700 text-center text-3xl">Choosing a card to play</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingAdvisorCardChoiceInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingAdvisorCardChoiceInnerGameState);

const advisor = computed(() => props.game.players.find(p => p.name === gameState.value.advisor_name)!);
</script>