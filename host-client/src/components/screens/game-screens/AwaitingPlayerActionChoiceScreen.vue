<template>
  <div class="p-10">
    <div class="text-center">
      <div class="flex justify-center items-center flex-col">
        <div class="relative">
          <PlayerCard :player="president" size="xl"/>
        </div>
        <p class="text-base text-gray-700">Choosing who to <span class="font-bold">{{ actionName }}</span></p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {
  ActionChoice,
  type AwaitingPresidentActionChoiceInnerGameState,
  type InProgressGameState
} from '@/game/state.ts';
import { computed } from 'vue';
import PlayerCard from '@/components/ui/PlayerCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingPresidentActionChoiceInnerGameState);

const president = computed(() => props.game.players.find(p => p.is_president)!);
const actionName = computed(() => {
  switch (gameState.value.action) {
    case ActionChoice.INVESTIGATE:
      return 'INVESTIGATE';
    case ActionChoice.KILL:
      return 'KILL';
    case ActionChoice.NOMINATE:
      return 'NOMINATE';
    case ActionChoice.NOMINATE_PRESIDENT:
      return 'NOMINATE PRESIDENT';
  }
});
</script>