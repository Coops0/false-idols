<template>
  <div class="flex flex-col items-center">
    <div class="flex justify-center items-center flex-col">
      <div class="relative">
        <PlayerPreview :player="president" size="2xl"/>
      </div>
      <p class="text-gray-700 font-amazonia font-light text-center text-3xl">Choosing who to <span
          class="!font-bold font-stretch-expanded">{{ actionName }}</span></p>
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
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

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

  return '???';
});
</script>