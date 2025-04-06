<template>
  <div class="p-8 max-w-md">
    <div class="text-center flex flex-col items-center">
    <div class="relative mb-3">
        <PlayerCard :player="president" size="lg"/>
      </div>
      <p class="text-base text-gray-700">Choosing a card to remove</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { AwaitingPresidentCardDiscardInnerGameState, InProgressGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerCard from '@/components/ui/PlayerCard.vue';

const props = defineProps<{ game: InProgressGameState }>();
const gameState = computed(() => props.game.inner_game_state as AwaitingPresidentCardDiscardInnerGameState);

const president = computed(() => props.game.players.find(p => p.is_president)!);
</script>