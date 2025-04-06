<template>
  <div class="p-10">
    <div class="text-center">
      <div class="flex justify-center items-center flex-col">
        <div class="relative">
          <PlayerCard :player="advisor" size="xl"/>
        </div>
        <p class="text-base text-gray-700">Choosing a card to play</p>
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