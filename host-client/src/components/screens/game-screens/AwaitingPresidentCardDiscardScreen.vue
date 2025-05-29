<template>
  <div class="flex flex-col items-center">
    <div class="flex justify-center items-center flex-col">
      <div class="relative">
        <PlayerPreview :player="president" size="2xl"/>
      </div>
      <p class="text-gray-700 font-amazonia font-light text-center text-2xl">Choosing two cards to send to the
        advisor</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingPresidentCardDiscardInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingPresidentCardDiscardInnerGameState);

const president = computed(() => props.game.players.find(p => p.is_president)!);
</script>